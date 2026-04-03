package my.edu.tarc.itcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        val editTextEmail: EditText = findViewById(R.id.editTextAddress)

        val buttonResetPassword: Button = findViewById(R.id.buttonResetPassword)

        buttonResetPassword.setOnClickListener {
            val email = editTextEmail.text.toString()

            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    // You can show a toast message or navigate to a confirmation screen
                    Toast.makeText(this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle case when password reset email fails to send
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}