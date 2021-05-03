package net.davtyan.paypalmobilesdk.items

import android.content.Context
import java.util.*


//sample items for the store. using for the main list and the cart
class Items() {

    var myItems: MutableList<Item> = mutableListOf()
    var myItemsId: MutableList<String> = mutableListOf()

    constructor(a: Context, numberOfItems: Int = 21) : this() {

        val maxPrice = 620000

        val imageArray = a.resources.obtainTypedArray(net.davtyan.paypalmobilesdk.R.array.drawer_icons)

        val randomValues = (1..numberOfItems).toList().toTypedArray()
        randomValues.shuffle()

        for (i in randomValues) {  // equivalent of 1 <= i && i <= numberOfItems
            val iString = i.toString()
            val drawable = imageArray.getDrawable(i)
            val drawableName =
                imageArray.getString(i)?.replace("res/drawable/", "")?.replace(".png", "")?.replace("_", " ")
                    ?.capitalize(Locale.ROOT)
            myItems.add(
                Item(
                    drawable,
                    drawableName,
                    (maxPrice / i)
                )
            )
            myItemsId.add(iString)
        }
        imageArray.recycle()
    }

}