package com.github.pcpl2.simplecache.models

import org.joda.time.DateTime

data class CacheEntry(val ts: DateTime, val lifeTime: Long, val value: ValueObject)

data class ValueObject(val value: Any, val type: String)
