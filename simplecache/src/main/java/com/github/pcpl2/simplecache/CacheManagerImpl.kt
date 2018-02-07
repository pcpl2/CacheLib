package com.github.pcpl2.simplecache

import android.os.Process
import android.content.Context
import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import org.joda.time.Hours
import org.joda.time.format.ISODateTimeFormat
import java.io.*
import java.lang.reflect.Type

/**
 * Created by patry on 29.01.2018.
 */
class CacheManagerImpl {
    data class CacheEntry(val ts: DateTime, val value: Any, val type: String)

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

    private var context: Context? = null

    private var filename = "CacheBase"

    private val gson = GsonBuilder().registerTypeAdapter(DateTime::class.java, JodaDateTimeTypeAdapter()).create()

    private val cahceMap: MutableMap<String, CacheEntry> = mutableMapOf()

    private var backgroundSaveFileThread: Thread? = null
    private var backgroundReadFileThread: Thread? = null

    fun init(context: Context, fileName: String?) {
        this.context = context.applicationContext
        if (fileName != null) {
            this.filename = fileName
        }
        readCacheFile()
    }

    fun <T> addCache(key: String, value: Any) {
        backgroundSaveFileThread?.join()
        val cacheEntry = CacheEntry(ts = DateTime.now(), value = value, type = (value.javaClass.toString()) )
        cahceMap[key] = cacheEntry
        updateCacheFile()
    }

/*    fun <T> getFromCache(key: String, callback: CachceManagerGetData) {
        backgroundReadFileThread?.join()
        //checkDateOfCache(serviceId = key)
        callback.onComplete(cahceMap[key]!! as T)
    }*/

    private fun updateCacheFile() {
        backgroundSaveFileThread?.join()
        backgroundSaveFileThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            val json = gson.toJson(cahceMap)
            Log.d("SaveJson", json)
            val file = File(context?.cacheDir, filename)
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
            val file = File(context?.cacheDir, filename)
            if (file.exists()) {
                val fr = FileReader(file.absoluteFile)
                val json = BufferedReader(fr).readLine()
                fr.close()
                val cacheEntryType = object : TypeToken<Map<String, CacheEntry>>() {}.type
                val obj = gson.fromJson<Map<String, CacheEntry>>(json, cacheEntryType)
                cahceMap.clear()
                cahceMap.putAll(obj)
            }
        })
        backgroundReadFileThread?.start()
    }

    /*
    private fun checkDateOfCache(serviceId: String) {
        if (cahceMap.contains(serviceId)) {
            val hours = Hours.hoursBetween(cahceMap[serviceId]?.ts, DateTime.now())
            if (hours.hours >= 24) {
                cahceMap.remove(serviceId)
                updateCacheFile()
            }
        }
    }*/
}