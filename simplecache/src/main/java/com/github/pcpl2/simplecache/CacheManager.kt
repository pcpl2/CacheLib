package com.github.pcpl2.simplecache

import android.content.Context
import java.io.File

object CacheManager {
    const val directoryName = "SimpleCache"

    fun getListOfCacheFiles(ctx: Context): List<String> {
        val directory = File(ctx.cacheDir, directoryName)
        return directory.listFiles().map { it.name }
    }

    fun createInstance(context: Context, fileName: String?, autoSave: Boolean = true): CacheManagerImpl {
        return CacheManagerImpl(context, fileName, autoSave)
    }

}