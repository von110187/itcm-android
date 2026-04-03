package my.edu.tarc.itcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotificationActivity : AppCompatActivity() {

    private lateinit var listViewNotifications: ListView
    private lateinit var notificationAdapter: ArrayAdapter<String>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        listViewNotifications = findViewById(R.id.listViewPosts)
        notificationAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        listViewNotifications.adapter = notificationAdapter

        val textViewNotification: TextView = findViewById(R.id.textViewNotification)
        textViewNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        val textViewProfile = findViewById<TextView>(R.id.textViewProfile)
        textViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfilePostActivity::class.java))
        }

        val textViewPost: TextView = findViewById(R.id.textViewPost)
        textViewPost.setOnClickListener {
            val dialog = PostDialogFragment()
            dialog.show(supportFragmentManager, "PostDialogFragment")
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

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications")

        // Add a listener to retrieve notifications data
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = ArrayList<String>()
                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

                // Loop through all notifications
                for (notificationSnapshot in snapshot.children) {
                    val action = notificationSnapshot.child("action").getValue(String::class.java) ?: ""
                    val datetime = notificationSnapshot.child("datetime").getValue(Long::class.java) ?: 0
                    val email = notificationSnapshot.child("email").getValue(String::class.java) ?: ""
                    val type = notificationSnapshot.child("type").getValue(String::class.java) ?: ""

                    // Check if notification contains buyerEmail or otherEmail
                    val buyerEmail = notificationSnapshot.child("buyerEmail").getValue(String::class.java)
                    val otherEmails = notificationSnapshot.child("otherEmail").children.mapNotNull { it.getValue(String::class.java) }.toList()

                    if (email == currentUserEmail) {
                        // Create notification text for buyerEmail
                        if (buyerEmail != null) {
                            val notificationText = "Action: $action\nBuyer Email: $buyerEmail\nDatetime: $datetime\nEmail: $email\nType: $type"
                            notifications.add(notificationText)
                        }

                        // Create notification text for otherEmails
                        if (otherEmails.isNotEmpty()) {
                            for (i in otherEmails.indices) {
                                val otherEmail = otherEmails[i]
                                val notificationText = "Action: $action\nOther Email: $otherEmail\nDatetime: $datetime\nEmail: $email\nType: $type"
                                notifications.add(notificationText)
                            }
                        }
                    }
                }

                // Update the adapter with new data
                notificationAdapter.clear()
                notificationAdapter.addAll(notifications)
                notificationAdapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
