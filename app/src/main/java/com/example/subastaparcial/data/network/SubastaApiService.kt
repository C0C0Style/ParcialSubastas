package com.example.subastaparcial.data.network

import com.example.subastaparcial.data.model.Subasta
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SubastaApiService {
    @GET("subastas")
    suspend fun obtenerSubastas(): List<Subasta>

    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: Subasta): retrofit2.Response<Unit>
}
