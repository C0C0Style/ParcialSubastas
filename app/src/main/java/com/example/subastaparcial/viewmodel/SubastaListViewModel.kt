package com.example.subastaparcial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastaparcial.data.model.Subasta
import com.example.subastaparcial.data.repository.SubastaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubastaListViewModel : ViewModel() {

    private val repository = SubastaRepository()

    private val _subastas = MutableStateFlow<List<Subasta>>(emptyList())
    val subastas: StateFlow<List<Subasta>> = _subastas

    // ✅ Subasta seleccionada que se usará en DetalleSubastaScreen
    var subastaSeleccionada: Subasta? = null

    fun cargarSubastas() {
        viewModelScope.launch {
            _subastas.value = repository.obtenerSubastas()
        }
    }
}
