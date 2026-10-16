package com.example.miobra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "obras")
data class ObraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val clienteNombre: String, // Nombre visible del cliente
    val clienteId: Long? = null, // ID relacional opcional con ClienteEntity
    val direccion: String,
    val estado: String, // "En curso", "Pendiente", "Terminado"
    val imagenUrl: String? = null,
    val fecha: String = "",
    val fotos: List<String> = emptyList() // Manejado por Converters
)