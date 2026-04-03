package my.edu.tarc.itcm

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SellActivity : AppCompatActivity() {
    private lateinit var editTextCategory: EditText
    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductDetails: EditText
    private lateinit var editTextCondition: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var buttonSell: Button
    private lateinit var imageViewUploadedImages: ImageView
    private var selectedImages: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell)

        // Initialize views
        editTextCategory = findViewById(R.id.editTextCategory)
        editTextProductName = findViewById(R.id.editTextProductName)
        editTextProductDetails = findViewById(R.id.editTextProductDetails)
        editTextCondition = findViewById(R.id.editTextCondition)
        editTextPrice = findViewById(R.id.editTextPrice)
        buttonSell = findViewById(R.id.buttonUpdate)
        imageViewUploadedImages = findViewById(R.id.imageViewUploadedImages)

        // Set click listener for the Sell button
        buttonSell.setOnClickListener {
            sellItem()
        }

        // Set click listener for the ImageView to trigger image selection
        imageViewUploadedImages.setOnClickListener {
            // Launch image picker
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, PICK_IMAGES_REQUEST_CODE)
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

    private fun sellItem() {
        // Get current user's email
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        // Get input from EditText fields
        val category = editTextCategory.text.toString().trim()
        val productName = editTextProductName.text.toString().trim()
        val productDetails = editTextProductDetails.text.toString().trim()
        val condition = editTextCondition.text.toString().trim()
        val price = editTextPrice.text.toString().trim()

        // Validate input (you can add more validation as per your requirements)
        if (category.isEmpty() || productName.isEmpty() || productDetails.isEmpty() || condition.isEmpty() || price.isEmpty()) {
            // Show a toast or error message indicating that all fields are required
            return
        }

        // Get current datetime in milliseconds
        val currentDateTime = getCurrentDateTime()

        // Create a reference to the Firebase database
        val database = FirebaseDatabase.getInstance().reference

        // Create a HashMap to hold the item data
        val itemMap = hashMapOf<String, Any>(
            "category" to category,
            "productName" to productName,
            "productDetails" to productDetails,
            "condition" to condition,
            "price" to price,
            "email" to currentUserEmail.orEmpty(),
            "datetime" to currentDateTime
        )

        // Generate a unique key for the item
        val itemId = database.child("products").push().key

        // Check if the itemId is not null
        itemId?.let { id ->
            // Save the item data to the database under the "products" node with the generated key
            database.child("products").child(id).setValue(itemMap)
                .addOnSuccessListener {
                    // Item data saved successfully
                    // Upload images if there are any
                    if (selectedImages.isNotEmpty()) {
                        uploadImagesToStorage(id)
                    } else {
                        // No images to upload, finish activity
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    // Failed to save item data
                    // Handle the error (e.g., show an error message)
                }
        }
    }

    private fun uploadImagesToStorage(itemId: String) {
        // Create a reference to the Firebase storage
        val storageRef = FirebaseStorage.getInstance().reference

        // Upload images to Firebase storage
        for ((index, imageUri) in selectedImages.withIndex()) {
            val imageRef = storageRef.child("product_images/$itemId/image$index")
            imageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully
                    // Get the download URL of the uploaded image
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Save the image URL to the database
                        FirebaseDatabase.getInstance().reference
                            .child("products")
                            .child(itemId)
                            .child("images")
                            .child("image$index")
                            .setValue(uri.toString())

                        // Check if all images are uploaded
                        if (index == selectedImages.size - 1) {
                            // Finish activity after uploading all images
                            finish()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle image upload failure
                }
        }
    }

    private fun getCurrentDateTime(): Long {
        return System.currentTimeMillis()
    }

    companion object {
        private const val PICK_IMAGES_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let { intent ->
                if (intent.clipData != null) {
                    val count = intent.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri: Uri = intent.clipData!!.getItemAt(i).uri
                        selectedImages.add(imageUri)
                    }
                } else if (intent.data != null) {
                    val imageUri: Uri = intent.data!!
                    selectedImages.add(imageUri)
                }
                // Display selected images
                displaySelectedImages(selectedImages)
            }
        }
    }

    private fun displaySelectedImages(selectedImages: List<Uri>) {
        // Clear previous images
        imageViewUploadedImages.setImageDrawable(null)

        // Load and display selected images
        for ((index, imageUri) in selectedImages.withIndex()) {
            Glide.with(this)
                .load(imageUri)
                .into(imageViewUploadedImages)
        }
    }
}