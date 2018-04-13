package com.github.pcpl2.simplecache

import org.joda.time.DateTime

data class CacheEntry(val ts: DateTime, val value: Any, val type: String)
