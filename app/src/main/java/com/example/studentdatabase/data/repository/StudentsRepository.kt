package com.example.studentdatabase.data.repository

import androidx.lifecycle.LiveData
import com.example.studentdatabase.data.dao.StudentsDao
import com.example.studentdatabase.data.model.Student

class StudentsRepository(private val studentDao: StudentsDao) {

    suspend fun insert(student: Student) {
        studentDao.insert(student)
    }

    suspend fun getAllStudents(): List<Student> {
        return studentDao.getAllStudents()
    }


    fun getStudentById(id: Long): LiveData<Student> {
        return studentDao.getStudentById(id)
    }
}
