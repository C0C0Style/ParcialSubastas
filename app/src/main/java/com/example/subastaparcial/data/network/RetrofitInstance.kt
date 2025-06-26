package com.example.subastaparcial.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: SubastaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.20.27:3000/")
            //.baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SubastaApiService::class.java)
    }
}
