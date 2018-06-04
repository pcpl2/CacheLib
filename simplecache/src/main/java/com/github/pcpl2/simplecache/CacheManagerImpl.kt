package com.github.pcpl2.simplecache

import android.os.Process
import android.content.Context
import android.util.Log
import com.github.pcpl2.simplecache.models.CacheEntry
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import org.joda.time.Seconds
import org.joda.time.format.ISODateTimeFormat
import java.io.*
import java.lang.reflect.Type

/**
 * Created by patry on 29.01.2018.
 */
class CacheManagerImpl(private val context: Context, fileName: String?) {
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

    private var filename = fileName ?: "CacheBase"

    private val gson = GsonBuilder().registerTypeAdapter(DateTime::class.java, JodaDateTimeTypeAdapter()).create()

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
        val cacheEntry = CacheEntry(ts = DateTime.now(), lifeTime = lifeTime, value = value, type = value.javaClass.name)
        cahceMap[key] = cacheEntry
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
            val classType = Class.forName(entry.type)
            val valueTyped = classType.cast(entry.value)
            if (checkExpired) {
                val removed = checkDateOfCache(key = key, entry = entry)
                if (!removed) {
                    success(valueTyped, classType)
                } else {
                    if(error != null) {
                        error()
                    }
                }
            } else {
                success(valueTyped, classType)
            }
        } else {
            if(error != null) {
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
            updateCacheFile()
        }
    }

    /**
     * Removing all elements form cache.
     */
    fun removeAllElements() {
        backgroundReadFileThread?.join()
        cahceMap.clear()
        updateCacheFile()
    }

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
                    val obj = gson.fromJson<Map<String, CacheEntry>>(json, cacheEntryType)
                    cahceMap.clear()
                    cahceMap.putAll(obj)
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
                updateCacheFile()
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    private fun createDirectory() {
        File(context.cacheDir, CacheManager.directoryName).mkdirs()
    }
}
