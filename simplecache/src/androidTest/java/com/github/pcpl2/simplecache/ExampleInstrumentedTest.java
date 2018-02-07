package com.github.pcpl2.simplecache;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.github.pcpl2.simplecache.test", appContext.getPackageName());

        CacheManagerImpl cacheManager = new CacheManagerImpl();
        cacheManager.init(appContext, null);

        cacheManager.addCache("String", "Hello World");
        cacheManager.addCache("Int", 255);
        cacheManager.addCache("Bool", false);
        cacheManager.addCache("float", 5.55);
        Map<String, String> testmap = new HashMap<>();
        testmap.put("testKey", "TestValue");
        testObject object = new testObject(69, "abababa", false, 6.66f, testmap);
        cacheManager.addCache("map", testmap);
        cacheManager.addCache("obj", object);
        cacheManager.addCache("obj2", object);
        assertEquals("com.github.pcpl2.simplecache.test", appContext.getPackageName());
    }
}

class testObject {
    private int test;
    private String test1;
    private boolean test2;
    private float test3;
    private Map<String, String> test4;

    public testObject(int test, String test1, boolean test2, float test3, Map<String, String> test4) {
        this.test = test;
        this.test1 = test1;
        this.test2 = test2;
        this.test3 = test3;
        this.test4 = test4;
    }

    @Override
    public String toString() {
        return "testObject{" +
                "test=" + test +
                ", test1='" + test1 + '\'' +
                ", test2=" + test2 +
                ", test3=" + test3 +
                ", test4=" + test4 +
                '}';
    }
}
