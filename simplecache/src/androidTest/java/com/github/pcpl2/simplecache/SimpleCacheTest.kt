package com.github.pcpl2.simplecache

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
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
class SimpleCacheTest {
    @Test
    @Throws(Exception::class)
    fun simpleCacheTest() {

        val appContext = InstrumentationRegistry.getTargetContext()

        val cacheManager = CacheManager.createInstance(appContext, null)

        cacheManager.add("String", "Hello World")
        cacheManager.add("Int", 255)
        cacheManager.add("Bool", false)
        cacheManager.add("float", 5.55)
        val testMap = HashMap<String, String>()
        testMap["testKey"] = "TestValue"
        cacheManager.add("map", testMap)
        val testObject = TestObject(69, "abababa", false, 6.66f, testMap)
        cacheManager.add("obj", testObject)
        val testObject2 = TestObject(885, "Testing two!", true, 3.14f, testMap)

        cacheManager.add("obj2", testObject2)

        cacheManager.get(key = "String", success = { value, type ->
            assertEquals("Hello World", value)
            assert(type!!.isInstance(String::class))
            System.out.println(value.toString())
        })

        cacheManager.get(key = "Int", success = { value, type ->
            assertEquals(255, value)
            assert(type!!.isInstance(Int::class))
            System.out.println(value.toString())
        })

        cacheManager.get(key = "Bool", success = { value, type ->
            assertEquals(false, value)
            assert(type!!.isInstance(Boolean::class))
            System.out.println(value.toString())
        })

        cacheManager.get(key = "float", success = { value, type ->
            assertEquals(5.55, value)
            assert(type!!.isInstance(Float::class))
            System.out.println(value.toString())
        })

        cacheManager.get(key = "map", success = { value, type ->
            assertEquals(testMap, value)
            assert(type!!.isInstance(HashMap::class))
            System.out.println(value.toString())
        })

        cacheManager.get(key = "obj", success = { value, type ->
            assertEquals(testObject, value)
            assert(type!!.isInstance(TestObject::class))
            System.out.println(value.toString())
        })

        cacheManager.get(key = "obj2", success = { value, type ->
            assertEquals(testObject2, value)
            assert(type!!.isInstance(TestObject::class))
            System.out.println(value.toString())
        })

        cacheManager.remove("obj2")

        cacheManager.get(key = "obj2", success = { _,  _ ->

        }, error = {
            System.out.println("obj2 is not exist.")
            assert(true)
        })

        System.out.println(CacheManager.getListOfCacheFiles(appContext).toString())

        assertEquals("com.github.pcpl2.simplecache.test", appContext.packageName)
    }
}
