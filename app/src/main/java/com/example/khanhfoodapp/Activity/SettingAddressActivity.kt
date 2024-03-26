package com.example.khanhfoodapp.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.khanhfoodapp.MySingleton.mUser
import com.example.khanhfoodapp.MySingleton.myRef
import com.example.khanhfoodapp.R
import com.example.khanhfoodapp.databinding.ActivitySettingAddressBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.Locale

class SettingAddressActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivitySettingAddressBinding
    private lateinit var myMap: GoogleMap
    private val FINE_PERMISSION_CORE = 1
    private lateinit var currentLocation: android.location.Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLocation = android.location.Location("provider")
        currentLocation.latitude = 21.0278
        currentLocation.longitude = 105.8342

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
        initListener()
//        val mapFragment: SupportMapFragment =
//            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    private fun initListener() {
        binding.apply {
            btnBack.setOnClickListener { finish() }

            txtMyLocation.setOnClickListener {
                getLocation()
            }
            btnConfirm.setOnClickListener {
                if (checkEditAddress()) {
                    setUserAddress()
                }
            }
        }
    }

    private fun checkEditAddress(): Boolean {
        if (binding.editStreet.text.trim().isEmpty() || binding.editCity.text.trim().isEmpty()) {
            binding.textError.visibility = View.VISIBLE
            return false
        }
        binding.textError.visibility = View.GONE
        return true
    }

    private fun setUserAddress() {
        mUser.address = "${binding.editStreet.text.toString().trim()}, ${binding.editCity.text.toString().trim()}"
        myRef.child(mUser.id).child("address").setValue(mUser.address)
            .addOnCompleteListener {
                Toast.makeText(
                    this@SettingAddressActivity,
                    "Cập nhật địa chỉ thành công",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permission, FINE_PERMISSION_CORE)
            return
        }

        val task: Task<android.location.Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val mapFragment: SupportMapFragment =
                    supportFragmentManager.findFragmentById(R.id.map_my_location) as SupportMapFragment
                mapFragment.getMapAsync(this)


            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        val sydney = LatLng(currentLocation.latitude, currentLocation.longitude)

        //1650 Amphitheatre Pkwy, Mountain View, CA 94043, USA
        val options = MarkerOptions().position(sydney).title("My location")
        myMap.addMarker(options)
        myMap.moveCamera((CameraUpdateFactory.newLatLngZoom(sydney, 20f)))


        myMap.uiSettings.isZoomControlsEnabled = true
        myMap.uiSettings.isCompassEnabled = true

        //myMap.uiSettings.isZoomGesturesEnabled = false
        // myMap.uiSettings.isScrollGesturesEnabled = false

    }

    private fun getLocation() {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses =
                geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            val address = addresses!![0].getAddressLine(0)
            binding.editCity.setText(address)
            Log.e("khanh", address)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_PERMISSION_CORE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission is denied, please allow the permission",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


}