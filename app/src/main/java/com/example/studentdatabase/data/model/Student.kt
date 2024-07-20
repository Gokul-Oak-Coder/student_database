package com.example.studentdatabase.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var imagUri: String? = null,
    val name: String,
    var standard: String,
    var section : String,
    val school: String,
    val gender: String,
    val dob : String,
    val blood : String,
    val father : String,
    val mother : String,
    val contact : String,
    val address1 : String,
    val address2 : String? = null,
    val city : String,
    val state : String,
    val zip : String,
    val emergencyContact : String? = null,
    val latitude: String,
    val longitude: String
)
