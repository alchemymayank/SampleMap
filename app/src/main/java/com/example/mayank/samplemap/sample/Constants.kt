package com.example.mayank.samplemap.sample

import android.util.Log

/**
 * Created by Mayank on 03/04/2018.
 */
object Constants {

    var locationRequest : Boolean = false

    var API_BASE_ADDRESS = "https://googleapis.com/"

    fun showLogDebug(tag: String, message: String){
        Log.d(tag, message)
    }
}