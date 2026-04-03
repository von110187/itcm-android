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
import android.widget.ListView
import android.widget.TextView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class HomepageActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var listViewPosts: ListView
    private lateinit var postsAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val textViewCommunity: TextView = findViewById(R.id.textViewCommunity)
        val textViewMarketplace: TextView = findViewById(R.id.textViewMarketplace)
        val textViewNotification: TextView = findViewById(R.id.textViewNotification)
        val textViewPost: TextView = findViewById(R.id.textViewPost)
        val textViewProfile: TextView = findViewById(R.id.textViewProfile)

        val buttonSearchForPost: Button = findViewById(R.id.buttonSearchForPost)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("posts")
        listViewPosts = findViewById(R.id.listViewPosts)

        // Initialize adapter for list view
        postsAdapter = PostAdapter(this, mutableListOf())
        listViewPosts.adapter = postsAdapter

        // Set up ValueEventListener to fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
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

        textViewCommunity.setOnClickListener {
            startActivity(Intent(this, HomepageActivity::class.java))
        }

        textViewMarketplace.setOnClickListener {
            startActivity(Intent(this, MarketplaceActivity::class.java))
        }

        textViewNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        textViewPost.setOnClickListener {
            val dialog = PostDialogFragment()
            dialog.show(supportFragmentManager, "PostDialogFragment")
        }

        textViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfilePostActivity::class.java))
        }

        // Set click listener for the search button
        buttonSearchForPost.setOnClickListener {
            // Start the SearchForPostActivity
            startActivity(Intent(this, SearchForPostActivity::class.java))
        }

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("posts")
        listViewPosts = findViewById(R.id.listViewPosts)

        // Initialize adapter for list view
        postsAdapter = PostAdapter(this, mutableListOf())
        listViewPosts.adapter = postsAdapter

        // Set up ValueEventListener to fetch data from Firebase
        database.addValueEventListener(object : ValueEventListener {
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

    override fun onDestroy() {
        super.onDestroy()
        // Remove the ValueEventListener to avoid memory leaks
        listViewPosts.tag?.let { postsListener ->
            val postsRef = FirebaseDatabase.getInstance().reference.child("posts")
            postsRef.removeEventListener(postsListener as ValueEventListener)
        }
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
        context: AppCompatActivity,
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
            val imageViewProfile: ImageView = itemView.findViewById(R.id.imageViewProfile)

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
                                        .into(imageViewProfile)
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
                    val formattedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(datetime))
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

                            // Insert notification for the like action
                            insertNotification(currentPost.email, currentUserEmail, "post", "like", currentPost.key.toString())
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle database error
                        }
                    })
                }

                // Add click listener to textViewComment
                textViewComment.setOnClickListener {
                    val postKey = getItem(position)?.key ?: ""
                    showCommentDialog(postKey, currentPost)
                }
            }

            return itemView
        }
    }

    private fun insertNotification(postEmail: String?, currentUserEmail: String?, type: String, action: String, postId: String) {
        postEmail?.let {
            val notificationRef = FirebaseDatabase.getInstance().reference.child("notifications")
            notificationRef.orderByChild("email").equalTo(postEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (notificationSnapshot in dataSnapshot.children) {
                        val notification = notificationSnapshot.getValue(Notification::class.java)
                        if (notification != null && notification.type == type && notification.action == action && notification.otherEmail != null) {
                            val otherEmails = notification.otherEmail.toMutableMap()
                            val newUserId = "user${otherEmails.size}"
                            otherEmails[newUserId] = currentUserEmail!!
                            notificationSnapshot.ref.child("otherEmail").setValue(otherEmails)
                            return
                        }
                    }

                    // If no existing notification found or action not matched, add new notification
                    val datetime = Calendar.getInstance().timeInMillis
                    val otherEmailMap = hashMapOf<String, String>()
                    currentUserEmail?.let { otherEmailMap["email0"] = it }

                    val notification = Notification(
                        postEmail,
                        type = type,
                        action = action,
                        datetime = datetime,
                        otherEmail = otherEmailMap
                    )

                    // Push the notification to the database
                    notificationRef.push().setValue(notification)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
        }
    }

    data class Notification(
        val email: String? = null,
        val type: String? = null,
        val action: String? = null,
        val datetime: Long? = null,
        val otherEmail: Map<String, String>? = null
    )



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

    private fun showCommentDialog(postKey: String, currentPost: Post) {
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
                addComment(postKey, commentText, currentPost)
                // Clear the editTextComment after adding the comment
                editTextComment.setText("")
            }
        }

        dialog.show()
    }

    private fun loadComments(postKey: String, listViewComments: ListView) {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("posts").child(postKey).child("comments")
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
                val adapter = CommentAdapter(this@HomepageActivity, commentsList)
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

    private fun addComment(postKey: String, commentText: String, currentPost: Post) {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("posts").child(postKey).child("comments")

        // Push the new comment to the database
        val commentMap = HashMap<String, Any>()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val timestamp = System.currentTimeMillis()
        commentMap["email"] = currentUserEmail
        commentMap["comment"] = commentText
        commentMap["timestamp"] = timestamp

        commentsRef.push().setValue(commentMap)
            .addOnSuccessListener {
                // Comment added successfully, insert notification
                insertNotification(currentPost.email, currentUserEmail, "post", "comment", postKey)
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}