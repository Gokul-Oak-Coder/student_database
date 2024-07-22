package com.example.studentdatabase.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.studentdatabase.R
import com.example.studentdatabase.data.model.Student
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    private val window: View = LayoutInflater.from(context).inflate(R.layout.custom_info_map, null)


    private fun renderWindowText(marker: Marker?, view: View) {
        val student = marker?.tag as? Student
        if (student != null) {
            val nameTextView = view.findViewById<TextView>(R.id.stud_name)
            val image = view.findViewById<ImageView>(R.id.marker_image1)

            nameTextView.text = student.name

            Glide.with(context)
                .load(student.imagUri)
                .into(image)
        }
    }

    override fun getInfoContents(marker: Marker): View? {
        renderWindowText(marker, window)
        return window
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}