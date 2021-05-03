package net.davtyan.paypalmobilesdk.items

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import net.davtyan.paypalmobilesdk.R

//adapter for the items in the store. filling the list
class ItemAdapter (
    private val activity: Activity,
    itemId: MutableList<String>,
    private val item: MutableList<Item>,
) : ArrayAdapter<String>(activity, R.layout.item, itemId) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        var rowView = view
        if (rowView == null) {
            val inflater = activity.layoutInflater
            rowView = inflater.inflate(R.layout.item, parent, false)
        }

        val itemPicImage = rowView?.findViewById(R.id.item_pic) as ImageView
        val itemInfoText = rowView.findViewById(R.id.item_info) as TextView
        val itemPriceText = rowView.findViewById(R.id.item_price) as TextView

        itemPicImage.setImageDrawable(item[position].itemPic)
        itemInfoText.text = item[position].itemInfo
        ("$" + String.format("%.2f", 0.01 * item[position].itemPrice)).also { itemPriceText.text = it }

        return rowView
    }
}