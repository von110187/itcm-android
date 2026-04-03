package my.edu.tarc.itcm

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*

class ProfileProductActivity : AppCompatActivity() {

    private lateinit var listViewProducts: ListView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var database: DatabaseReference
    private lateinit var currentUserEmail: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_product)

        val buttonPost = findViewById<Button>(R.id.buttonPost)
        val buttonProduct = findViewById<Button>(R.id.buttonProduct)
        val buttonOrder = findViewById<Button>(R.id.buttonOrder)

        buttonPost.setOnClickListener {
            startActivity(Intent(this, ProfilePostActivity::class.java))
        }

        buttonProduct.setOnClickListener {
            startActivity(Intent(this, ProfileProductActivity::class.java))
        }

        buttonOrder.setOnClickListener {
            startActivity(Intent(this, ProfileOrderActivity::class.java))
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

        val textViewLogout = findViewById<TextView>(R.id.textViewLogout)
        textViewLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Redirect to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val textViewNotification: TextView = findViewById(R.id.textViewNotification)
        textViewNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        auth = Firebase.auth
        currentUserEmail = auth.currentUser?.email ?: ""

        listViewProducts = findViewById(R.id.listViewProducts)

        // Initialize adapter for list view
        productAdapter = ProductAdapter(this, mutableListOf())
        listViewProducts.adapter = productAdapter

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("products")

        // Set up ValueEventListener to fetch product data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<MarketplaceActivity.Product>()

                // Iterate through each product
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(MarketplaceActivity.Product::class.java)
                    product?.let {
                        if (it.email == currentUserEmail) {
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
    }
}