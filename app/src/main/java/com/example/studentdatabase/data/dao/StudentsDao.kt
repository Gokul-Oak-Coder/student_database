package com.example.studentdatabase.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studentdatabase.data.model.Student

@Dao
interface StudentsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Long): LiveData<Student>

    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<Student>


}