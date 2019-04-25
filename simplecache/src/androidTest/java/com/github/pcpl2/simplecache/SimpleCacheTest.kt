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
class SimpleCacheTest {

    private val appContext = InstrumentationRegistry.getTargetContext()!!

    @Test
    @Throws(Exception::class)
    fun cacheWithAutoSaveTest() {
        val cacheManager = CacheManager.createInstance(context = appContext)

        cacheManager.set("String", "Hello World")
        cacheManager.set("Int", 255)
        cacheManager.set("Bool", false)
        cacheManager.set("float", 5.55)
        val testMap = HashMap<String, String>()
        testMap["testKey"] = "TestValue"
        cacheManager.set("map", testMap)
        val testObject = TestObject(69, "Test String", false, 6.66f, testMap)
        cacheManager.set("obj", testObject)
        val testObject2 = TestObject(885, "Testing two!", true, 3.14f, testMap)

        cacheManager.set("obj2", testObject2)

        cacheManager.get(key = "String", success = { value, type ->
            assertEquals("Hello World", value)
            assert(type.isInstance(String::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "Int", checkExpired = true, success = { value, type ->
            assertEquals(255, value)
            assert(type.isInstance(Int::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "Bool", success = { value, type ->
            assertEquals(false, value)
            assert(type.isInstance(Boolean::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "float", success = { value, type ->
            assertEquals(5.55, value)
            assert(type.isInstance(Float::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "map", success = { value, type ->
            assertEquals(testMap, value)
            assert(type.isInstance(HashMap::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "obj", success = { value, type ->
            assertEquals(testObject, value)
            assert(type.isInstance(TestObject::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "obj2", success = { value, type ->
            assertEquals(testObject2, value)
            assert(type.isInstance(TestObject::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.remove("obj2")

        cacheManager.get(key = "obj2", success = { _,  _ ->

        }, error = {
            Log.d("simpleCacheTest", "obj2 is not exist.")
            assert(true)
        })

        cacheManager.removeAllElements()
    }

    @Test
    @Throws(Exception::class)
    fun cacheWithoutAutoSaveTest() {
        val cacheManager = CacheManager.createInstance(context = appContext, fileName = "NoAutoSave", autoSave = false)

        cacheManager.set("String", "Hello World")
        cacheManager.set("Int", 255)
        cacheManager.set("Bool", false)
        cacheManager.set("float", 5.55)
        val testMap = HashMap<String, String>()
        testMap["testKey"] = "TestValue"
        cacheManager.set("map", testMap)
        val testObject = TestObject(69, "Test String", false, 6.66f, testMap)
        cacheManager.set("obj", testObject)
        val testObject2 = TestObject(885, "Testing two!", true, 3.14f, testMap)

        cacheManager.set("obj2", testObject2)

        cacheManager.save()

        cacheManager.get(key = "String", success = { value, type ->
            assertEquals("Hello World", value)
            assert(type.isInstance(String::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "Int", checkExpired = true, success = { value, type ->
            assertEquals(255, value)
            assert(type.isInstance(Int::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "Bool", success = { value, type ->
            assertEquals(false, value)
            assert(type.isInstance(Boolean::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "float", success = { value, type ->
            assertEquals(5.55, value)
            assert(type.isInstance(Float::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "map", success = { value, type ->
            assertEquals(testMap, value)
            assert(type.isInstance(HashMap::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "obj", success = { value, type ->
            assertEquals(testObject, value)
            assert(type.isInstance(TestObject::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.get(key = "obj2", success = { value, type ->
            assertEquals(testObject2, value)
            assert(type.isInstance(TestObject::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.remove("obj2")

        cacheManager.get(key = "obj2", success = { _,  _ ->

        }, error = {
            Log.d("simpleCacheTest", "obj2 is not exist.")
            assert(true)
        })

        cacheManager.removeAllElements()
    }

    @Test
    @Throws(Exception::class)
    fun cacheWithAutoSaveAsyncTest() {
        val cacheManager = CacheManager.createInstance(context = appContext, fileName = "autoSAveAsync")

        cacheManager.setAsync("String", "Hello World")
        cacheManager.setAsync("Int", 255)
        cacheManager.setAsync("Bool", false)
        cacheManager.setAsync("float", 5.55)
        val testMap = HashMap<String, String>()
        testMap["testKey"] = "TestValue"
        cacheManager.setAsync("map", testMap)
        val testObject = TestObject(69, "Test String", false, 6.66f, testMap)
        cacheManager.setAsync("obj", testObject)
        val testObject2 = TestObject(885, "Testing two!", true, 3.14f, testMap)

        cacheManager.setAsync("obj2", testObject2)

        cacheManager.getAsync(key = "String", success = { value, type ->
            assertEquals("Hello World", value)
            assert(type.isInstance(String::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.getAsync(key = "Int", checkExpired = true, success = { value, type ->
            assertEquals(255, value)
            assert(type.isInstance(Int::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.getAsync(key = "Bool", success = { value, type ->
            assertEquals(false, value)
            assert(type.isInstance(Boolean::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.getAsync(key = "float", success = { value, type ->
            assertEquals(5.55, value)
            assert(type.isInstance(Float::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.getAsync(key = "map", success = { value, type ->
            assertEquals(testMap, value)
            assert(type.isInstance(HashMap::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.getAsync(key = "obj", success = { value, type ->
            assertEquals(testObject, value)
            assert(type.isInstance(TestObject::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.getAsync(key = "obj2", success = { value, type ->
            assertEquals(testObject2, value)
            assert(type.isInstance(TestObject::class))
            Log.d("simpleCacheTest", value.toString())
        })

        cacheManager.remove("obj2")

        cacheManager.getAsync(key = "obj2", success = { _,  _ ->

        }, error = {
            Log.d("simpleCacheTest", "obj2 is not exist.")
            assert(true)
        })

        cacheManager.removeAllElements()
    }

    @Test
    fun cacheListFilesTest() {
        val files = CacheManager.getListOfCacheFiles(appContext)
        Log.d("simpleCacheTest",files.toString())

        assert(files.size == 2)
    }

    @Test
    fun globalInstancesTest() {
        val instanceName1 = "Instance1"
        val instanceName2 = "Instance2"

        val instance1 = CacheManager.createGlobalInstance(context = appContext, instanceName = instanceName1, fileName = "instanceFile1")
        val instance2 = CacheManager.createGlobalInstance(context = appContext, instanceName = instanceName2, fileName = "InstanceFile2")

        val globalInstance1 = CacheManager.getGlobalInstance(instanceName1)
        val globalInstance2 = CacheManager.getGlobalInstance(instanceName2)

        assert(instance1 == globalInstance1)
        assert(instance1 != globalInstance2)
        assert(instance2 == globalInstance2)

        var listOfIInstancesNames = CacheManager.getListOfGlobalInstanceNames()

        assert(listOfIInstancesNames.contains(instanceName2))

        CacheManager.removeGlobalInstance(instanceName1)

        listOfIInstancesNames = CacheManager.getListOfGlobalInstanceNames()

        assert(!listOfIInstancesNames.contains(instanceName1))

        CacheManager.removeAllGlobalInstances()

        listOfIInstancesNames = CacheManager.getListOfGlobalInstanceNames()

        assert(listOfIInstancesNames.isEmpty())
    }
}
