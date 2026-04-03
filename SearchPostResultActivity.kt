package my.edu.tarc.itcm

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SearchPostResultActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var listViewPosts: ListView
    private lateinit var postsAdapter: HomepageActivity.PostAdapter
    private var postsList: MutableList<HomepageActivity.Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_post_result)

        val query = intent.getStringExtra("QUERY") ?: ""

        listViewPosts = findViewById(R.id.listViewPosts)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("posts")

        // Initialize adapter for list view
        postsAdapter = HomepageActivity().PostAdapter(this, postsList)
        listViewPosts.adapter = postsAdapter

        // Set up ValueEventListener to fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postsList.clear()
                // Iterate through each post
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(HomepageActivity.Post::class.java)
                    // Check if the post contains the search query
                    if (post?.content?.contains(query, ignoreCase = true) == true) {
                        // Add post to the list
                        postsList.add(post)
                    }
                }
                // Update adapter with filtered posts data
                postsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        // Click listener for sorting by datetime (latest)
        findViewById<TextView>(R.id.textViewLatest).setOnClickListener {
            sortPostsByDatetime()
        }

        // Click listener for sorting by number of likes (most like)
        findViewById<TextView>(R.id.textViewMostLike).setOnClickListener {
            sortPostsByLikes()
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

    private fun sortPostsByDatetime() {
        // Sort posts by datetime in descending order
        postsList.sortByDescending { it.datetime }
        postsAdapter.notifyDataSetChanged()
    }

    private fun sortPostsByLikes() {
        // Sort posts by number of likes in descending order
        postsList.sortByDescending { it.likes?.size ?: 0 }
        postsAdapter.notifyDataSetChanged()
    }
}
