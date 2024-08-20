package com.example.studentdatabase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
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
            // Retrieve the email, password, phone, and confirm password values
            val email = binding.userName.text.toString().trim()
            val password = binding.pass.text.toString().trim()
            val phone = binding.phoneNo.text.toString().trim()
            val conPass = binding.confPass.text.toString().trim()

            // Set the ProgressBar visibility
            binding.progressBar.visibility = View.VISIBLE

            // Check if any field is empty
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
            if (TextUtils.isEmpty(conPass)) {
                Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }


            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address (e.g., example@email.com)", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }


            val phonePattern = "^[0-9]{10}$"
            if (!phone.matches(phonePattern.toRegex())) {
                Toast.makeText(this, "Enter a valid phone number (should contain exactly 10 numbers)", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if(password.length < 6 ){
                Toast.makeText(this, "Password should contain atleast 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != conPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }


            // Check if email already exists
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods != null && signInMethods.isNotEmpty()) {
                            // Email is already in use
                            Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                        } else {
                            // Email is not in use, proceed with registration
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { signUpTask ->
                                    // Hide the ProgressBar
                                    binding.progressBar.visibility = View.GONE

                                    if (signUpTask.isSuccessful) {
                                        // If sign up is successful, navigate to the login activity
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // If sign up fails, display a message to the user
                                        Toast.makeText(
                                            baseContext,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        // If fetchSignInMethodsForEmail fails, display a message to the user
                        Toast.makeText(
                            baseContext,
                            "Error checking email.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
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

