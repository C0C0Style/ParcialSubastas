package com.example.subastaparcial.data.repository

import com.example.subastaparcial.data.model.Puja
import com.example.subastaparcial.data.network.RetrofitInstance
import retrofit2.Response

class PujaRepository {
    suspend fun enviarPuja(puja: Puja, isUpdate: Boolean): Response<Unit> {
        return if (isUpdate) {
            RetrofitInstance.api.actualizarPuja(puja.subastaId, puja.numero, puja)
        } else {
            RetrofitInstance.api.crearPuja(puja)
        }
    }

    suspend fun existePuja(subastaId: Int, numero: Int): Boolean {
        return try {
            val response = RetrofitInstance.api.obtenerPuja(subastaId, numero)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerPuja(subastaId: Int, numero: Int): Puja? {
        return try {
            val response = RetrofitInstance.api.obtenerPuja(subastaId, numero)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerPujasDeSubasta(subastaId: Int): List<Puja> {
        return try {
            val response = RetrofitInstance.api.obtenerPujasDeSubasta(subastaId)
            if (response.isSuccessful) response.body() ?: emptyList()
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun eliminarPuja(subastaId: Int, numero: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.eliminarPuja(subastaId, numero)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}