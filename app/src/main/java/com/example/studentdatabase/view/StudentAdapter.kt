package com.example.studentdatabase.view

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.ListAdapter
import com.example.studentdatabase.databinding.StudentListBinding
import com.example.studentdatabase.R
import com.example.studentdatabase.data.model.Student
import com.squareup.picasso.Picasso


class StudentAdapter(private val onStudentClick: (Student) -> Unit) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = StudentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)
        holder.itemView.setOnClickListener {
            onStudentClick(student)
        }
    }

    class StudentViewHolder(private val binding: StudentListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student) {
            binding.apply {
                nameStud.text = "${student.id}, ${student.name}"
                Log.d("ViewStudentActivity", "passing student imagUri ${student.imagUri}")
                detStud.text = "${student.standard} - ${student.section} / ${student.school}"
                Picasso.get()
                    .load(student.imagUri)
//                    .placeholder(R.drawable.chotu)
//                    .error(R.drawable.chotu)
                    .into(binding.imgStud)
            }
        }
    }

    class StudentComparator : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}