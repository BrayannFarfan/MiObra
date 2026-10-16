package com.example.miobra

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun obtenerTodosLosClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id LIMIT 1")
    suspend fun obtenerClientePorId(id: Long): ClienteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCliente(cliente: ClienteEntity): Long

    @Update
    suspend fun actualizarCliente(cliente: ClienteEntity): Unit

    @Delete
    suspend fun eliminarCliente(cliente: ClienteEntity): Unit
}