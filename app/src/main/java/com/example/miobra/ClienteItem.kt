package com.example.miobra

data class ClienteItem(
    val id: String,
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val email: String = "",
    val obrasAsociadas: List<ObraItem> = emptyList()
)