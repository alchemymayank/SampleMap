package com.example.mayank.samplemap.sample.mymap

import android.telecom.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by Mayank on 4/16/2018.
 */
interface IGoogleMapAPi {

    @GET
    fun getNfcLocations(@Url url : String): retrofit2.Call<String>
}