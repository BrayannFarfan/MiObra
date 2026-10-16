package com.example.miobra

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ObraDao {
    @Query("SELECT * FROM obras ORDER BY id DESC")
    fun obtenerTodasLasObras(): Flow<List<ObraEntity>>

    @Query("SELECT * FROM obras WHERE id = :id LIMIT 1")
    suspend fun obtenerObraPorId(id: Long): ObraEntity?

    @Query("SELECT * FROM obras WHERE clienteId = :clienteId")
    fun obtenerObrasPorCliente(clienteId: Long): Flow<List<ObraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarObra(obra: ObraEntity): Long

    @Update
    suspend fun actualizarObra(obra: ObraEntity): Unit

    @Delete
    suspend fun eliminarObra(obra: ObraEntity): Unit
}