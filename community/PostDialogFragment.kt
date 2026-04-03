package my.edu.tarc.itcm

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class PostDialogFragment : DialogFragment() {

    private lateinit var selectedImages: MutableList<Uri>
    private lateinit var imageView: ImageView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openImagePicker()
            } else {
                Log.e(TAG, "Permission denied: READ_MEDIA_IMAGES")
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Cannot select images.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val pickImages =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val clipData = data.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val imageUri = clipData.getItemAt(i).uri
                            selectedImages.add(imageUri)
                            // Handle each image URI here as needed
                        }
                    } else {
                        val imageUri = data.data
                        selectedImages.add(imageUri!!)
                        // Handle the single selected image URI here as needed
                    }
                    displayImages()
                }
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.post_dialog_fragment, null)
        val editTextPostContent = view?.findViewById<EditText>(R.id.editTextPostContent)
        imageView = view?.findViewById<ImageView>(R.id.imageView)!!
        val buttonPublish = view?.findViewById<Button>(R.id.buttonPublish)

        selectedImages = mutableListOf()

        imageView.setOnClickListener {
            if (selectedImages.isNotEmpty()) {
                displayImages()
            } else {
                checkPermissionAndOpenImagePicker()
            }
        }

        buttonPublish?.setOnClickListener {
            val postContent = editTextPostContent?.text.toString().trim()
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            val currentTime = System.currentTimeMillis()

            if (postContent.isNotEmpty()) {
                // Push post content to Firebase
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("posts").push()
                reference.child("content").setValue(postContent)
                userEmail?.let { reference.child("email").setValue(it) }
                reference.child("datetime").setValue(currentTime)

                // Upload selected images to Firebase Storage and get their URLs
                for ((index, imageUri) in selectedImages.withIndex()) {
                    uploadImageToFirebaseStorage(imageUri) { imageUrl ->
                        reference.child("images").child("image$index").setValue(imageUrl)
                    }
                }
                dismiss()
            } else {
                editTextPostContent?.error = "Please enter post content"
            }
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun checkPermissionAndOpenImagePicker() {
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openImagePicker()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickImages.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private fun displayImages() {
        if (selectedImages.isNotEmpty()) {
            // Display the selected images in the ImageView
            imageView.setImageURI(selectedImages[0])
            for (i in 1 until selectedImages.size) {
                val additionalImageView = ImageView(requireContext())
                additionalImageView.setImageURI(selectedImages[i])
                // Add additionalImageView to your layout if needed
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, onComplete: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imagesRef = storageReference.child("post_images/${UUID.randomUUID()}.jpg")

        imagesRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete.invoke(uri.toString())
                }.addOnFailureListener { exception ->
                    // Handle any errors
                    Log.e(TAG, "Failed to upload image: ${exception.message}")
                    Toast.makeText(
                        requireContext(),
                        "Failed to upload image: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "PostDialogFragment"
    }
}