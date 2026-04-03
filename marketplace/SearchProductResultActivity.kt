package my.edu.tarc.itcm

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SearchProductResultActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var listViewProducts: ListView
    private lateinit var productsAdapter: MarketplaceActivity.ProductAdapter
    private var productsList: MutableList<MarketplaceActivity.Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product_result)

        val query = intent.getStringExtra("QUERY") ?: ""

        listViewProducts = findViewById(R.id.listViewProducts)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("products")

        // Initialize adapter for list view
        productsAdapter = MarketplaceActivity().ProductAdapter(this, productsList)
        listViewProducts.adapter = productsAdapter

        // Set up ValueEventListener to fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear()
                // Iterate through each product
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(MarketplaceActivity.Product::class.java)
                    // Check if the product contains the search query
                    if (product?.productName?.contains(query, ignoreCase = true) == true) {
                        // Add product to the list
                        productsList.add(product)
                    }
                }
                // Update adapter with filtered products data
                productsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        // Click listener for sorting by datetime (latest)
        findViewById<TextView>(R.id.textViewLatest).setOnClickListener {
            sortProductsByDatetime()
        }

        // Click listener for sorting by price (ascending)
        findViewById<TextView>(R.id.textViewPrice).setOnClickListener {
            sortProductsByPrice()
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
    }

    private fun sortProductsByDatetime() {
        // Sort products by datetime in descending order
        productsList.sortByDescending { it.datetime }
        productsAdapter.notifyDataSetChanged()
    }

    private fun sortProductsByPrice() {
        // Sort products by price in ascending order
        productsList.sortBy { it.price }
        productsAdapter.notifyDataSetChanged()
    }
}