package com.example.mayank.samplemap.sample.firebasescheduler

import com.example.mayank.samplemap.sample.Constants.showLogDebug
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

/**
 * Created by Mayank on 04/04/2018.
 */
class MyJobService : JobService() {

    private val TAG : String = MyJobService::class.java.simpleName

    override fun onStopJob(job: JobParameters): Boolean {
        showLogDebug(TAG, "On Stop Job Called")
        jobFinished(job, true)
        return false

    }

    override fun onStartJob(job: JobParameters): Boolean {
        showLogDebug(TAG, "On Start Job Called")
        jobFinished(job, true)
        return false
    }
}