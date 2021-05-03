package net.davtyan.paypalmobilesdk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PayPalButton


class Checkout : AppCompatActivity() {
    private lateinit var payPalButton: PayPalButton
    private lateinit var continueShoppingButton: Button
    private lateinit var cartInfoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

//        getting the total price for checkout
        val bundle: Bundle? = intent.extras
        val totalItemsInCart: Int = bundle!!.getInt(getString(R.string.text_items))
        val totalPriceInCart: Int = bundle.getInt(getString(R.string.text_price))

        payPalButton = findViewById(R.id.payPalButton)

//        do not order now, but continue shopping. back to the main activity
        continueShoppingButton = findViewById(R.id.cartContinueShopping)
        continueShoppingButton.setOnClickListener {
            finish()
        }

//        combining the cart description
        var cartInfoText = "\n"
        cartInfoText += totalItemsInCart.toString()
        cartInfoText += (if (totalItemsInCart > 1) " items" else " item")
        cartInfoText += " in your cart\nfor "
        ("$" + String.format("%.2f", 0.01 * totalPriceInCart)).also { cartInfoText += it }
        cartInfoText += "\n checkout with"
        cartInfoTextView = findViewById(R.id.cartTextView)
        cartInfoTextView.text = cartInfoText

//        initializing PP SDK credentials for the ordering process
        orderInit(totalPriceInCart)
    }

//    PP SDK setup
    private fun orderInit(totalPrice: Int) {
        val config = CheckoutConfig(  //credentials for PP SDK
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

        payPalButton.setup(  //button setup for PP checkout process
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
                                value = (String.format("%.2f", 0.01 * totalPrice))
                            )
                        )
                    )
                )
                createOrderActions.create(order)
            },
            onApprove = OnApprove { approval ->  // wiping the cart and returning to the main screen on success
                approval.orderActions.capture {
                    val intent = Intent()
                    intent.putExtra(
                        getString(R.string.activity_result),
                        getString(R.string.activity_result_success)
                    )
                    setResult(RESULT_OK, intent)
                    finish()
                }
            },
            onCancel = OnCancel {  //toast message on order cancel
                Toast.makeText(
                    this,
                    getString(R.string.message_order_cancelled),
                    Toast.LENGTH_LONG
                ).show()
            },
            onError = OnError { errorInfo ->  //errors handling. starting an activity on error, showing the detailed error message
                val checkoutErrorIntent = Intent(this, CheckoutError()::class.java)
                checkoutErrorIntent.putExtra(
                    getString(R.string.error_message),
                    errorInfo.toString()
                )
                startActivity(checkoutErrorIntent)
                Toast.makeText(  //toast message on order error also
                    this,
                    getString(R.string.message_order_cancelled),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        )

    }

}