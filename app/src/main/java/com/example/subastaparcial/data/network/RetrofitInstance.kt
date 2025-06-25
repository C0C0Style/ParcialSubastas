package com.example.subastaparcial.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: SubastaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://0.0.0.0:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SubastaApiService::class.java)
    }
}
