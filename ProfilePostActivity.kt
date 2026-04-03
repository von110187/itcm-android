package my.edu.tarc.itcm

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfilePostActivity : AppCompatActivity() {

    private lateinit var listViewPosts: ListView
    private lateinit var postsAdapter: PostAdapter
    private lateinit var currentUserEmail: String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_post)

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

        listViewPosts = findViewById(R.id.listViewPosts)

        // Initialize adapter for list view
        postsAdapter = PostAdapter(this, mutableListOf())
        listViewPosts.adapter = postsAdapter

        // Get current user's email
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("posts")

        // Set up ValueEventListener to fetch data from Firebase for posts with matching email
        val query: Query = database.orderByChild("email").equalTo(currentUserEmail)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postsList = mutableListOf<Post>()

                // Iterate through each post
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let {
                        // Set the key for each post
                        val postWithKey = it.withKey(postSnapshot.key ?: "")
                        // Add post to the list
                        postsList.add(postWithKey)
                    }
                }

                // Update adapter with posts data
                postsAdapter.clear()
                postsAdapter.addAll(postsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    // Data class representing a Post
    data class Post(
        val key: String? = null,
        val content: String? = null,
        val images: Map<String, String>? = null,
        val email: String? = null,
        val datetime: Long? = null,
        val likes: Map<String, String>? = null
    ) {
        fun withKey(newKey: String): Post {
            return copy(key = newKey)
        }
    }

    // Custom adapter to handle both text and images
    inner class PostAdapter(
        private val context: AppCompatActivity,
        protected val posts: MutableList<Post>
    ) : ArrayAdapter<Post>(context, R.layout.list_item, posts) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                val inflater = LayoutInflater.from(context)
                itemView = inflater.inflate(R.layout.list_item, parent, false)
            }

            val currentPost = getItem(position)

            val contentTextView: TextView = itemView!!.findViewById(R.id.contentTextView)
            val imageView: ImageView = itemView.findViewById(R.id.imageView)
            val textViewName: TextView = itemView.findViewById(R.id.textViewName)
            val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
            val textViewLike: TextView = itemView.findViewById(R.id.textViewLike)
            val textViewComment: TextView = itemView.findViewById(R.id.textViewComment)
            val textViewUpdate: TextView = itemView.findViewById(R.id.textViewUpdate)
            val textViewDelete: TextView = itemView.findViewById(R.id.textViewDelete)
            val imageViewProfile: ImageView = itemView.findViewById(R.id.imageViewProfile) // New ImageView for profile picture

            // Set content text
            contentTextView.text = currentPost?.content

            // Check if currentPost is not null
            if (currentPost != null) {
                // Check if there is an image URL available
                if (currentPost.images.isNullOrEmpty()) {
                    // No image URL available, hide the ImageView
                    imageView.visibility = View.GONE
                } else {
                    // Load and set image using Glide
                    currentPost.images.values.firstOrNull()?.let { imageUrl ->
                        Glide.with(context)
                            .load(imageUrl)
                            .into(imageView)
                    }
                    // Make sure the ImageView is visible
                    imageView.visibility = View.VISIBLE
                }

                // Retrieve user data and set username and profile picture
                currentPost.email?.let { userEmail ->
                    val usersRef = FirebaseDatabase.getInstance().getReference("users")
                    usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (userSnapshot in dataSnapshot.children) {
                                val username = userSnapshot.child("username").getValue(String::class.java)
                                textViewName.text = username

                                val profilePictureUrl = userSnapshot.child("profile_picture").getValue(String::class.java)
                                profilePictureUrl?.let {
                                    Glide.with(context)
                                        .load(it)
                                        .into(imageViewProfile) // Load and set profile picture
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle database error
                        }
                    })
                }

                // Set datetime
                val datetime = currentPost.datetime
                // Check if datetime is not null
                if (datetime != null) {
                    // Convert datetime to readable format, assuming it's in milliseconds
                    val formattedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        Date(datetime)
                    )
                    textViewDate.text = formattedDateTime
                } else {
                    // If datetime is null, set a default value or handle it accordingly
                    textViewDate.text = "Unknown"
                }

                // Set initial like count
                textViewLike.text = "Like: ${currentPost.likes?.size ?: 0}"

                // Add click listener to textViewLike
                textViewLike.setOnClickListener {
                    // Increment like count
                    val currentLikes = currentPost.likes?.size ?: 0
                    textViewLike.text = "Like: ${currentLikes + 1}"

                    // Update database with new like
                    val postRef = database.child(getItem(position)?.key ?: "")

                    // Get the current user's email
                    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

                    // Check if the user has already liked the post
                    postRef.child("likes").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var likeExists = false
                            dataSnapshot.children.forEach { likeSnapshot ->
                                if (likeSnapshot.getValue(String::class.java) == currentUserEmail) {
                                    // User has already liked the post, so remove the like
                                    likeSnapshot.ref.removeValue()
                                    likeExists = true
                                }
                            }

                            if (!likeExists) {
                                // User has not liked the post, so add the like
                                val newLikeIndex = dataSnapshot.childrenCount.toInt()
                                postRef.child("likes").child("like$newLikeIndex").setValue(currentUserEmail)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle database error
                        }
                    })
                }

                // Add click listener to textViewComment
                textViewComment.setOnClickListener {
                    val postKey = getItem(position)?.key ?: ""
                    showCommentDialog(postKey)
                }

                // Make textViewUpdate and textViewDelete visible
                textViewUpdate.visibility = View.VISIBLE
                textViewDelete.visibility = View.VISIBLE

                // Add click listener to textViewUpdate
                textViewUpdate.setOnClickListener {
                    // Start UpdatePostActivity and pass necessary data
                    val intent = Intent(context, UpdatePostActivity::class.java).apply {
                        putExtra("postKey", currentPost?.key)
                        putExtra("content", currentPost?.content)
                        val imageUrl = currentPost?.images?.values?.firstOrNull()
                        putExtra("imageUrl", imageUrl)
                    }
                    context.startActivity(intent)
                }

                // Add click listener to textViewDelete
                textViewDelete.setOnClickListener {
                    // Show confirmation dialog before deleting the post
                    val dialog = Dialog(context)
                    dialog.setContentView(R.layout.confirmation_dialog)

                    val confirmButton: Button = dialog.findViewById(R.id.confirmButton)
                    val cancelButton: Button = dialog.findViewById(R.id.cancelButton)

                    confirmButton.setOnClickListener {
                        // Delete the post
                        deletePost(currentPost?.key)
                        dialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }

            }

            return itemView
        }
    }

    // Data class representing a Comment
    data class Comment(
        val email: String? = null,
        val comment: String? = null,
        val timestamp: Long? = null
    )

    // Custom adapter to display comments
    inner class CommentAdapter(
        context: AppCompatActivity,
        private val comments: List<Comment>
    ) : ArrayAdapter<Comment>(context, R.layout.comment_item, comments) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                val inflater = LayoutInflater.from(context)
                itemView = inflater.inflate(R.layout.comment_item, parent, false)
            }

            val currentComment = getItem(position)

            val imageViewCommentProfile: ImageView = itemView!!.findViewById(R.id.imageViewCommentProfile)
            val textViewCommentName: TextView = itemView.findViewById(R.id.textViewCommentName)
            val textViewCommentDate: TextView = itemView.findViewById(R.id.textViewCommentDate)
            val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)

            // Set comment text
            commentTextView.text = currentComment?.comment

            // Retrieve user data and set username and profile picture
            currentComment?.email?.let { userEmail ->
                val usersRef = FirebaseDatabase.getInstance().getReference("users")
                usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (userSnapshot in dataSnapshot.children) {
                            val username = userSnapshot.child("username").getValue(String::class.java)
                            textViewCommentName.text = username

                            val profilePictureUrl = userSnapshot.child("profile_picture").getValue(String::class.java)
                            profilePictureUrl?.let {
                                Glide.with(context)
                                    .load(it)
                                    .into(imageViewCommentProfile)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle database error
                    }
                })
            }

            // Set timestamp
            val timestamp = currentComment?.timestamp
            if (timestamp != null) {
                val formattedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
                textViewCommentDate.text = formattedDateTime
            } else {
                textViewCommentDate.text = "Unknown"
            }

            return itemView
        }
    }

    // Function to show comment dialog
    private fun showCommentDialog(postKey: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.comments_layout)

        val listViewComments: ListView = dialog.findViewById(R.id.listViewComments)
        val editTextComment: EditText = dialog.findViewById(R.id.editTextComment)
        val buttonAddComment: Button = dialog.findViewById(R.id.buttonAddComment)

        // Populate the listViewComments with existing comments for the post
        loadComments(postKey, listViewComments)

        // Set click listener for the buttonAddComment
        buttonAddComment.setOnClickListener {
            val commentText = editTextComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                // Add the comment to the database
                addComment(postKey, commentText)
                // Clear the editTextComment after adding the comment
                editTextComment.setText("")
            }
        }

        dialog.show()
    }

    // Function to load comments from Firebase and update the list view
    private fun loadComments(postKey: String, listViewComments: ListView) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("posts").child(postKey).child("comments")
        val commentsList = mutableListOf<Comment>()

        val commentsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentsList.clear()
                for (commentSnapshot in dataSnapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    comment?.let {
                        commentsList.add(it)
                    }
                }
                // Update the ListView with the comments
                val adapter = CommentAdapter(this@ProfilePostActivity, commentsList)
                listViewComments.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        }

        // Attach the ValueEventListener to listen for changes in comments
        commentsRef.addValueEventListener(commentsListener)

        // Save a reference to the listener so that you can remove it later
        listViewComments.tag = commentsListener
    }

    // Function to add a new comment to the Firebase database
    private fun addComment(postKey: String, commentText: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("posts").child(postKey).child("comments")

        // Push the new comment to the database
        val commentMap = HashMap<String, Any>()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val timestamp = System.currentTimeMillis()
        commentMap["email"] = currentUserEmail
        commentMap["comment"] = commentText
        commentMap["timestamp"] = timestamp

        commentsRef.push().setValue(commentMap)
    }

    // Function to delete a post from Firebase
    private fun deletePost(postKey: String?) {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts")
        postKey?.let {
            postsRef.child(it).removeValue()
        }
    }

}
