package com.example.subastaparcial.data.repository

import android.util.Log
import com.example.subastaparcial.data.model.Subasta
import com.example.subastaparcial.data.network.RetrofitInstance

class SubastaRepository {

    suspend fun obtenerSubastas(): List<Subasta> {
        return try {
            val datos = RetrofitInstance.api.obtenerSubastas()
            Log.d("SubastaRepository", "Se recibieron ${datos.size} subastas.")
            datos
        } catch (e: Exception) {
            Log.e("SubastaRepository", "Error al obtener subastas: ${e.message}")
            emptyList()
        }
    }

    suspend fun crearSubasta(subasta: Subasta): Boolean {
        return try {
            RetrofitInstance.api.crearSubasta(subasta)
            Log.d("SubastaRepository", "Subasta creada correctamente.")
            true
        } catch (e: Exception) {
            Log.e("SubastaRepository", "Error al crear subasta: ${e.message}")
            false
        }
    }

    suspend fun obtenerSubastaPorId(id: Int): Subasta? {
        return try {
            RetrofitInstance.api.obtenerSubastaPorId(id)
        } catch (e: Exception) {
            Log.e("SubastaRepository", "Error al obtener subasta por ID: ${e.message}")
            null
        }
    }

}
