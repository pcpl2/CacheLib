package com.github.pcpl2.simplecache

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.github.pcpl2.simplecache.models.TestObject

import org.junit.Test
import org.junit.runner.RunWith

import java.util.HashMap

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        val cacheManager = CacheManagerImpl()
        cacheManager.init(appContext, null)

        cacheManager.addToCache("String", "Hello World")
        cacheManager.addToCache("Int", 255)
        cacheManager.addToCache("Bool", false)
        cacheManager.addToCache("float", 5.55)
        val testMap = HashMap<String, String>()
        testMap["testKey"] = "TestValue"
        cacheManager.addToCache("map", testMap)
        val testObject = TestObject(69, "abababa", false, 6.66f, testMap)
        cacheManager.addToCache("obj", testObject)
        val testObject2 = TestObject(885, "Testing two!", true, 3.14f, testMap)
        cacheManager.addToCache("obj2", testObject2)

        cacheManager.getFromCache("String") { value, type ->
            assertEquals("Hello World", value)
            assert(type!!.isInstance(String::class))
            System.out.println(value.toString())
        }

        cacheManager.getFromCache("Int") { value, type ->
            assertEquals(255, value)
            assert(type!!.isInstance(Int::class))
            System.out.println(value.toString())
        }

        cacheManager.getFromCache("Bool") { value, type ->
            assertEquals(false, value)
            assert(type!!.isInstance(Boolean::class))
            System.out.println(value.toString())
        }

        cacheManager.getFromCache("float") { value, type ->
            assertEquals(5.55, value)
            assert(type!!.isInstance(Float::class))
            System.out.println(value.toString())
        }

        cacheManager.getFromCache("map") { value, type ->
            assertEquals(testMap, value)
            assert(type!!.isInstance(HashMap::class))
            System.out.println(value.toString())
        }

        cacheManager.getFromCache("obj") { value, type ->
            assertEquals(testObject, value)
            assert(type!!.isInstance(TestObject::class))
            System.out.println(value.toString())
        }

        cacheManager.getFromCache("obj2") { value, type ->
            assertEquals(testObject2, value)
            assert(type!!.isInstance(TestObject::class))
            System.out.println(value.toString())
        }

        assertEquals("com.github.pcpl2.simplecache.test", appContext.packageName)
    }
}
