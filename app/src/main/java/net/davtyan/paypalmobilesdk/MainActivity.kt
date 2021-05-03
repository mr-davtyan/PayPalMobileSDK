package net.davtyan.paypalmobilesdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.davtyan.paypalmobilesdk.databinding.ActivityMainBinding
import net.davtyan.paypalmobilesdk.items.Cart
import net.davtyan.paypalmobilesdk.items.ItemAdapter
import net.davtyan.paypalmobilesdk.items.Items

private const val CHECKOUT_ACTIVITY_RESULT_CODE = 13254214

class MainActivity : AppCompatActivity() {

    private lateinit var checkoutButton: Button
    private lateinit var clearButton: Button

    private lateinit var cartInfoText: TextView

    private lateinit var myCart: Cart

    private var totalItemsInCart: Int = 0
    private var totalPriceInCart: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartInfoText = findViewById(R.id.cartText)

        checkoutButton = findViewById(R.id.buttonCheckout)
        checkoutButton.setOnClickListener {
            val checkoutIntent = Intent(this, Checkout()::class.java)
            checkoutIntent.putExtra("items", totalItemsInCart)
            checkoutIntent.putExtra("price", totalPriceInCart)
            startActivityForResult(checkoutIntent, CHECKOUT_ACTIVITY_RESULT_CODE)
        }

        //  creating empty cart
        myCart = Cart()

        //  button. wiping cart
        clearButton = findViewById(R.id.buttonClearCart)
        clearButton.setOnClickListener {
            myCart.wipe()
            updateCart()
        }

        //  loading dummy items for purchase
        val myItems = Items(this)
        binding.listView.adapter =
            ItemAdapter(this, myItems.myItemsId, myItems.myItems)
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            myCart.addItem(myItems.myItems[position])  // adding an item in the cart and refresh
            updateCart()
        }

        //  refreshing the cart. empty on this point
        updateCart()
    }


    //  refreshing the cart text view, calculating total price for checkout
    private fun updateCart() {
        val (f, s) = myCart.updateCart()
        totalItemsInCart = f
        totalPriceInCart = s

        var cartText = getString(R.string.cart_is_empty)

        if (totalPriceInCart > 0) {
            checkoutButton.isEnabled = true
            clearButton.isEnabled = true

            cartText = totalItemsInCart.toString()
            cartText += (if (totalItemsInCart > 1) " items  " else " item  ")
            (getString(R.string.text_usd) + String.format(
                "%.2f",
                0.01 * totalPriceInCart
            )).also { cartText += it }

        } else {
            checkoutButton.isEnabled = false
            clearButton.isEnabled = false
        }
        cartInfoText.text = cartText
    }


    //  checking if the order completed successful
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECKOUT_ACTIVITY_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            myCart.wipe()
            updateCart()
            Toast.makeText(
                this, "Order Completed!",
                Toast.LENGTH_LONG
            ).show()
        }
    }


}