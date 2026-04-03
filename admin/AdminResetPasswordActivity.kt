package my.edu.tarc.itcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminResetPasswordActivity : AppCompatActivity() {

    private lateinit var editTextAdminEmail: EditText
    private lateinit var buttonResetPassword: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reset_password)

        editTextAdminEmail = findViewById(R.id.editTextAddress)
        buttonResetPassword = findViewById(R.id.buttonResetPassword)
        databaseReference = FirebaseDatabase.getInstance().getReference("admins")

        buttonResetPassword.setOnClickListener {
            val adminEmail = editTextAdminEmail.text.toString()

            if (adminEmail.isNotEmpty()) {
                verifyAdminEmail(adminEmail)
            } else {
                Toast.makeText(this, "Please enter admin email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyAdminEmail(adminEmail: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (adminSnapshot in snapshot.children) {
                    val email = adminSnapshot.child("email").getValue(String::class.java)
                    if (email == adminEmail) {
                        sendPasswordResetEmail(adminEmail)
                        return
                    }
                }
                Toast.makeText(this@AdminResetPasswordActivity, "Invalid admin email", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@AdminResetPasswordActivity, "Database error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendPasswordResetEmail(email: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    Toast.makeText(this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle case when password reset email fails to send
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
