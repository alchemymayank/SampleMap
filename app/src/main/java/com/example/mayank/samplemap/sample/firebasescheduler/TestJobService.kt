package com.example.mayank.samplemap.sample.firebasescheduler

import android.app.job.JobParameters
import android.app.job.JobService
import android.location.Location
import android.widget.Toast
import com.example.mayank.samplemap.sample.Constants.showLogDebug
import com.example.mayank.samplemap.sample.location.LocationHelper
import com.google.android.gms.location.*

/**
 * Created by Mayank on 04/04/2018.
 */
class TestJobService : JobService() {

    private val TAG : String = TestJobService::class.java.simpleName
    var jobParameters : JobParameters? = null

    var locationHelper: LocationHelper? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProvider : FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = LocationCallback()

    }


    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        showLogDebug(TAG, "On Stop Job Called")
        Toast.makeText(this, "On stop job called", Toast.LENGTH_SHORT).show()
        jobFinished(jobParameters, false)
        return false
    }





    override fun onStartJob(jobParameters: JobParameters?): Boolean {
        this.jobParameters = jobParameters
        showLogDebug(TAG, "On start job called")
        locationHelper = LocationHelper(this,fusedLocationProvider, jobParameters, locationCallback)
//        Toast.makeText(this, "On start job called", Toast.LENGTH_SHORT).show()
//        jobFinished(jobParameters, false)

        return false
    }





    override fun onDestroy() {
        super.onDestroy()
        showLogDebug(TAG, "On Destroy Called")

        locationHelper?.stopLocationUpdates()
        jobFinished(jobParameters, false)


    }


}