# SimpleCache 
[![Build Status](https://travis-ci.org/pcpl2/CacheLib.svg?branch=master)](https://travis-ci.org/pcpl2/CacheLib) 
[![SimpleCache](https://api.bintray.com/packages/pcpl2/maven/simplecache/images/download.svg) ](https://bintray.com/pcpl2/maven/simplecache/_latestVersion)
[![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![API](https://img.shields.io/badge/API-14%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-simplecache-green.svg?style=flat )]( https://android-arsenal.com/details/1/6965 )

A simple library for saving data in the cache and reading them.

# Setup
The library is hosted on jcenter. To use it, add the following to your module level `build.gradle` file's dependencies:

```gradle
dependencies {
    implementation 'com.github.pcpl2:simplecache:1.2.0'
}
```

# Basic usage

**To init instance of cache manager without filename:**

```kotlin
val cacheManager = CacheManager.createInstance(context = appContext)
```
The `cacheManager` instance usage default cache file.

**To init instance of cache manager with filename:**

```kotlin
val cacheManager = CacheManager.createInstance(context = appContext, fileName = "filesCache")
```
The `cacheManager` instance usage cache with file name `filesCache`.

**To init instance of cache manager without autosave:**
```kotlin
val cacheManager = CacheManager.createInstance(context = appContext, autoSave = false)
```
If you have deactivated the automatic saving, you must remember to save the data using the `save` method.

## Add or update elements to cache: 
The `set` function accepts 3 parameters: `key: Stirng, value: Any, lifetime: Long`.

The lifetime parameter is the lifetime in seconds and is optional, default set as 0 (no lifetime).

**To add or update element to cache instance with set lifetime:**

```kotlin
cacheManager.set("HelloWorldKey", "Hello World", 60)
cacheManager.set("IntValueKey", 255, 30)
cacheManager.set("BooleanKey", false, 60)
cacheManager.set("FloatKey", 5.55, 60)
```

**To add or update element to cache instance without set lifetime:**

```kotlin
cacheManager.set("HelloWorldKey", "Hello World")
cacheManager.set("IntValueKey", 255)
cacheManager.set("BooleanKey", false)
cacheManager.set("FloatKey", 5.55)
```
## Save cache data 
**To save cache on disk (if autosave disabled):**

```kotlin
cacheManager.save()
```

## Getting element from cache: 
The `get` function accepts 4 parameters: `key: Stirng, checkExpired: Boolean = true, success:  (value: Any, type: Class<*>) -> Unit, error: (() -> Unit)? = null`.

The checkExpired parameter is optional, default set as true.

Callback success return value from map and value type.

Callback error is optional and run if element with key do not exist or lifetime of element is ended.

**To get element from cache instance with lifetime check:**

```kotlin
cacheManager.get(key = "HelloWorldKey", success = { value, type ->
    Log.d("simpleCacheLog", value.toString())
})

cacheManager.get(key = "IntValueKey", success = { value, type ->
    Log.d("simpleCacheLog", value.toString())
})

cacheManager.get(key = "BooleanKey", success = { value, type ->
   Log.d("simpleCacheLog", value.toString())
}, error = { 
    Log.d("simpleCacheLog", "BooleanKey is empty.")
})

cacheManager.get(key = "FloatKey", success = { value, type ->
    Log.d("simpleCacheLog", value.toString())
}, error = { 
    Log.d("simpleCacheLog", "FloatKey is empty.")
})
```

**To get element from cache instance without lifetime check:**

```kotlin
cacheManager.get(key = "HelloWorldKey", checkExpired = false, success = { value, type ->
    Log.d("simpleCacheLog", value.toString())
})

cacheManager.get(key = "IntValueKey", checkExpired = false, success = { value, type ->
    Log.d("simpleCacheLog", value.toString())
})

cacheManager.get(key = "BooleanKey", checkExpired = false, success = { value, type ->
   Log.d("simpleCacheLog", value.toString())
}, error = { 
    Log.d("simpleCacheLog", "BooleanKey is empty.")
})

cacheManager.get(key = "FloatKey", checkExpired = false, success = { value, type ->
    Log.d("simpleCacheLog", value.toString())
}, error = { 
    Log.d("simpleCacheLog", "FloatKey is empty.")
})
```

## Remove element from cache
The `remove` function accept 1 parameter: `key: Stirng`.

**To remove element from cache instance:**
```kotlin
cacheManager.remove("FloatKey")
```

## Clear cache instance

The `removeAllElements` function cleans the entire cache.

**To clear cache istance:**

```kotlin
cacheManager.removeAllElements()
```

**To remove all elements in cache istance:**

```kotlin
cacheManager.removeAllElements()
```

The `dispose` function accept 1 parameter: `save: Boolean = true`.

The save parameter is optional, default set as true.

## List of the cache files.
The `getListOfCacheFiles` function accept 1 parameter: `ctx: Context` and return list of exist cache files.

**To get cache files:**

```kotlin
CacheManager.getListOfCacheFiles(appContext)
```

# Global instances

## Create global instance 
The `createGlobalInstance` function accept 4 parameters: `context: Context, instanceName: String, fileName: String = "CacheBase", autoSave: Boolean = true` and return cache instance.

**To create global instance:**

```kotlin
CacheManager.createGlobalInstance(context = appContext, instanceName = "Instance1", fileName = "instanceFile1")
```

## Get global instance 
The `getGlobalInstance` function accept 1 parameter: `instanceName: String` and return cache instance.

**To get global instance by name:**

```kotlin
val globalInstance2 = CacheManager.getGlobalInstance(instanceName = "Instance1")
```

**To get list of global instance names:**

```kotlin
var listOfIInstancesNames = CacheManager.getListOfGlobalInstanceNames()
```

## Remove global instance
The `removeGlobalInstance` function accept 2 parameters: `instanceName: String, save: Boolean = true`.

The save parameter is optional, default set as true.

**To remove global instance by name:**

```kotlin
CacheManager.removeGlobalInstance(instanceName = "Instance1")
```

**To remove all global instances:**

```kotlin
CacheManager.removeAllGlobalInstances()
```

# Changelog
Please see the [Changelog](https://github.com/pcpl2/CacheLib/wiki/Changelog) page to see what's recently changed.


# License
```
Copyright 2018 Patryk Ławicki

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
