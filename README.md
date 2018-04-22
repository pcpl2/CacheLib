# SimpleCache 
[![Build Status](https://travis-ci.org/pcpl2/CacheLib.svg?branch=master)](https://travis-ci.org/pcpl2/CacheLib) 
[![SimpleCache](https://api.bintray.com/packages/pcpl2/maven/simplecache/images/download.svg) ](https://bintray.com/pcpl2/maven/simplecache/_latestVersion)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=plastic)](https://android-arsenal.com/api?level=21)

A simple library for saving data in the cache and reading them.

# Setup
The library is hosted on jcenter. To use it, add the following to your module level `build.gradle` file's dependencies:

```gradle
dependencies {
    implementation 'com.github.pcpl2:simplecache:1.0.0'
}
```

# Basic usage

**To init instance of cache manager without filename:**

```kotlin
val cacheManager = CacheManager.createInstance(appContext, null) 
```
The `cacheManager` instance usage default cache file.

**To init instance of cache manager with filename:**

```kotlin
val cacheManager = CacheManager.createInstance(appContext, "filesCache")
```
The `cacheManager` instance usage cache with file name `filesCache`.


## Add elements to cache: 
The `add` function accepts 3 parameters: `key: Stirng, value: Any, lifetime: Long`.

The lifetime parameter is the lifetime in seconds and is optional, default setted to 0 (no lifetime).

**To add element to cache instance with set lifetime:**

```kotlin
cacheManager.add("HelloWorldKey", "Hello World", 60)
cacheManager.add("IntValueKey", 255, 30)
cacheManager.add("BooleanKey", false, 60)
cacheManager.add("FloatKey", 5.55, 60)
```

**To add element to cache instance without set lifetime:**

```kotlin
cacheManager.add("HelloWorldKey", "Hello World")
cacheManager.add("IntValueKey", 255)
cacheManager.add("BooleanKey", false)
cacheManager.add("FloatKey", 5.55)
```

## Getting element from cache: 
The `get` function accepts 3 parameters: `key: Stirng, checkExpired: Any, callback:  (value: Any?, type: Class<*>?) -> Unit`.

The checkExpired parameter is optional, default setted as true.

In lambda callback `value` is a nullable any object and `type` is a nullable java Class.

**To get element from cache instance with lifetime check:**

```kotlin
cacheManager.get("HelloWorldKey") { value, type ->
    System.out.println(value.toString())
}

cacheManager.get("IntValueKey") { value, type ->
    System.out.println(value.toString())
}

cacheManager.get("BooleanKey") { value, type ->
    System.out.println(value.toString())
}

cacheManager.get("FloatKey") { value, type ->
    System.out.println(value.toString())
}
```

**To get element from cache instance without lifetime check:**

```kotlin
cacheManager.get("HelloWorldKey", false) { value, type ->
    System.out.println(value.toString())
}

cacheManager.get("IntValueKey", false) { value, type ->
    System.out.println(value.toString())
}

cacheManager.get("BooleanKey", false) { value, type ->
   System.out.println(value.toString())
}

cacheManager.get("FloatKey", false) { value, type ->
    System.out.println(value.toString())
}
```

## Remove element from cache
The `remove` function accept 1 parameter: `key: Stirng`.


**To remove element from cache instance:**
```kotlin
cacheManager.remove("FloatKey")
```

## Clear cache instance
The `removeAllElements` function cleans the entire cache.

**To clear cachce istance:**

```kotlin
cacheManager.removeAllElements()
```

## List of the cahce files.
The `getListOfCacheFiles` function accept 1 parameter: `ctx: Context` and return list of exist cache files.

**To get cahce files:**

```kotlin
CacheManager.getListOfCacheFiles(appContext)
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