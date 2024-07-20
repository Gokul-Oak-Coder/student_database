package com.example.studentdatabase.map


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.studentdatabase.R
import com.example.studentdatabase.data.database.StudentsDatabase
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
import com.google.android.gms.maps.model.MarkerOptions

class StudentMapViewActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityStudentMapViewBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var studentViewModel: StudentsViewModel

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
        studentViewModel = ViewModelProvider(this, viewModelFactory).get(StudentsViewModel::class.java)

        studentViewModel.students.observe(this) { students ->
            // Ensure the map is initialized before adding markers
            if (::mMap.isInitialized) {
                students.forEach { student ->
                    val lat = student.latitude.toDoubleOrNull()
                    val lng = student.longitude.toDoubleOrNull()

                    if (lat != null && lng != null) {
                        val latLng = LatLng(lat, lng)
                        mMap.addMarker(MarkerOptions().position(latLng).title(student.name))
                    } else {
                        Log.e("StudentMapViewActivity", "Invalid coordinates for student: ${student.name}. Latitude: $lat, Longitude: $lng")
                    }
                }

                // Optionally adjust the camera to fit all markers
                if (students.isNotEmpty()) {
                    val bounds = LatLngBounds.Builder()
                    students.forEach { student ->
                        val lat = student.latitude.toDoubleOrNull()
                        val lng = student.longitude.toDoubleOrNull()
                        if (lat != null && lng != null) {
                            bounds.include(LatLng(lat, lng))
                        }
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        val defaultLocation = LatLng(-34.0, 151.0) // Example location
//        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Marker"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        addCustomMarker()
       // loadStudentLocations()
    }
    private fun addCustomMarker(){
       // val location = LatLng(-34.0, 151.0)
        val markerOptions = MarkerOptions()
            //.position(location)
            .title("custom marker")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map))
        mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(345.67, 34.68), 20f))  // Adjust as needed
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}