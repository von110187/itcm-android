package my.edu.tarc.itcm

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class OrderActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // Populate spinner for delivery options
        val deliverySpinner: Spinner = findViewById(R.id.spinnerDelivery)
        val deliveryOptions = arrayOf("COD", "Post")
        val deliveryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deliveryOptions)
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        deliverySpinner.adapter = deliveryAdapter

        // Populate spinner for payment options
        val paymentSpinner: Spinner = findViewById(R.id.spinnerPayment)
        val paymentOptions = arrayOf("Cash", "Online Transfer")
        val paymentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentOptions)
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentSpinner.adapter = paymentAdapter

        val textViewCommunity = findViewById<TextView>(R.id.textViewCommunity)
        textViewCommunity.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }

        val textViewMarketplace = findViewById<TextView>(R.id.textViewMarketplace)
        textViewMarketplace.setOnClickListener {
            val intent = Intent(this, MarketplaceActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Retrieve product details passed from MarketplaceActivity
        val productName = intent.getStringExtra("productName")
        val price = intent.getStringExtra("price")
        val imageUrl = intent.getStringExtra("imageUrl")
        val email = intent.getStringExtra("email")

        // Display product details in the layout
        val productNameTextView: TextView = findViewById(R.id.productNameTextView)
        val priceTextView: TextView = findViewById(R.id.priceTextView)
        val productImageView: ImageView = findViewById(R.id.productImageView)

        productNameTextView.text = productName
        priceTextView.text = "RM$price"

        // Load image into ImageView using Glide if the image URL exists
        imageUrl?.let {
            Glide.with(this)
                .load(imageUrl)
                .into(productImageView)
        }

        // Handle placing order button click
        val placeOrderButton: Button = findViewById(R.id.buttonPlaceOrder)
        placeOrderButton.setOnClickListener {
            // Retrieve user input (e.g., address)
            val addressEditText: EditText = findViewById(R.id.editTextAddress)
            val address = addressEditText.text.toString()

            // Check if the address field is empty
            if (address.isEmpty()) {
                // Show an error message or indication to the user that address is required
                addressEditText.error = "Address is required!"
                return@setOnClickListener // Stop further execution
            }

            // Retrieve selected delivery option
            val deliverySpinner: Spinner = findViewById(R.id.spinnerDelivery)
            val selectedDeliveryOption = deliverySpinner.selectedItem.toString()

            // Retrieve selected payment option
            val paymentSpinner: Spinner = findViewById(R.id.spinnerPayment)
            val selectedPaymentOption = paymentSpinner.selectedItem.toString()

            // Retrieve user email
            val buyerEmail = auth.currentUser?.email

            // Retrieve product details passed from MarketplaceActivity
            val productName = intent.getStringExtra("productName")
            val price = intent.getStringExtra("price")
            val imageUrl = intent.getStringExtra("imageUrl")
            val email = intent.getStringExtra("email")

            // Save order data to Firebase directly under "orders" node
            val orderRef = database.reference.child("orders").push()
            val order = Order(productName, price, address, imageUrl, buyerEmail, selectedDeliveryOption, selectedPaymentOption)
            orderRef.setValue(order)

            // Update product node to include buyerEmail
            updateProductWithBuyerEmail(productName, buyerEmail)

            // Insert notification data into the database
            insertNotification(email, buyerEmail, "product", "place order") // Passing buyer's email as well

            // Redirect user to ProfileActivity
            startActivity(Intent(this, ProfileOrderActivity::class.java))
            finish()
        }
    }

    private fun updateProductWithBuyerEmail(productName: String?, buyerEmail: String?) {
        if (productName != null && buyerEmail != null) {
            val productQuery = database.reference.child("products")
                .orderByChild("productName")
                .equalTo(productName)
            productQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (productSnapshot in dataSnapshot.children) {
                        val productId = productSnapshot.key
                        productId?.let {
                            // Update the product node with buyerEmail
                            database.reference.child("products").child(it).child("buyerEmail").setValue(buyerEmail)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Product updated successfully
                                    } else {
                                        // Failed to update product
                                    }
                                }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun insertNotification(email: String?, buyerEmail: String?, type: String, action: String) {
        email?.let {
            val datetime = Calendar.getInstance().timeInMillis
            val notificationRef = database.reference.child("notifications").push()
            val notification = Notification(email, buyerEmail, type, action, datetime)
            notificationRef.setValue(notification)
        }
    }
}

// Data class to represent an order
data class Order(
    val productName: String? = null,
    val price: String? = null,
    val address: String? = null,
    val imageUrl: String? = null,
    val email: String? = null,
    val deliveryOption: String? = null,
    val paymentOption: String? = null
)

// Data class to represent a notification
data class Notification(
    val email: String? = null,
    val buyerEmail: String? = null,
    val type: String? = null,
    val action: String? = null,
    val datetime: Long? = null
)