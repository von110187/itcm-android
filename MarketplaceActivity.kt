package my.edu.tarc.itcm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MarketplaceActivity : AppCompatActivity() {

    private lateinit var listViewProducts: ListView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("products")
        listViewProducts = findViewById(R.id.listViewProducts)

        // Initialize adapter for list view
        productAdapter = ProductAdapter(this, mutableListOf())
        listViewProducts.adapter = productAdapter

        // Set up ValueEventListener to fetch product data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()

                // Iterate through each product
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        // Check if the product contains the key "buyerEmail"
                        if (!productSnapshot.child("buyerEmail").exists()) {
                            // If not, add the product to the list
                            productList.add(it)
                        }
                    }
                }

                // Update adapter with product data
                productAdapter.clear()
                productAdapter.addAll(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        val textViewSell = findViewById<TextView>(R.id.textViewSell)
        textViewSell.setOnClickListener {
            val intent = Intent(this, SellActivity::class.java)
            startActivity(intent)
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

        val buttonSearchForProduct = findViewById<Button>(R.id.buttonSearchForProduct)
        buttonSearchForProduct.setOnClickListener {
            val intent = Intent(this, SearchForProductActivity::class.java)
            startActivity(intent)
        }

        val textViewProfile = findViewById<TextView>(R.id.textViewProfile)
        textViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfilePostActivity::class.java))
        }

        val textViewNotification: TextView = findViewById(R.id.textViewNotification)
        textViewNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    inner class ProductAdapter(context: AppCompatActivity, products: MutableList<Product>) :
        ArrayAdapter<Product>(context, 0, products) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
            }

            val currentProduct = getItem(position)

            val productNameTextView: TextView = itemView!!.findViewById(R.id.productNameTextView)
            val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
            val datetimeTextView: TextView = itemView.findViewById(R.id.datetimeTextView)
            val productImageView: ImageView = itemView.findViewById(R.id.productImageView)

            currentProduct?.let {
                productNameTextView.text = it.productName
                priceTextView.text = "RM${it.price}"
                it.datetime?.let { datetime ->
                    val datetimeStr = convertTimeToString(datetime)
                    datetimeTextView.text = datetimeStr
                }

                // Load image into ImageView using Glide if the image URL exists
                currentProduct.images?.let { images ->
                    val imageUrl = images["image0"]
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(context)
                            .load(imageUrl)
                            .into(productImageView)
                        productImageView.visibility = View.VISIBLE // Show the ImageView
                    } else {
                        productImageView.visibility = View.GONE // Hide the ImageView
                    }
                }
            }

            // Set click listener for the entire item
            itemView.setOnClickListener {
                // Redirect to BuyActivity with appropriate data
                val intent = Intent(context, BuyActivity::class.java)
                // Pass necessary data to BuyActivity using intent extras
                intent.putExtra("productName", currentProduct?.productName)
                intent.putExtra("price", currentProduct?.price)
                intent.putExtra("imageUrl", currentProduct?.images?.get("image0"))
                intent.putExtra("datetime", currentProduct?.datetime)
                intent.putExtra("condition", currentProduct?.condition)
                intent.putExtra("category", currentProduct?.category)
                intent.putExtra("productDetails", currentProduct?.productDetails)
                intent.putExtra("email", currentProduct?.email)
                // Add more data if needed
                context.startActivity(intent)
            }

            return itemView
        }
    }

    // Function to convert milliseconds since epoch to human-readable datetime
    companion object {
        fun convertTimeToString(timeInMillis: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date(timeInMillis)
            return sdf.format(date)
        }
    }

    // Data class representing a product
    data class Product(
        val productName: String? = null,
        val productDetails: String? = null,
        val condition: String? = null,
        val category: String? = null,
        val price: String? = null,
        val datetime: Long? = null,
        val images: Map<String, String>? = null,
        val email: String? = null
    )
}
