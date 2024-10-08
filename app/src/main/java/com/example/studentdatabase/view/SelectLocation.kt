package com.example.studentdatabase.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.example.studentdatabase.R
import com.example.studentdatabase.databinding.ActivitySelectLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class SelectLocation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var googleMap: GoogleMap
    private var selectedLatLng: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = binding.searchView.query.toString()
                if (location.isNotEmpty()) {
                    val geocoder = Geocoder(this@SelectLocation)
                    var addressList: List<Address>? = null
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (!addressList.isNullOrEmpty()) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        selectedLatLng = latLng
                        googleMap.clear()
                        googleMap.addMarker(MarkerOptions().position(latLng).title(location).icon( BitmapDescriptorFactory.fromResource(R.drawable.pin_loc)))
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    } else {
                        Toast.makeText(
                            this@SelectLocation,
                            "Location not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
            binding.addLoc.setOnClickListener {
                selectedLatLng?.let {
//                    val intent = Intent(this, AddActivity::class.java).apply {
//                        putExtra("latitude", it.latitude)
//                        putExtra("longitude", it.longitude)
//                    }
//                    startActivity(intent)
//                    finish()
                    val resultIntent = Intent()
                    resultIntent.putExtra("lat", it.latitude)
                    resultIntent.putExtra("long", it.longitude)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } ?: run {
                    Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show()
                }

        }


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_close -> {
                // Handle close button click
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

    }
}