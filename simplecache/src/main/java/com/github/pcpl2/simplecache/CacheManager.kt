package com.github.pcpl2.simplecache

import android.content.Context
import java.io.File

object CacheManager {
    const val directoryName = "SimpleCache"

    private val instancesActive: MutableMap<String, CacheManagerImpl> = mutableMapOf()

    /**
     * Getting list of cache files.
     *
     * @param context System context
     *
     * @return List of cache file names.
     */
    fun getListOfCacheFiles(context: Context): List<String> {
        val directory = File(context.cacheDir, directoryName)
        return directory.listFiles().map { it.name }
    }

    /**
     * Create global instance of cache manager.
     *
     * @param context System context.
     * @param instanceName Name of instance for getting him.
     * @param fileName Cache file name. Default is CacheBase
     * @param autoSave Autosave active status. Default is true
     *
     * @return Cache manager instance.
     */
    fun createGlobalInstance(context: Context, instanceName: String, fileName: String = "CacheBase", autoSave: Boolean = true): CacheManagerImpl {
        val instance = CacheManagerImpl(context, fileName, autoSave)

        instancesActive[instanceName] = instance
        return instance
    }

    /**
     * Getting global instance by instance name.
     *
     * @param instanceName Name of instance.
     *
     * @return Cache manager instance or null if not exist.
     */
    fun getGlobalInstance(instanceName: String): CacheManagerImpl? {
        return instancesActive[instanceName]
    }

    /**
     * Getting list of active instance names.
     *
     * @return List of active instance names.
     */
    fun getListOfGlobalInstanceNames() : List<String> {
        return instancesActive.keys.toList()
    }

    /**
     * Remove global instance.
     *
     * @param instanceName Name of instance.
     */
    fun removeGlobalInstance(instanceName: String) {
        instancesActive.remove(instanceName)
    }

    /**
     * Remove all active global instances.
     */
    fun removeAllGlobalInstances() {
        instancesActive.clear()
    }

    /**
     * Create local instance of cache manager.
     *
     * @param context System context.
     * @param fileName Cache file name. Default is CacheBase
     * @param autoSave Autosave active status. Default is true
     *
     * @return Cache manager instance.
     */
    fun createInstance(context: Context, fileName: String = "CacheBase", autoSave: Boolean = true): CacheManagerImpl {
        return CacheManagerImpl(context, fileName, autoSave)
    }

}