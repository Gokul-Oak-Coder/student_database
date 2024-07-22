package com.example.studentdatabase.view

import android.annotation.SuppressLint
import android.provider.MediaStore
import android.widget.RadioButton
import android.widget.RadioGroup
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.studentdatabase.R
import com.example.studentdatabase.data.database.StudentsDatabase
import com.example.studentdatabase.data.model.Student
import com.example.studentdatabase.data.repository.StudentsRepository
import com.example.studentdatabase.databinding.ActivityAddBinding
import com.example.studentdatabase.viewmodel.StudentViewModelFactory
import com.example.studentdatabase.viewmodel.StudentsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import java.util.*

class AddActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityAddBinding
    private lateinit var googleMap: GoogleMap
    private var receivedLatLng: LatLng? = null
    private var isImageSelected = false
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var studentsViewModel: StudentsViewModel

    companion object {
        const val SELECT_LOCATION_REQUEST_CODE = 1
        const val DEFAULT_ZOOM_LEVEL = 15f
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = StudentsDatabase.getDatabase(this)
        val repository = StudentsRepository(database.studentDao())
        val viewModelFactory = StudentViewModelFactory(repository)

        studentsViewModel =
            ViewModelProvider(this, viewModelFactory).get(StudentsViewModel::class.java)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.back_arrow)
        supportActionBar?.title = "Add Student"
        toolbar.setTitleTextColor(Color.WHITE)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.studImg.setOnClickListener {
            openImagePicker()
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.small_map_container1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val standard = resources.getStringArray(R.array.standard)
        val section = resources.getStringArray(R.array.section)
        val blood = resources.getStringArray(R.array.blood_group)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, standard)
        val adapter1 = ArrayAdapter(this, R.layout.dropdown_item, section)
        val adapter2 = ArrayAdapter(this, R.layout.dropdown_item, blood)
        binding.standardEt.setAdapter(adapter)
        binding.sectionEt.setAdapter(adapter1)
        binding.bloodEt.setAdapter(adapter2)

//        val latitude = intent.getDoubleExtra("lat", 0.0)
//        val longitude = intent.getDoubleExtra("long", 0.0)
//
//        receivedLatLng = if (latitude != 0.0 && longitude != 0.0) {
//            LatLng(latitude, longitude)
//        } else {
//            null
//        }

//        binding.locLatData.text = "Latitude $latitude"
//        binding.locLongData.text = "Latitude $longitude"

//        binding.selectLocation.setOnClickListener {
//            val intent = Intent(this, SelectLocation::class.java)
//            startActivity(intent)
//            Toast.makeText(this, "Tapped on select location", Toast.LENGTH_SHORT).show()
//        }
        binding.selectLocation.setOnClickListener {
            val intent = Intent(this, SelectLocation::class.java)
            startActivityForResult(intent, SELECT_LOCATION_REQUEST_CODE)
            Toast.makeText(this, "Tapped on select location", Toast.LENGTH_SHORT).show()
        }
        val dobText = binding.dobEt
        dobText.setOnClickListener {
            showDatePickerDialog(dobText)
        }
       val loggedInUser = getLoggedInUserFromPreferences()

        binding.imageView5.setOnClickListener {
            if (loggedInUser != null) {
                Toast.makeText(this, loggedInUser,Toast.LENGTH_LONG).show()
                //Log.d("user",loggedInUser)
            }
            else{
                Toast.makeText(this, "May be user is null", Toast.LENGTH_SHORT).show()
            }
        }



        binding.submitBtn.setOnClickListener {

            val name = binding.nameEt.text.toString()
            val stand = binding.standardEt.text.toString()
            val sec = binding.sectionEt.text.toString()
            val gender = getSelectedGender(binding.radioGender)
            val dob = binding.dobEt.text.toString()
            val long = binding.locLongData.text.toString()
            val lat = binding.locLatData.text.toString()
            val school = binding.schoolEt.text.toString()
            val blood = binding.bloodEt.text.toString()
            val father = binding.fatherEt.text.toString()
            val mother = binding.motherEt.text.toString()
            val contact = binding.contactEt.text.toString()
            val address1 = binding.address1Et.text.toString()
            val address2 = binding.address2Et.text.toString()
            val city = binding.cityEt.text.toString()
            val state = binding.stateEt.text.toString()
            val zip = binding.zipEt.text.toString()
            val emergency = binding.emergencyEt.text.toString()


                if (name.isNotEmpty() && stand.isNotEmpty() &&
                    sec.isNotEmpty() && dob.isNotEmpty() &&
                    gender.isNotEmpty() && long.isNotEmpty() && lat.isNotEmpty() &&
                    school.isNotEmpty() && blood.isNotEmpty() &&
                    father.isNotEmpty() && mother.isNotEmpty() &&
                    address1.isNotEmpty() && city.isNotEmpty() &&
                    state.isNotEmpty() && zip.isNotEmpty()
                ) {

                    val student = Student(
                        name = name,
                        standard = stand,
                        section = sec,
                        gender = gender,
                        dob = dob,
                        latitude = lat,
                        longitude = long,
                        school = school,
                        blood = blood,
                        father = father,
                        mother = mother,
                        contact = contact,
                        address1 = address1,
                        address2 = address2,
                        city = city,
                        state = state,
                        zip = zip,
                        emergencyContact = emergency,
                        imagUri = imageUri.toString(),
                        //user = loggedInUser
                    )

                    studentsViewModel.insert(student)
                    Log.d("img", "img uri ${student.imagUri}")
                    Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after saving
                } else {
                    Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
//        receivedLatLng?.let {
//            googleMap.addMarker(MarkerOptions().position(it).title("Selected Location"))
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
//        }

        googleMap.uiSettings.isZoomControlsEnabled = true

        // Check if we have a receivedLatLng and use it to set the map location
        receivedLatLng?.let {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM_LEVEL))
            map.addMarker(MarkerOptions().position(it).title("Selected Location").icon( BitmapDescriptorFactory.fromResource(R.drawable.pin_loc)))
        }
    }

    private fun showDatePickerDialog(dobEditText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dobEditText.setText(selectedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun getSelectedGender(radioGroup: RadioGroup): String {
        val selectedId = radioGroup.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectedId)
        return radioButton.text.toString()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            isImageSelected = true
            //Glide.with(this).load(imageUri).into(binding.studImg)
            Picasso.get()
                .load(imageUri)
                .into(binding.studImg)
        }
        if (requestCode == SELECT_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getDoubleExtra("lat", 0.0) ?: 0.0
            val longitude = data?.getDoubleExtra("long", 0.0) ?: 0.0
            binding.locLatData.text = "Latitude $latitude"
            binding.locLongData.text = "Latitude $longitude"
            val receivedLatLng = if (latitude != 0.0 && longitude != 0.0) {
                LatLng(latitude, longitude)
            } else {
                null
            }

            if (receivedLatLng != null) {
                // Update the map if it's already ready
                if (::googleMap.isInitialized) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(receivedLatLng, DEFAULT_ZOOM_LEVEL))
                    googleMap.addMarker(MarkerOptions().position(receivedLatLng).title("Selected Location").icon( BitmapDescriptorFactory.fromResource(R.drawable.pin_loc)))
                } else {
                    // Save the location for when the map is ready
                    this.receivedLatLng = receivedLatLng
                }
                Toast.makeText(this, "Selected location: $receivedLatLng", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No location selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getLoggedInUserFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("logged_in_user", null)
    }
}