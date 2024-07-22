package com.example.studentdatabase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdatabase.data.model.Student
import com.example.studentdatabase.data.repository.StudentsRepository
import kotlinx.coroutines.launch

class StudentsViewModel(private val repository: StudentsRepository) : ViewModel() {

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> get() = _students

    fun insert(student: Student) = viewModelScope.launch {
        repository.insert(student)
        getAllStudents()
    }
//    fun getAllStudents(): LiveData<Student> {
//        return repository.getAllStudents()
//    }

    fun getAllStudents() = viewModelScope.launch {
        _students.postValue(repository.getAllStudents())
    }

//    fun getStudentsByUser(user: String) = viewModelScope.launch {
//        _students.postValue(repository.getStudentsByUser(user))
//    }

    fun getStudentById(id: Long): LiveData<Student> {
        return repository.getStudentById(id)
    }

}