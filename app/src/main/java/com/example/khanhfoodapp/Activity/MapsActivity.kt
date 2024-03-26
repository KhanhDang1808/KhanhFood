package com.example.khanhfoodapp.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.example.khanhfoodapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.khanhfoodapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.tasks.Task
import java.io.IOException

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var myMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapSearch: SearchView

    //  private lateinit var currentLocation: android.location.Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapSearch = findViewById(R.id.map_search)
        setSupportActionBar(binding.toolbarMap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


//        currentLocation = android.location.Location("Khanh Food")
//        currentLocation.latitude = 21.0554643
//        currentLocation.longitude = 105.7442554

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        
        binding.toolbarTitle.setOnClickListener {
            myMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(21.0554643, 105.7442554),
                    20f
                )
            )
        }

        mapSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = mapSearch.query.toString()
                var addressList: List<Address>? = null

                val geocoder: Geocoder = Geocoder(this@MapsActivity)
                try {
                    addressList = geocoder.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList != null) {
                    if (addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        myMap.addMarker(MarkerOptions().position(latLng).title(location))
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                    }
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//        getLastLocation()
        mapFragment.getMapAsync(this)
    }

//    private fun getLastLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//            ActivityCompat.requestPermissions(this, permission, FINE_PERMISSION_CORE)
//            return
//        }
//
//        val task: Task<Location> = fusedLocationProviderClient.lastLocation
//        task.addOnSuccessListener { location ->
//            if (location != null) {
//                currentLocation = location
//                val mapFragment: SupportMapFragment =
//                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//                mapFragment.getMapAsync(this)
//            }
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == FINE_PERMISSION_CORE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Location permission is denied, please allow the permission",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//        }
//    }

    override fun onMapReady(googleMap: GoogleMap) {

        getMyLocation(googleMap)

//        val sydney = LatLng(currentLocation.latitude, currentLocation.longitude)
//        myMap.moveCamera((CameraUpdateFactory.newLatLng(sydney)))
//
//        //1650 Amphitheatre Pkwy, Mountain View, CA 94043, USA
//        val options = MarkerOptions().position(sydney).title("Your location")
//        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
//        myMap.addMarker(options)


        myMap.uiSettings.isZoomControlsEnabled = true
        myMap.uiSettings.isCompassEnabled = true

    }

    private fun getMyLocation(googleMap: GoogleMap) {
        myMap = googleMap

        // Add a marker in Sydney and move the camera
        val khanhFood = LatLng(21.0554643, 105.7442554)
        myMap.addMarker(MarkerOptions().position(khanhFood).title("Khanh Food"))
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(khanhFood, 20f))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_none -> myMap.mapType = GoogleMap.MAP_TYPE_NONE
            R.id.map_normal -> myMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.map_hybrid -> myMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.map_terrain -> myMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            R.id.map_satellite -> myMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val FINE_PERMISSION_CORE = 1
    }

}