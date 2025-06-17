package com.example.subastaparcial.data.model

data class Subasta(
    val id: Int = 0,
    val nombre: String = "",
    val ofertaMinima: Double = 0.0,
    val fechaSubasta: String = "",
    val imagen: String = "",
    val inscritos: Int = 0
)
