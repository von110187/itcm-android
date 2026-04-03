package my.edu.tarc.itcm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class AdminUpdatePostActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1 // Request code for image picker

    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_update_post)

        // Retrieve data from intent extras
        val postKey = intent.getStringExtra("postKey")
        val content = intent.getStringExtra("content")
        imageUrl = intent.getStringExtra("imageUrl")

        // Now you can populate your views with this data
        val editTextPostContent = findViewById<EditText>(R.id.editTextPostContent)
        editTextPostContent.setText(content)

        // Load the image if available
        val imageView = findViewById<ImageView>(R.id.imageView)
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        }

        // Set OnClickListener for the imageView to change the image
        imageView.setOnClickListener {
            // Open an image picker or provide options to select a new image
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        // Set OnClickListener for the publish button
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdate)
        buttonUpdate.setOnClickListener {
            // Call function to update Firebase
            updateFirebase(postKey)
        }
    }

    // Handle the result from the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the selected image URI
            val imageUri = data.data

            // Update the imageView with the selected image
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageURI(imageUri)

            // Update the imageUrl with the new image URI
            imageUrl = imageUri.toString()
        }
    }

    private fun updateFirebase(postKey: String?) {
        val content = findViewById<EditText>(R.id.editTextPostContent).text.toString()

        // Update the content in Firebase
        val database = FirebaseDatabase.getInstance()
        val postsRef = database.getReference("posts")
        val postRef = postsRef.child(postKey ?: return)
        postRef.child("content").setValue(content)

        // If imageUrl is updated, also update it in Firebase
        imageUrl?.let {
            postRef.child("images").child("image0").setValue(it)
        }

        // Redirect to AdminPostActivity after updating Firebase
        val intent = Intent(this, AdminPostActivity::class.java)
        startActivity(intent)

        // Finish the current activity
        finish()
    }
}