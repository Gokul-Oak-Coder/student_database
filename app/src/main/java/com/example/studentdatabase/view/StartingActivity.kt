package com.example.studentdatabase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.studentdatabase.databinding.ActivityStartingBinding

class StartingActivity : AppCompatActivity() {
   public lateinit var binding : ActivityStartingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.btnLogin
        val signup = binding.signupBtn

        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        signup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}