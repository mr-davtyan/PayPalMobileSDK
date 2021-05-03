package net.davtyan.paypalmobilesdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class CheckoutError : AppCompatActivity() {

    private lateinit var closeButton: Button
    private lateinit var errorTextView: TextView

//simple activity for errors handling during checkout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_error)

        closeButton = findViewById(R.id.buttonErrorClose)
        errorTextView = findViewById(R.id.textViewError)

        val bundle: Bundle? = intent.extras
        val errorMessage: String? = bundle!!.getString(getString(R.string.error_message))
        errorTextView.text = errorMessage
        closeButton.setOnClickListener {
            finish()
        }

    }
}