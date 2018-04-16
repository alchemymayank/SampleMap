package com.example.mayank.samplemap.sample.mymap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.mayank.samplemap.R
import com.example.mayank.samplemap.sample.Constants.showLogDebug
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by Mayank on 4/16/2018.
 */
class MyMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MyMapActivity::class.java.simpleName
    private lateinit var mMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        showLogDebug(TAG, "On Map Ready Called")
        mMap = googleMap

        val myHome = LatLng(23.300796, 77.397697)
        mMap.addMarker(MarkerOptions().position(myHome).title("My Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myHome))
    }
}