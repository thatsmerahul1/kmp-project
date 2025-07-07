package com.weather

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Basic test to verify the build system is working
 */
class BasicBuildTest {
    
    @Test
    fun testBasicKotlinTest() {
        assertTrue(true, "Basic test should pass")
    }
    
    @Test
    fun testStringConcatenation() {
        val result = "Hello" + " " + "World"
        assertTrue(result == "Hello World", "String concatenation should work")
    }
    
    @Test
    fun testListOperations() {
        val list = listOf(1, 2, 3, 4, 5)
        assertTrue(list.size == 5, "List should have 5 elements")
        assertTrue(list.sum() == 15, "Sum should be 15")
    }
}