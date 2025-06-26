package com.example.subastaparcial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastaparcial.data.model.Subasta
import com.example.subastaparcial.data.repository.SubastaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalleSubastaViewModel : ViewModel() {
    private val _subasta = MutableStateFlow<Subasta?>(null)
    val subasta: StateFlow<Subasta?> = _subasta

    private val repository = SubastaRepository()

    fun cargarSubastaPorId(id: Int) {
        viewModelScope.launch {
            val nueva = repository.obtenerSubastaPorId(id)
            _subasta.value = nueva
        }
    }
}
