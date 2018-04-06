package com.example.mayank.samplemap.sample.firebasescheduler

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mayank.samplemap.R
import com.example.mayank.samplemap.sample.Constants.showLogDebug
import com.firebase.jobdispatcher.*
import com.firebase.jobdispatcher.GooglePlayDriver
import android.app.job.JobInfo
import android.content.ComponentName
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.R.string.cancel
import android.app.ActivityManager
import android.support.v4.app.FragmentActivity
import android.util.Log


class FirebaseScheduler : AppCompatActivity() {

    private val TAG: String = FirebaseScheduler::class.java.simpleName

    lateinit var dispatcher: FirebaseJobDispatcher
    private var jobScheduler : JobScheduler? = null
    private val jobId = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_scheduler)

        dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
    }

    fun checkService(view: View){
        showLogDebug(TAG, "Check Service Button Clicked")
        val isRunning = isLocationTrackingServiceRunning
        if (isRunning){
            showLogDebug(TAG, "Service is running")
        }else{
            showLogDebug(TAG, "Service stopped")
        }
    }

    fun startService(view: View) {
        showLogDebug(TAG, "Start Service Button Clicked")
        startJobScheduler()
    }



    @SuppressLint("MissingPermission")
    private fun startJobScheduler(){
        showLogDebug(TAG, "Job Scheduler starting...")


        jobScheduler?.schedule(JobInfo.Builder(jobId,
                ComponentName(this, TestJobService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(10000)
                .setPersisted(true)
                .build())
    }

    private fun stopJobScheduler(){
        showLogDebug(TAG, "Job Scheduler Stopping...")
        for (jobInfo in jobScheduler?.allPendingJobs!!) {
            if (jobInfo.id == jobId) {
                jobScheduler!!.cancel(jobId)
                showLogDebug(TAG, "Cancelled Job with ID:" + jobId)
            }
        }
    }

    private fun startFirebaseJobDispatcher(){
        showLogDebug(TAG, "Firebase job dispatcher starting...")
        val myJob = dispatcher.newJobBuilder()
                .setService(MyJobService::class.java) // the JobService that will be called
                .setTag("my-unique-tag") // uniquely identifies the job
                .setLifetime(Lifetime.FOREVER) // Set life time of job to forever
                .setRecurring(true) // Repeating lifetime of the application
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(5, 10))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(false)
                .setConstraints(// only run on an unmetered network
                        Constraint.ON_ANY_NETWORK)
                .build()

        dispatcher.mustSchedule(myJob)
        showLogDebug(TAG, "Firebase dispatcher Job Scheduled Successfully")
    }

    private fun stopFirebaseJobDispatcher(){
        showLogDebug(TAG, "Firebase job dispatcher stopping...")
        dispatcher.cancel("my-unique-tag");
        showLogDebug(TAG, "Firebase dispatcher Job Cancelled")
    }

    fun stopService(view: View) {
        showLogDebug(TAG, "Stop Service Button Clicked")
        stopJobScheduler()
    }

    private val isLocationTrackingServiceRunning: Boolean
        get() = isServiceRunning(TestJobService::class.java)

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        for (jobInfo in jobScheduler?.allPendingJobs!!) {
            if (jobInfo.id == jobId) {
                return true
            }
        }
        return false
    }
}
