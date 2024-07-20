package com.example.studentdatabase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.studentdatabase.map.StudentMapViewActivity
import com.example.studentdatabase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var studentAdapter: StudentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if(currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val addStudent = binding.addStud
        val viewStudent = binding.viewStud
        val studentMapView = binding.llMap

        binding.logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        addStudent.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
        viewStudent.setOnClickListener {
            val intent = Intent(this, ViewStudentActivity::class.java)
            startActivity(intent)
        }
        studentMapView.setOnClickListener {
            val intent = Intent(this, StudentMapViewActivity::class.java)
            startActivity(intent)
        }
    }
}