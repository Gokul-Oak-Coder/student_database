package com.example.studentdatabase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.studentdatabase.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //val phone = binding.phoneNo.text.toString()
        //val conf_password = binding.confPass.text.toString()

        binding.signinBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignup.setOnClickListener {
            // Retrieve the email and password values here
            val email = binding.userName.text.toString()
            val password = binding.pass.text.toString()

            // Set the ProgressBar visibility
            binding.progressBar.visibility = View.VISIBLE
            // binding.progressLayer.visibility = View.VISIBLE

            // Check if email and password are empty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // Hide the ProgressBar
                    binding.progressBar.visibility = View.GONE
                    // binding.progressLayer.visibility = View.GONE

                    if (task.isSuccessful) {
                        // If sign up is successful, navigate to the login activity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        val user = auth.currentUser
                        // updateUI(user)
                    } else {
                        // If sign up fails, display a message to the user
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // updateUI(null)
                    }
                }
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

