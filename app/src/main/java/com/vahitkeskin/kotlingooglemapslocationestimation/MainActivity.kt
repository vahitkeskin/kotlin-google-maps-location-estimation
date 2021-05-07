package com.vahitkeskin.kotlingooglemapslocationestimation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.vahitkeskin.kotlingooglemapslocationestimation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private var provinceName: String = ""
    private var districtName: String = ""
    private var openAddress: String = ""
    private var selectedLocationLat: Double = 0.0
    private var selectedLocationLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedMap = supportFragmentManager.findFragmentById(R.id.selectedMap) as SupportMapFragment
        selectedMap.getMapAsync(this)

    }

    fun saved(view: View) {
        val province = binding.userProvince.text.toString()
        val district = binding.userDistrict.text.toString()
        val address = binding.userOpenAddress.text.toString()
        if (province.isEmpty() || district.isEmpty() || address.isEmpty()) {
            Toast.makeText(this,"Province, district or address cannot be left blank",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this,"Your Address: $address",Toast.LENGTH_LONG).show()
        }
    }

    fun goToMaps(view: View) {
        val intent = Intent(this,MapsActivity::class.java)
        startActivityForResult(intent,1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {

                provinceName = data.getStringExtra("provinceName")!!
                districtName = data.getStringExtra("districtName")!!
                openAddress = data.getStringExtra("openAddress")!!
                selectedLocationLat = data.getDoubleExtra("selectedLatitude",0.0)
                selectedLocationLng = data.getDoubleExtra("selectedLongitude",0.0)

                binding.userProvince.setText(provinceName)
                binding.userDistrict.setText(districtName)
                binding.userOpenAddress.setText(openAddress)

                if (selectedLocationLat != 0.0 && selectedLocationLng != 0.0) {
                    binding.mapsLL.isVisible = true
                    val selectedLatLng = LatLng(selectedLocationLat, selectedLocationLng)
                    mMap.addMarker(MarkerOptions().position(selectedLatLng).title("Selected Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f))
                }

            }
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        p0?.let {googleMap ->
            mMap = googleMap
        }
    }
}