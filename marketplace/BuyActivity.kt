package my.edu.tarc.itcm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        // Retrieve data passed from MarketplaceActivity
        val productName = intent.getStringExtra("productName")
        val price = intent.getStringExtra("price")
        val imageUrl = intent.getStringExtra("imageUrl")
        val datetime = intent.getLongExtra("datetime", 0) // Default value 0 if not found
        val condition = intent.getStringExtra("condition")
        val category = intent.getStringExtra("category")
        val productDetails = intent.getStringExtra("productDetails")
        val email = intent.getStringExtra("email")

        // Populate views with the retrieved data
        val productNameTextView: TextView = findViewById(R.id.productNameTextView)
        val priceTextView: TextView = findViewById(R.id.priceTextView)
        val datetimeTextView: TextView = findViewById(R.id.datetimeTextView)
        val conditionTextView: TextView = findViewById(R.id.textViewCondition)
        val categoryTextView: TextView = findViewById(R.id.textViewCategory)
        val productDetailsTextView: TextView = findViewById(R.id.textViewDetails)
        val productImageView: ImageView = findViewById(R.id.productImageView)
        val buyButton: Button = findViewById(R.id.buttonBuy)

        productNameTextView.text = productName
        priceTextView.text = "RM$price"
        datetimeTextView.text = convertTimeToString(datetime)
        conditionTextView.text = "Condition: $condition"
        categoryTextView.text = "Category: $category"
        productDetailsTextView.text = productDetails

        // Load image into ImageView using Glide if the image URL exists
        imageUrl?.let {
            Glide.with(this)
                .load(imageUrl)
                .into(productImageView)
            productImageView.visibility = View.VISIBLE // Show the ImageView
        } ?: run {
            productImageView.visibility = View.GONE // Hide the ImageView if no image URL
        }

        // Check if the current user's email matches the provided email
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail == email) {
            buyButton.visibility = View.GONE // Hide the button if emails match
        }

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

        // Set OnClickListener for the buy button
        buyButton.setOnClickListener {
            // Start OrderActivity and pass necessary data using intent extras
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("productName", productName)
            intent.putExtra("price", price)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }

    // Function to convert milliseconds since epoch to human-readable datetime
    private fun convertTimeToString(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(timeInMillis)
        return sdf.format(date)
    }
}