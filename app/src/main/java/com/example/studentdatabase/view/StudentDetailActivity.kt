package com.example.studentdatabase.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.studentdatabase.R
import com.example.studentdatabase.data.database.StudentsDatabase
import com.example.studentdatabase.data.repository.StudentsRepository
import com.example.studentdatabase.databinding.ActivityStudentDetailBinding
import com.example.studentdatabase.viewmodel.StudentViewModelFactory
import com.example.studentdatabase.viewmodel.StudentsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso

class StudentDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityStudentDetailBinding
    private lateinit var studentViewModel: StudentsViewModel
    private lateinit var googleMap: GoogleMap
    private var receivedLatLng: LatLng? = null
//    private var latitude : Double = 0.0
//    private var longitude : Double = 0.0
companion object {

    private const val DEFAULT_ZOOM_LEVEL = 15f
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.back_arrow)
        supportActionBar?.title = "Student Details"
        toolbar.setTitleTextColor(Color.WHITE)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val studentId = intent.getLongExtra("STUDENT_ID", -1L)

        if (studentId == -1L) {
            Toast.makeText(this, "Invalid Student ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val database = StudentsDatabase.getDatabase(this)
        val repository = StudentsRepository(database.studentDao())
        val viewModelFactory = StudentViewModelFactory(repository)
        studentViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentsViewModel::class.java)

        studentViewModel.getStudentById(studentId).observe(this) { student ->
            student?.let {
                Picasso.get()
                    .load(it.imagUri)
//                    .placeholder(R.drawable.chotu)
//                    .error(R.drawable.chotu)
                    .into(binding.imageView6)

                Log.d("StudentDetailActivity", "Received Student ID: ${it.imagUri}")
                binding.name.text = it.name
                binding.classSection.text = "${it.standard} Standard - ${it.section} Section"
                binding.school.text = it.school
                binding.genderOpt.text = it.gender
                binding.dobOpt.text = it.dob
                binding.bloodOpt.text = it.blood
                binding.fatopt.text = it.father
                binding.motopt.text = it.mother
                binding.conopt.text = it.contact
                binding.emopt.text = it.emergencyContact
                binding.rdop1.text = it.address1
                binding.rdop2.text = it.address2
                binding.rdop3.text = it.city
                binding.rdop4.text = it.state
                binding.rdop5.text = it.zip

                receivedLatLng = LatLng(extractValidCharacters(it.latitude).toDouble(), extractValidCharacters(it.longitude).toDouble())
                if (receivedLatLng != null) {
                    // Update the map if it's already ready
                    if (::googleMap.isInitialized) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            receivedLatLng!!,
                            AddActivity.DEFAULT_ZOOM_LEVEL
                        ))
                        googleMap.addMarker(MarkerOptions().position(receivedLatLng!!).title("Selected Location").icon( BitmapDescriptorFactory.fromResource(R.drawable.pin_loc)))
                    } else {
                        // Save the location for when the map is ready
                        this.receivedLatLng = receivedLatLng
                    }
                    Toast.makeText(this, "Selected location: $receivedLatLng", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No location selected", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Student data not found", Toast.LENGTH_SHORT).show()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.small_map_container1) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
//        receivedLatLng?.let {
//                googleMap.addMarker(MarkerOptions().position(it).title("Selected Location"))
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
//
//        }

        googleMap.uiSettings.isZoomControlsEnabled = true

        // Check if we have a receivedLatLng and use it to set the map location
        receivedLatLng?.let {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, AddActivity.DEFAULT_ZOOM_LEVEL))
            map.addMarker(MarkerOptions().position(it).title("Selected Location").icon( BitmapDescriptorFactory.fromResource(R.drawable.pin_loc)))
        }
    }
    fun extractValidCharacters(input: String): String {
        val regex = Regex("[^0-9+\\-\\.]")
        return input.replace(regex, "")
    }
}