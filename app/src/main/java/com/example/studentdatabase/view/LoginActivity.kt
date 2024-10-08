package com.example.studentdatabase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.studentdatabase.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val loginBtn = binding.btnLogin
        val signup = binding.signupBtn

        signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginBtn.setOnClickListener {
                // Retrieve the email and password values here
                val email = binding.user.text.toString()
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
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        val user = auth.currentUser
                        if (user != null) {
                            Log.d("YourActivity", "User UID: ${user.uid}")
                            saveUserToPreferences(user.uid)
                        } else {
                            Log.e("YourActivity", "User is null.")
                            Toast.makeText(baseContext, "Failed to retrieve user.", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        //updateUI(null)
                    }
                }
            }
    }
    private fun saveUserToPreferences(uid: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("logged_in_user", uid)
        editor.apply()
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
