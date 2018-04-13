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

    fun addToCache(key: String, value: Any) {
        backgroundSaveFileThread?.join()
        val cacheEntry = CacheEntry(ts = DateTime.now(), value = value, type = value.javaClass.name)
        cahceMap[key] = cacheEntry
        updateCacheFile()
    }

    fun getFromCache(key: String, checkExpired: Boolean = true, callback: (value: Any?, type: Class<*>?) -> Unit) {
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
                if(!json.isNullOrEmpty()) {
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
            val hours = Hours.hoursBetween(cahceMap[key]?.ts, DateTime.now())
            if (hours.hours >= 24) {
                cahceMap.remove(key)
                updateCacheFile()
            }
        }
    }
}
