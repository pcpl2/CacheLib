package com.github.pcpl2.simplecache

import android.os.Process
import android.content.Context
import com.github.pcpl2.simplecache.models.CacheEntry
import com.github.pcpl2.simplecache.models.ValueObject
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import org.joda.time.Seconds
import org.joda.time.format.ISODateTimeFormat
import java.io.*
import java.lang.reflect.Type
import com.google.gson.JsonElement


/**
 * Created by patry on 29.01.2018.
 */
class CacheManagerImpl(private val context: Context, fileName: String?, private val autoSave: Boolean = true) {
    internal class JodaDateTimeTypeAdapter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type,
                                 context: JsonDeserializationContext): DateTime {
            return DateTime.parse(json.asString)
        }

        override fun serialize(src: DateTime, typeOfSrc: Type,
                               context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(ISODateTimeFormat
                    .dateTimeNoMillis()
                    .print(src))
        }
    }

    internal class ValueTypeAdapter : JsonDeserializer<ValueObject> {
        private val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, JodaDateTimeTypeAdapter())
                .create()

        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type,
                                 context: JsonDeserializationContext): ValueObject {
            val entries = json.asJsonObject.entrySet()
            val entryType = entries.find { it.key == "type" }
                    ?: throw JsonParseException("type field is not exist")
            val entryValue = entries.find { it.key == "value" }
                    ?: throw JsonParseException("value field is not exist")
            val classType = Class.forName(entryType.value.asString)
            val value = gson.fromJson(entryValue.value, classType)

            return ValueObject(value, entryType.value.asString)
        }

    }

    private var filename = fileName ?: "CacheBase"

    private val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaDateTimeTypeAdapter())
            .registerTypeAdapter(ValueObject::class.java, ValueTypeAdapter())
            .create()

    private val cahceMap: MutableMap<String, CacheEntry> = mutableMapOf()

    private var backgroundSaveFileThread: Thread? = null
    private var backgroundReadFileThread: Thread? = null

    init {
        createDirectory()
        readCacheFile()
    }

    /**
     * Add or update element in cache.
     *
     * @param key The key under which the added element will be available.
     * @param value Element that is added. can be of any type.
     * @param lifeTime Element lifetime in cache (given in seconds). If it is zero then there is no life time.
     */
    fun set(key: String, value: Any, lifeTime: Long = 0) {
        backgroundSaveFileThread?.join()
        val cacheEntry = CacheEntry(ts = DateTime.now(), lifeTime = lifeTime, value = ValueObject(value = value, type = value.javaClass.name))
        cahceMap[key] = cacheEntry
        if (autoSave)
            updateCacheFile()
    }

    /**
     * Getting element form cache if exist.
     *
     * @param key The key under which the cache element was saved.
     * @param checkExpired Checking if the lifetime of the element has been exceeded.
     * @param success Callback returning element and element type from cache. If it does not exist, the element and type returned are null.
     * @param error Callback running if element with key not exist in map or lifetime of element is end.
     */
    fun get(key: String, checkExpired: Boolean = true, success: (value: Any, type: Class<*>) -> Unit, error: (() -> Unit)? = null) {
        backgroundReadFileThread?.join()

        val entry = cahceMap[key]
        if (entry != null) {
            val classType = Class.forName(entry.value.type)
            val valueTyped = classType.cast(entry.value.value)

            if (checkExpired) {
                val removed = checkDateOfCache(key = key, entry = entry)
                if (!removed) {
                    success(valueTyped, classType)
                } else {
                    if (error != null) {
                        error()
                    }
                }
            } else {
                success(valueTyped, classType)
            }
        } else {
            if (error != null) {
                error()
            }
        }
    }

    /**
     * Removing element from cache if exist.
     *
     * @param key The key under which the cache element was saved.
     */
    fun remove(key: String) {
        backgroundReadFileThread?.join()
        if (cahceMap.containsKey(key = key)) {
            cahceMap.remove(key)
            if (autoSave)
                updateCacheFile()
        }
    }

    /**
     * Removing all elements form cache.
     */
    fun removeAllElements() {
        backgroundReadFileThread?.join()
        cahceMap.clear()
        if (autoSave)
            updateCacheFile()
    }

    /**
     * Save elements to disk, works only if autosave is false.
     */
    fun save() {
        backgroundReadFileThread?.join()
        if (!autoSave)
            updateCacheFile()
    }

    /**
     * Update cache data in json file.
     */
    private fun updateCacheFile() {
        backgroundSaveFileThread?.join()
        backgroundSaveFileThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            val json = gson.toJson(cahceMap.toMap())
            //Log.d("SaveJson", json)
            val file = File(context.cacheDir, "${CacheManager.directoryName}${File.separator}$filename")
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(json)
            bw.close()
        })
        backgroundSaveFileThread?.start()
    }

    /**
     * Load cache data from json file.
     */
    private fun readCacheFile() {
        backgroundReadFileThread?.join()
        backgroundReadFileThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            val file = File(context.cacheDir, "${CacheManager.directoryName}${File.separator}$filename")
            if (file.exists()) {
                val fr = FileReader(file.absoluteFile)
                val json = BufferedReader(fr).readLine()
                fr.close()
                if (!json.isNullOrEmpty()) {
                    val cacheEntryType = object : TypeToken<Map<String, CacheEntry>>() {}.type
                    val objs = gson.fromJson<Map<String, CacheEntry>>(json, cacheEntryType)
                    cahceMap.clear()
                    cahceMap.putAll(objs)
                }
            }
        })
        backgroundReadFileThread?.start()
    }

    /**
     * Check and remove element from map if lifetime is end.
     *
     * @param key key of element in map.
     * @param entry entry element from map.
     *
     * @return boolean of is removed or not. Return true if removed from map.
     */
    private fun checkDateOfCache(key: String, entry: CacheEntry): Boolean {
        backgroundSaveFileThread?.join()

        return if (entry.lifeTime > 0) {
            val hours = Seconds.secondsBetween(entry.ts, DateTime.now())
            if (hours.seconds >= entry.lifeTime) {
                cahceMap.remove(key)
                if (autoSave)
                    updateCacheFile()
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * Create directory for cache json.
     */
    private fun createDirectory() {
        File(context.cacheDir, CacheManager.directoryName).mkdirs()
    }
}
