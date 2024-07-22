package com.example.studentdatabase.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdatabase.data.database.StudentsDatabase
import com.example.studentdatabase.data.repository.StudentsRepository
import com.example.studentdatabase.databinding.ActivityViewStudentBinding
import com.example.studentdatabase.viewmodel.StudentViewModelFactory
import com.example.studentdatabase.viewmodel.StudentsViewModel
import androidx.lifecycle.Observer
import com.example.studentdatabase.R

class ViewStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewStudentBinding
    private lateinit var studentsViewModel: StudentsViewModel
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.back_arrow)
        supportActionBar?.title = "View Student Data"
        toolbar.setTitleTextColor(Color.WHITE)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val database = StudentsDatabase.getDatabase(this)
        val repository = StudentsRepository(database.studentDao())
        val viewModelFactory = StudentViewModelFactory(repository)
        studentsViewModel = ViewModelProvider(this, viewModelFactory).get(StudentsViewModel::class.java)

        studentAdapter = StudentAdapter { student ->
            Log.d("ViewStudentActivity", "Passing Student ID: ${student.id}")
            Log.d("ViewStudentActivity", "passing student imagUri ${student.imagUri}")
            val intent = Intent(this, StudentDetailActivity::class.java).apply {
                putExtra("STUDENT_ID", student.id)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ViewStudentActivity)
            adapter = studentAdapter
        }

        studentsViewModel.students.observe(this, Observer { students ->
            students?.let { studentAdapter.submitList(it) }
        })

//        val loggedInUser = getLoggedInUserFromPreferences()
//        if (loggedInUser != null) {
//            studentsViewModel.getStudentsByUser(loggedInUser)
//        }

        studentsViewModel.getAllStudents()
    }
    private fun getLoggedInUserFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("logged_in_user", null)
    }
}

