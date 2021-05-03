package net.davtyan.paypalmobilesdk.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import org.junit.Assert.*
import org.junit.Test


class CartTest {

    private var tDrawable: Drawable = ColorDrawable(Color.TRANSPARENT)
    private val tItem :Item = (Item(tDrawable, "drawableTestName", 222))


    @Test
    fun wipe() {
        val tCart = Cart()
        tCart.addItem(tItem)
        tCart.wipe()
        assertEquals(0, tCart.getCartSize())
    }

    @Test
    fun addItem() {
        val tCart = Cart()
        tCart.addItem(tItem)
        assertEquals("drawableTestName", tCart.getItem(0).itemInfo)
    }

    @Test
    fun updateCart() {
        val tCart = Cart()
        tCart.addItem(tItem)
        assertEquals(Pair(1, 222), tCart.updateCart())
    }
}