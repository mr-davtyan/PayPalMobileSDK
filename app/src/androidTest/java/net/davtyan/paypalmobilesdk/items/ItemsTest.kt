package net.davtyan.paypalmobilesdk.items

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

import org.junit.Assert.*

class ItemsTest {
    private val tNumberOfItems = 10

    @Test
    fun getMyItemsSize() {
        val tItems = Items(InstrumentationRegistry.getInstrumentation().targetContext, tNumberOfItems)
        assertEquals(tNumberOfItems, tItems.myItems.size)
    }

    @Test
    fun getMyItemsInfoIsExist() {
        val tItems = Items(InstrumentationRegistry.getInstrumentation().targetContext, tNumberOfItems)
        assertEquals(false, tItems.myItems[0].itemInfo.isNullOrEmpty())
    }

    @Test
    fun getMyItemsPriceRange() {
        val tItems = Items(InstrumentationRegistry.getInstrumentation().targetContext, tNumberOfItems)
        assertEquals(true, (tItems.myItems[0].itemPrice in 1..620000))
    }


}