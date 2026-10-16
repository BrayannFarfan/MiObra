package com.example.miobra


data class ObraItem(
    val id: Long = 0,
    val titulo: String,
    val cliente: String,
    val direccion: String,
    val estado: String,
    val imagenUrl: String? = null,
    val fotos: List<String> = emptyList(),
    val tareas: List<TareaItem> = emptyList(),
    val fecha: String
)

data class TareaItem(
    val id: Long = 0,
    val descripcion: String,
    val completada: Boolean = false
)