package com.example.miobra

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VeronaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.obtenerBaseDatos(application)
    private val obraDao = database.obraDao()
    private val clienteDao = database.clienteDao()
    private val tareaDao = database.tareaDao()

    // FLUX DE OBRAS: Obtiene obras y les mapea sus tareas correspondientes
    val obras: StateFlow<List<ObraItem>> = obraDao.obtenerTodasLasObras()
        .flatMapLatest { listaObrasEntity ->
            if (listaObrasEntity.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flowsTareas = listaObrasEntity.map { obraEntity ->
                    tareaDao.obtenerTareasPorObra(obraEntity.id).map { tareasEntity ->
                        ObraItem(
                            id = obraEntity.id,
                            titulo = obraEntity.titulo,
                            cliente = obraEntity.clienteNombre,
                            direccion = obraEntity.direccion,
                            estado = obraEntity.estado,
                            imagenUrl = obraEntity.imagenUrl,
                            fotos = obraEntity.fotos,
                            fecha = obraEntity.fecha,
                            tareas = tareasEntity.map { TareaItem(it.id, it.descripcion, it.completada) },
                        )
                    }
                }
                combine(flowsTareas) { it.toList() }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // FLUX DE CLIENTES
    val clientes: StateFlow<List<ClienteItem>> = clienteDao.obtenerTodosLosClientes()
        .map { lista ->
            lista.map { cliente ->
                ClienteItem(
                    id = cliente.id.toString(),
                    nombre = cliente.nombre,
                    telefono = cliente.telefono,
                    direccion = cliente.direccion,
                    email = cliente.email
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ACCIONES DE OBRAS ---
    // --- ACCIONES DE OBRAS ---
    fun agregarObra(
        titulo: String,
        cliente: String,
        direccion: String,
        fecha: String,
        imagenUrl: String? = null // <-- Agregamos el parámetro opcional para la imagen
    ) {
        viewModelScope.launch {
            val listaFotosInicial = if (!imagenUrl.isNullOrEmpty()) listOf(imagenUrl) else emptyList()

            obraDao.insertarObra(
                ObraEntity(
                    titulo = titulo,
                    clienteNombre = cliente,
                    direccion = direccion,
                    fecha = fecha, // <-- Guardamos la fecha elegida
                    estado = "En curso",
                    imagenUrl = imagenUrl, // <-- Guardamos la imagen principal
                    fotos = listaFotosInicial // <-- La agregamos también a la lista de fotos de la obra
                )
            )
        }
    }

    fun cambiarEstadoObra(obraId: Long, nuevoEstado: String) {
        viewModelScope.launch {
            val obra = obraDao.obtenerObraPorId(obraId) ?: return@launch
            obraDao.actualizarObra(obra.copy(estado = nuevoEstado))
        }
    }

    fun agregarFotoObra(obraId: Long, nuevaFotoUri: String) {
        viewModelScope.launch {
            val obra = obraDao.obtenerObraPorId(obraId) ?: return@launch
            val nuevasFotos = obra.fotos + nuevaFotoUri
            obraDao.actualizarObra(
                obra.copy(
                    fotos = nuevasFotos,
                    imagenUrl = nuevaFotoUri
                )
            )
        }
    }

    // --- ACCIONES DE TAREAS ---
    fun agregarTarea(obraId: Long, descripcion: String) {
        viewModelScope.launch {
            tareaDao.insertarTarea(
                TareaEntity(
                    obraId = obraId,
                    descripcion = descripcion,
                    completada = false
                )
            )
        }
    }

    fun toggleTarea(tareaId: Long, completada: Boolean) {
        viewModelScope.launch {
            // Se actualiza directamente el estado de la tarea
        }
    }

    fun actualizarTareasObra(obraId: Long, nuevasTareas: List<TareaItem>) {
        viewModelScope.launch {
            nuevasTareas.forEach { tarea ->
                if (tarea.id == 0L) {
                    tareaDao.insertarTarea(
                        TareaEntity(
                            obraId = obraId,
                            descripcion = tarea.descripcion,
                            completada = tarea.completada
                        )
                    )
                } else {
                    tareaDao.actualizarTarea(
                        TareaEntity(
                            id = tarea.id,
                            obraId = obraId,
                            descripcion = tarea.descripcion,
                            completada = tarea.completada
                        )
                    )
                }
            }
        }
    }

    // --- ACCIONES DE CLIENTES ---
    fun agregarCliente(nombre: String, telefono: String, direccion: String, email: String) {
        viewModelScope.launch {
            clienteDao.insertarCliente(
                ClienteEntity(
                    nombre = nombre,
                    telefono = telefono,
                    direccion = direccion,
                    email = email
                )
            )
        }
    }
}