package com.example.subastaparcial.data.network

import com.example.subastaparcial.data.model.Puja
import com.example.subastaparcial.data.model.Subasta

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SubastaApiService {
    @GET("subastas")
    suspend fun obtenerSubastas(): List<Subasta>

    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: Subasta): retrofit2.Response<Unit>

    @POST("pujas")
    suspend fun crearPuja(@Body puja: Puja): Response<Unit>

    @PUT("pujas/{subastaId}/{numero}")
    suspend fun actualizarPuja(
        @Path("subastaId") subastaId: Int,
        @Path("numero") numero: Int,
        @Body puja: Puja
    ): Response<Unit>

    @GET("pujas/{subastaId}/{numero}")
    suspend fun obtenerPuja(
        @Path("subastaId") subastaId: Int,
        @Path("numero") numero: Int
    ): Response<Puja>

    @GET("pujas/subasta/{subastaId}")
    suspend fun obtenerPujasDeSubasta(
        @Path("subastaId") subastaId: Int
    ): Response<List<Puja>>

    @GET("subastas/{id}")
    suspend fun obtenerSubastaPorId(@Path("id") id: Int): Subasta

    @DELETE("pujas/{subastaId}/{numero}")
    suspend fun eliminarPuja(
        @Path("subastaId") subastaId: Int,
        @Path("numero") numero: Int
    ): Response<Unit>

}
