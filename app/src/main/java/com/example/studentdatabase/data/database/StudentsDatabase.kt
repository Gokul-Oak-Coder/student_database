package com.example.studentdatabase.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentdatabase.data.dao.StudentsDao
import com.example.studentdatabase.data.model.Student

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentsDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentsDao

    companion object {
        @Volatile
        private var INSTANCE: StudentsDatabase? = null

        fun getDatabase(context: Context): StudentsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentsDatabase::class.java,
                    "student_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}