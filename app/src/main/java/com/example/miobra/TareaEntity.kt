package com.example.miobra

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = ObraEntity::class,
            parentColumns = ["id"],
            childColumns = ["obraId"],
            onDelete = ForeignKey.CASCADE // Si se borra la obra, sus tareas se borran automáticamente
        )
    ],
    indices = [Index(value = ["obraId"])]
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val obraId: Long, // ID de la obra a la que pertenece
    val descripcion: String,
    val completada: Boolean = false
)