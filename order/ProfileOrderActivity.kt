package my.edu.tarc.itcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*

class ProfileOrderActivity : AppCompatActivity() {

    private lateinit var listViewOrders: ListView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var database: DatabaseReference
    private lateinit var currentUserEmail: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_order)

        val buttonPost = findViewById<Button>(R.id.buttonPost)
        val buttonProduct = findViewById<Button>(R.id.buttonProduct)
        val buttonOrder = findViewById<Button>(R.id.buttonOrder)

        buttonPost.setOnClickListener {
            startActivity(Intent(this@ProfileOrderActivity, ProfilePostActivity::class.java))
        }

        buttonProduct.setOnClickListener {
            startActivity(Intent(this@ProfileOrderActivity, ProfileProductActivity::class.java))
        }

        buttonOrder.setOnClickListener {
            startActivity(Intent(this@ProfileOrderActivity, ProfileOrderActivity::class.java))
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

        auth = FirebaseAuth.getInstance()
        currentUserEmail = auth.currentUser?.email ?: ""

        listViewOrders = findViewById(R.id.listViewOrders)

        // Initialize adapter for list view
        orderAdapter = OrderAdapter(this, mutableListOf())
        listViewOrders.adapter = orderAdapter

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("orders")

        // Set up ValueEventListener to fetch order data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = mutableListOf<Order>()

                // Iterate through each order
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        if (it.email == currentUserEmail) {
                            orderList.add(it)
                        }
                    }
                }

                // Update adapter with order data
                orderAdapter.clear()
                orderAdapter.addAll(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}