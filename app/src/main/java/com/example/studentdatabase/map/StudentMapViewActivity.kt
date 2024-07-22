package com.example.studentdatabase.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.studentdatabase.R
import com.example.studentdatabase.data.database.StudentsDatabase
import com.example.studentdatabase.data.model.Student
import com.example.studentdatabase.data.repository.StudentsRepository
import com.example.studentdatabase.databinding.ActivityStudentMapViewBinding
import com.example.studentdatabase.viewmodel.StudentViewModelFactory
import com.example.studentdatabase.viewmodel.StudentsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class StudentMapViewActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityStudentMapViewBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var studentViewModel: StudentsViewModel

    private var currentMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentMapViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Enable the back button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.navigationIcon = resources.getDrawable(R.drawable.back_arrow)

        // Set the title or label for the toolbar
        supportActionBar?.title = "Map View"
        toolbar.setTitleTextColor(Color.WHITE)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Setup ViewModel
        val database = StudentsDatabase.getDatabase(this)
        val repository = StudentsRepository(database.studentDao())
        val viewModelFactory = StudentViewModelFactory(repository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentsViewModel::class.java)


        studentViewModel.students.observe(this) { students ->
            // Ensure the map is initialized before adding markers
            if (::mMap.isInitialized) {
                students.forEach { student ->
                    Log.d(
                        "location",
                        "${extractValidCharacters(student.latitude).toDouble()} , ${
                            extractValidCharacters(student.longitude).toDouble()
                        }"
                    )
                    val lat = extractValidCharacters(student.latitude).toDoubleOrNull()
                    val lng = extractValidCharacters(student.longitude).toDoubleOrNull()
                    val image = student.imagUri.toString()



                    if (lat != null && lng != null) {
                        val latLng = LatLng(lat, lng)
                        Toast.makeText(this,"The Uri of your image is: $image", Toast.LENGTH_SHORT).show()
                        val customMarkerBitmap = createCustomMarker(this, image)

                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(student.name)
                                .icon(BitmapDescriptorFactory.fromBitmap(customMarkerBitmap))
                        )

                        marker?.tag = student
                    } else {
                        Log.e(
                            "StudentMapViewActivity",
                            "Invalid coordinates for student: ${student.name}. Latitude: $lat, Longitude: $lng"
                        )
                    }
                }

                // Optionally adjust the camera to fit all markers
                if (students.isNotEmpty()) {
                    val bounds = LatLngBounds.Builder()
                    students.forEach { student ->
                        val lat = extractValidCharacters(student.latitude).toDoubleOrNull()
                        val lng = extractValidCharacters(student.longitude).toDoubleOrNull()
                        if (lat != null && lng != null) {
                            bounds.include(LatLng(lat, lng))
                        }
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
                }
            }
        }
        studentViewModel.getAllStudents()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        mMap.setOnMarkerClickListener { clickedMarker ->
            currentMarker?.let {
                it.setIcon((it.tag as Student).imagUri?.let { it1 ->
                    createCustomMarker(this,
                        it1
                    )
                }?.let { it2 -> BitmapDescriptorFactory.fromBitmap(it2) })
            }

            clickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map))
            clickedMarker.showInfoWindow()

            currentMarker = clickedMarker
            true
        }

    }

    fun extractValidCharacters(input: String): String {
        val regex = Regex("[^0-9+\\-\\.]")
        return input.replace(regex, "")
    }

    private fun createCustomMarker(context: Context, imageUri: String): Bitmap {
        val markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker, null)

        val markerImage = markerView.findViewById<ImageView>(R.id.marker_image)

        // Load the image using Glide
        Glide.with(context)
            .load(imageUri)
            .into(markerImage)

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        markerView.buildDrawingCache()

        val returnedBitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val drawable = markerView.background
        if (drawable != null) {
            drawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        markerView.draw(canvas)
        return returnedBitmap
    }

}