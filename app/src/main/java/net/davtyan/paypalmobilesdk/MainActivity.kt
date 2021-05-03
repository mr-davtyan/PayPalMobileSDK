package net.davtyan.paypalmobilesdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PayPalButton
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

        //  pre-loading PP resources. not necessary, but speeds up rendering the PP button on checkout
        payPalPreLoad(payPalButton = findViewById(R.id.payPalButtonPreLoad))    //  the button is also invisible

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


    //  pre-loading PP resources. not necessary, but speeds up rendering the PP button on checkout
    private fun payPalPreLoad(payPalButton: PayPalButton) {
        val config = CheckoutConfig(
            application = application,
            clientId = BuildConfig.PP_CLIENT_ID,  //ID from secrets.properties
            environment = Environment.SANDBOX,
            returnUrl = "${applicationContext.packageName}://paypalpay",  //return URL. Use the same in PP SDK console
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)
        payPalButton.setup(
            createOrder = CreateOrder { createOrderActions ->
                val order = Order(
                    intent = OrderIntent.CAPTURE,
                    appContext = AppContext(
                        userAction = UserAction.PAY_NOW
                    ),
                    purchaseUnitList = listOf(
                        PurchaseUnit(
                            amount = Amount(
                                currencyCode = CurrencyCode.USD,
                                value = "0"
                            )
                        )
                    )
                )
                createOrderActions.create(order)
            }
        )
    }


}