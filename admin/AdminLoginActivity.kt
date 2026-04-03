package my.edu.tarc.itcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        database = FirebaseDatabase.getInstance().reference.child("admins")

        val textViewForgotPassword: TextView = findViewById(R.id.textViewForgotPassword)
        val editTextEmail: EditText = findViewById(R.id.editTextAddress)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonUserLogin: Button = findViewById(R.id.buttonUserLogin)

        textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, AdminResetPasswordActivity::class.java))
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                validateAdmin(email, password)
            }
        }

        buttonUserLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validateAdmin(email: String, password: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isAdminValid = false
                for (adminSnapshot in dataSnapshot.children) {
                    val adminEmail = adminSnapshot.child("email").value.toString()
                    val adminPassword = adminSnapshot.child("password").value.toString()
                    if (adminEmail == email && adminPassword == password) {
                        isAdminValid = true
                        break
                    }
                }
                if (isAdminValid) {
                    // Admin credentials are valid, proceed to next activity
                    Toast.makeText(baseContext, "Admin login successful.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AdminLoginActivity, AdminPostActivity::class.java))
                    finish()
                } else {
                    // Admin credentials are invalid
                    Toast.makeText(baseContext, "Invalid email or password.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(baseContext, "Database error occurred.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
