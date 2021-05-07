package com.vahitkeskin.kotlingooglemapslocationestimation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.vahitkeskin.kotlingooglemapslocationestimation.databinding.ActivityMapsBinding
import java.util.*

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(onMapLongClickLocation)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object: LocationListener {
            override fun onLocationChanged(location: Location) {
                val userNewLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(userNewLatLng).title("onLocationChanged"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userNewLatLng,15f))
                //estimationLocation(location)
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
                println(provider)
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
                println(provider)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
                println(provider)
            }
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)

            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastLocation?.let {l1 ->
                val lastKnowLocation = LatLng(l1.latitude, l1.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnowLocation).title("Last Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnowLocation,15f))

                estimationLocation(l1.latitude, l1.longitude)
            }

        }


    }

    val onMapLongClickLocation = object: GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng?) {
            p0?.let {selectedLocation ->
                estimationLocation(selectedLocation.latitude, selectedLocation.longitude)
            }
        }

    }

    private fun estimationLocation(latitude: Double, longitude: Double) {

        val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())

        try {
            val addressList = geocoder.getFromLocation(latitude, longitude,1)
            if (addressList != null && addressList.size >0) {

                val neighborhoodName = addressList[0].subLocality
                val streetNo = addressList[0].thoroughfare
                val doorNo = addressList[0].featureName
                val districtName = addressList[0].subAdminArea
                val provinceName = addressList[0].adminArea

                val selectedLatitude = addressList[0].latitude
                val selectedLongitude = addressList[0].longitude

                val openAddress = "$neighborhoodName Mah. $streetNo. Sk. No: $doorNo $districtName/$provinceName"

                val alert = AlertDialog.Builder(this)
                alert.setTitle("Your full address information")
                alert.setMessage(openAddress)
                alert.setPositiveButton("Yes"){ dialog, witch ->
                    println("Mahalle Adı : $neighborhoodName")
                    println("Sokak No    : $streetNo")
                    println("Kapı No     : $doorNo")
                    println("İlçe adı    : $districtName")
                    println("İl adı      : $provinceName")
                    println("Açık Adres  : $openAddress")

                    val intent = intent
                    intent.putExtra("provinceName",provinceName)
                    intent.putExtra("districtName",districtName)
                    intent.putExtra("openAddress",openAddress)
                    intent.putExtra("selectedLatitude",selectedLatitude)
                    intent.putExtra("selectedLongitude",selectedLongitude)
                    setResult(RESULT_OK,intent)
                    finish()
                }
                alert.setNegativeButton("No") {dialog, witch ->
                    true
                }
                alert.show()

            } else {
                Toast.makeText(this,"Address information of the selected location could not be found.",Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0) {
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}