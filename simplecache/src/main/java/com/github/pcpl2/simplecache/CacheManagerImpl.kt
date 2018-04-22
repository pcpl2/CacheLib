package com.github.pcpl2.simplecache

import android.os.Process
import android.content.Context
import android.util.Log
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
     * Add element to cache.
     *
     * @param key The key under which the added element will be available.
     * @param value Element that is added. can be of any type.
     * @param lifeTime Element lifetime in cache (given in seconds). If it is zero then there is no life time.
     */
    fun add(key: String, value: Any, lifeTime: Long = 0) {
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
     * @param callback Callback returning element and element type from cache. If it does not exist, the element and type returned are null.
     */
    fun get(key: String, checkExpired: Boolean = true, callback: (value: Any?, type: Class<*>?) -> Unit) {
        backgroundReadFileThread?.join()
        if (checkExpired) {
            checkDateOfCache(key = key)
        }
        if (cahceMap.containsKey(key)) {
            val classType = Class.forName(cahceMap[key]!!.type)
            val valueTyped = classType.cast(cahceMap[key]?.value)
            callback(valueTyped, classType)
        }
    }

    /**
     * Removing element from cache if exist.
     *
     * @param key The key under which the cache element was saved.
     */
    fun remove(key: String) {
        backgroundReadFileThread?.join()
        if(cahceMap.containsKey(key = key)) {
            cahceMap.remove(key)
            updateCacheFile()
        }
    }

    /**
     * Removing all elements form cache.
     */
    private fun removeAllElements() {
        backgroundReadFileThread?.join()
        cahceMap.clear()
        updateCacheFile()
    }

    private fun updateCacheFile() {
        backgroundSaveFileThread?.join()
        backgroundSaveFileThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            val json = gson.toJson(cahceMap)
            Log.d("SaveJson", json)
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

    private fun checkDateOfCache(key: String) {
        if (cahceMap.contains(key)) {
            val element = cahceMap[key]!!
            if(element.lifeTime > 0) {
                val hours = Seconds.secondsBetween(element.ts, DateTime.now())
                if (hours.seconds >= element.lifeTime) {
                    cahceMap.remove(key)
                    updateCacheFile()
                }
            }
        }
    }

    private fun createDirectory() {
        File(context.cacheDir, CacheManager.directoryName).mkdirs()
    }
}
