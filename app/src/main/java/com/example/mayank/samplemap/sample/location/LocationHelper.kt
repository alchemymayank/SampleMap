package com.example.mayank.samplemap.sample.location

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.location.Location
import android.widget.Toast
import com.example.mayank.samplemap.sample.Constants.showLogDebug
import com.example.mayank.samplemap.sample.firebasescheduler.TestJobService
import com.google.android.gms.location.*
import android.os.Looper
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.location.FusedLocationProviderClient


/**
 * Created by Mayank on 04/04/2018.
 */
class LocationHelper(testJobService: TestJobService, fusedLocationProviderClient: FusedLocationProviderClient, jobParameters: JobParameters?, locationCallback: LocationCallback) {

    private val TAG = LocationHelper::class.java.simpleName

    private var locationRequest: LocationRequest? = null

    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    val fusedProviderClient = fusedLocationProviderClient
    val testJobService = testJobService
    val jobParameters = jobParameters
    var locationCall = locationCallback
    var service: Boolean = false
    val location: Location? = null

    init {
//        startLocationUpdatesNew()
        getLastLocationNew()
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        showLogDebug(TAG, "Inside Get Last Location")
        fusedProviderClient.lastLocation.addOnSuccessListener { location ->

            showLogDebug(TAG, "Latitude : ${location.latitude}")
            showLogDebug(TAG, "Longitude : ${location.longitude}")

            val message = "Latitude : ${location.latitude} Longitude : ${location.longitude}"

            Toast.makeText(testJobService, message, Toast.LENGTH_SHORT).show()
            testJobService.jobFinished(jobParameters, false)
        }

        fusedProviderClient.lastLocation.addOnFailureListener { exception ->
            showLogDebug(TAG, "Exception : ${exception.message}")
        }
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        showLogDebug(TAG, "Inside Start Location Updates")
        fusedProviderClient.requestLocationUpdates(locationRequest,
                locationCall,
                null /* Looper */)

    }


    private fun onLocationChanged(location: Location) {

        showLogDebug(TAG, "Inside on Location changed")
        val NEW_TAG = "onLocationChanged"
        showLogDebug(NEW_TAG, "Latitude : ${location.latitude}")
        showLogDebug(NEW_TAG, "Longitude : ${location.longitude}")

        val message = "Latitude : ${location.latitude} Longitude : ${location.longitude}"
        Toast.makeText(testJobService, message, Toast.LENGTH_SHORT).show()
        testJobService.jobFinished(jobParameters, false)

    }

    fun stopLocationUpdates() {
        showLogDebug(TAG, "Stopping location updates")
        fusedProviderClient.removeLocationUpdates(LocationCallback())
    }

    // Trigger new location updates at interval
    @SuppressLint("RestrictedApi", "MissingPermission")
    protected fun startLocationUpdatesNew() {

        // Create the location request to start receiving updates

        locationRequest = LocationRequest()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = UPDATE_INTERVAL
        locationRequest?.fastestInterval = FASTEST_INTERVAL

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()

        builder.addLocationRequest(locationRequest!!)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(testJobService)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)

        getFusedLocationProviderClient(testJobService).requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                // do work here
                onLocationChanged(locationResult!!.lastLocation)
            }
        },
                Looper.myLooper())
        service = true


    }

    @SuppressLint("MissingPermission")
    fun getLastLocationNew() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        val locationClient = getFusedLocationProviderClient(testJobService)

        locationClient.lastLocation
                .addOnSuccessListener { location ->
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        onLocationChanged(location)
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("MapDemoActivity", "Error trying to get last GPS location")
                    e.printStackTrace()
                }
    }

}