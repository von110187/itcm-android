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

class AdminProductActivity : AppCompatActivity() {

    private lateinit var listViewProducts: ListView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_product)

        val buttonPost = findViewById<Button>(R.id.buttonPost)
        val buttonProduct = findViewById<Button>(R.id.buttonProduct)
        val buttonOrder = findViewById<Button>(R.id.buttonOrder)

        buttonPost.setOnClickListener {
            startActivity(Intent(this, AdminPostActivity::class.java))
        }

        buttonProduct.setOnClickListener {
            startActivity(Intent(this, AdminProductActivity::class.java))
        }

        buttonOrder.setOnClickListener {
            startActivity(Intent(this, AdminOrderActivity::class.java))
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
                        productList.add(it)
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