package my.edu.tarc.itcm

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val editTextEmail: EditText = findViewById(R.id.editTextAddress)
        val editTextUsername: EditText = findViewById(R.id.editTextUsername)

        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)

        imageViewProfilePicture.setImageResource(R.drawable.normal)

        imageViewProfilePicture.setOnClickListener {
            openImagePicker()
        }

        buttonRegister.setOnClickListener {
            val txtPassword: String = editTextPassword.text.toString()
            val txtEmail: String = editTextEmail.text.toString()
            val username: String = editTextUsername.text.toString()

            if (TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(username)) {
                Toast.makeText(this@RegisterActivity, "Empty credentials!", Toast.LENGTH_SHORT).show()
            } else if (txtPassword.length < 6) {
                Toast.makeText(this@RegisterActivity, "Password too short!", Toast.LENGTH_SHORT).show()
            } else if (!::selectedImageUri.isInitialized) {
                Toast.makeText(this@RegisterActivity, "Please select a profile picture!", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(txtEmail, txtPassword, username)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            // Set the selected image to imageViewProfilePicture
            val imageViewProfilePicture: ImageView = findViewById(R.id.imageViewProfilePicture)
            imageViewProfilePicture.setImageURI(selectedImageUri)
        }
    }

    private fun registerUser(email: String, password: String, username: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@RegisterActivity) { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    val user = auth.currentUser
                    val userId = user?.uid

                    userId?.let {
                        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")
                        storageRef.putFile(selectedImageUri)
                            .addOnSuccessListener { _ ->
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val database = FirebaseDatabase.getInstance()
                                    val usersRef = database.getReference("users").child(userId)

                                    val userData = HashMap<String, String>()
                                    userData["username"] = username
                                    userData["email"] = email
                                    userData["profile_picture"] = uri.toString()

                                    usersRef.setValue(userData)
                                        .addOnCompleteListener { userCreationTask ->
                                            if (userCreationTask.isSuccessful) {
                                                Toast.makeText(this@RegisterActivity, "Registering user successful!", Toast.LENGTH_SHORT).show()
                                                startActivity(Intent(this, MainActivity::class.java))
                                                finish()
                                            } else {
                                                Toast.makeText(this@RegisterActivity, "Failed to save user data!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@RegisterActivity, "Failed to upload profile picture: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Registration failed
                    Toast.makeText(this@RegisterActivity, "Registration failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val IMAGE_PICK_REQUEST_CODE = 1
    }
}
