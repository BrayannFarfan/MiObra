package com.example.miobra

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas WHERE obraId = :obraId")
    fun obtenerTareasPorObra(obraId: Long): Flow<List<TareaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: TareaEntity): Long

    @Update
    suspend fun actualizarTarea(tarea: TareaEntity): Unit

    @Delete
    suspend fun eliminarTarea(tarea: TareaEntity): Unit
}