package com.example.subastaparcial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastaparcial.data.model.Puja
import com.example.subastaparcial.data.repository.PujaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PujaViewModel : ViewModel() {

    private val repository = PujaRepository()

    private val _estadoPuja = MutableStateFlow<Result<Unit>?>(null)
    val estadoPuja = _estadoPuja.asStateFlow()

    private val _pujasExistentes = MutableStateFlow<List<Puja>>(emptyList())
    val pujasExistentes = _pujasExistentes.asStateFlow()

    private val _estadoEliminacion = MutableStateFlow<Result<Unit>?>(null)
    val estadoEliminacion = _estadoEliminacion.asStateFlow()

    fun guardarPuja(puja: Puja, isUpdate: Boolean) {
        viewModelScope.launch {
            try {
                val response = repository.enviarPuja(puja, isUpdate)
                if (response.isSuccessful) {
                    _estadoPuja.value = Result.success(Unit)
                } else {
                    _estadoPuja.value = Result.failure(Exception("Error al enviar puja"))
                }
            } catch (e: Exception) {
                _estadoPuja.value = Result.failure(e)
            }
        }
    }

    fun guardarPujaDetectando(nuevaPuja: Puja) {
        viewModelScope.launch {
            try {
                val yaExiste = repository.existePuja(nuevaPuja.subastaId, nuevaPuja.numero)
                guardarPuja(nuevaPuja, isUpdate = yaExiste)
            } catch (e: Exception) {
                _estadoPuja.value = Result.failure(e)
            }
        }
    }

    fun cargarPujas(subastaId: Int) {
        viewModelScope.launch {
            val lista = repository.obtenerPujasDeSubasta(subastaId)
            println("🟢 Pujas recibidas: $lista")
            _pujasExistentes.value = lista
        }
    }

    fun eliminarPuja(subastaId: Int, numero: Int) {
        viewModelScope.launch {
            _estadoEliminacion.value = try {
                val result = repository.eliminarPuja(subastaId, numero)
                if (result.isSuccess) {
                    cargarPujas(subastaId) // Recargar
                }
                result
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
