package com.github.pcpl2.simplecache

import org.joda.time.DateTime

data class CacheEntry(val ts: DateTime, val lifeTime: Long, val value: Any, val type: String)
