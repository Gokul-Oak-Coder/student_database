package com.example.studentdatabase.data.repository

import androidx.lifecycle.LiveData
import com.example.studentdatabase.data.dao.StudentsDao
import com.example.studentdatabase.data.model.Student

class StudentsRepository(private val studentDao: StudentsDao) {

    suspend fun insert(student: Student) {
        studentDao.insert(student)
    }

//    fun getAllStudents(): LiveData<Student>{
//        return studentDao.getAllStudents()
//    }
    suspend fun getAllStudents(): List<Student> {
        return studentDao.getAllStudents()
    }


//    suspend fun getStudentsByUser(user: String): List<Student> {
//        return studentDao.getStudentsByUser(user)
//    }

    fun getStudentById(id: Long): LiveData<Student> {
        return studentDao.getStudentById(id)
    }
}
