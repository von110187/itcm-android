package my.edu.tarc.itcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.content.Intent
import android.app.Activity
import com.bumptech.glide.Glide

class UpdateProductActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1 // Request code for image picker

    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)

        // Initialize views
        val editTextCategory = findViewById<EditText>(R.id.editTextCategory)
        val editTextProductName = findViewById<EditText>(R.id.editTextProductName)
        val editTextProductDetails = findViewById<EditText>(R.id.editTextProductDetails)
        val editTextCondition = findViewById<EditText>(R.id.editTextCondition)
        val editTextPrice = findViewById<EditText>(R.id.editTextPrice)
        val imageViewUploadedImages = findViewById<ImageView>(R.id.imageViewUploadedImages)
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdate)

        // Set OnClickListener for the imageView to change the image
        imageViewUploadedImages.setOnClickListener {
            // Open an image picker or provide options to select a new image
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        // Retrieve data from intent extras
        val productName = intent.getStringExtra("productName")
        val productDetails = intent.getStringExtra("productDetails")
        val condition = intent.getStringExtra("condition")
        val category = intent.getStringExtra("category")
        val price = intent.getStringExtra("price")
        imageUrl = intent.getStringExtra("imageUrl")

        // Set retrieved data to respective views
        editTextCategory.setText(category)
        editTextProductName.setText(productName)
        editTextProductDetails.setText(productDetails)
        editTextCondition.setText(condition)
        editTextPrice.setText(price)
        // Load image into ImageView
        imageUrl?.let {
            Glide.with(this)
                .load(imageUrl)
                .into(imageViewUploadedImages)
            imageViewUploadedImages.visibility = View.VISIBLE // Show the ImageView
        } ?: run {
            imageViewUploadedImages.visibility = View.GONE // Hide the ImageView if no image URL
        }

        // Implement your logic for updating the product and handling the sell button click event
        buttonUpdate.setOnClickListener {
            // Your logic for updating the product
        }
    }

    // Handle the result from the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the selected image URI
            val imageUri = data.data

            // Update the imageView with the selected image
            val imageViewUploadedImages = findViewById<ImageView>(R.id.imageViewUploadedImages)
            Glide.with(this)
                .load(imageUri)
                .into(imageViewUploadedImages)

            // Update the imageUrl with the new image URI
            imageUrl = imageUri.toString()
        }
    }
}