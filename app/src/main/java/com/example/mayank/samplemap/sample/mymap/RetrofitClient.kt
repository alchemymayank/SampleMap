package com.example.mayank.samplemap.sample.mymap

import com.example.mayank.samplemap.sample.Constants.API_BASE_ADDRESS
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Mayank on 4/16/2018.
 */
class TokenService {

    @PublishedApi
    internal var retrofit: Retrofit

    init {

//        val gson = GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                .create()

//        val httpClient = OkHttpClient.Builder()
//                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
//                .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
//                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_ADDRESS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
    }

    inline fun <reified T> getService(): T {
        return retrofit.create(T::class.java)
    }

    fun <T> getService(service: Class<T>): T {
        return retrofit.create(service)
    }
}