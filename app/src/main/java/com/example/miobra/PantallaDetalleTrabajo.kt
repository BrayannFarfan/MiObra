package com.example.miobra

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val VerdeVerona = Color(0xFF0F4C3A)
private val VerdeMenta = Color(0xFF2DC194)
private val GrisFondoApple = Color(0xFFF6F8F7)
private val GrisTarjeta = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF1C1D1F)
private val TextoSecundario = Color(0xFF6E7179)
private val GrisCampo = Color(0xFFEFEFF4)

@Composable
fun PantallaDetalleTrabajo(
    padding: PaddingValues,
    obra: ObraItem,
    onVolver: () -> Unit,
    onCambiarEstado: (String) -> Unit,
    onAgregarFoto: (String) -> Unit, // Callback para guardar nueva foto
    onActualizarTareas: (List<TareaItem>) -> Unit
) {
    val context = LocalContext.current
    var tabSeleccionada by remember { mutableStateOf("Info") }
    val tabs = listOf("Info", "Fotos", "Tareas", "Informe")
    val estadosPosibles = listOf("En curso", "Pendiente", "Terminado")


    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onAgregarFoto(it.toString())
        }
    }

    // Unificamos la foto principal con la lista de fotos
    val todasLasFotos = remember(obra) {
        val lista = obra.fotos.toMutableList()
        if (!obra.imagenUrl.isNullOrEmpty() && !lista.contains(obra.imagenUrl)) {
            lista.add(0, obra.imagenUrl)
        }
        lista
    }

    // CÁLCULO DEL PROGRESO
    val totalTareas = obra.tareas.size
    val tareasCompletadas = obra.tareas.count { it.completada }
    val progresoCalculado = if (totalTareas > 0) tareasCompletadas.toFloat() / totalTareas.toFloat() else 0f
    val porcentajeTexto = (progresoCalculado * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisFondoApple)
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CABECERA VOLVER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹ Volver a obras",
                    color = VerdeVerona,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onVolver() }
                )
            }

            // SELECTOR DE ESTADO
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ESTADO DE LA OBRA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSecundario,
                    letterSpacing = 0.8.sp
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    estadosPosibles.forEach { estado ->
                        val seleccionado = obra.estado == estado
                        Surface(
                            onClick = { if (!seleccionado) onCambiarEstado(estado) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (seleccionado) VerdeVerona else GrisCampo,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = estado,
                                color = if (seleccionado) Color.White else TextoSecundario,
                                fontSize = 12.sp,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // TÍTULO
            Column {
                Text(
                    text = obra.titulo,
                    color = TextoPrincipal,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍 ", fontSize = 13.sp)
                    Text(obra.direccion, color = TextoSecundario, fontSize = 13.sp)
                }
            }

            // BARRA DE PESTAÑAS
            TabRow(
                selectedTabIndex = tabs.indexOf(tabSeleccionada),
                containerColor = Color.Transparent,
                contentColor = VerdeVerona,
                divider = {}
            ) {
                tabs.forEach { tab ->
                    val selected = tabSeleccionada == tab
                    Tab(
                        selected = selected,
                        onClick = { tabSeleccionada = tab },
                        text = {
                            Text(
                                text = tab,
                                fontSize = 14.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) VerdeVerona else TextoSecundario
                            )
                        }
                    )
                }
            }

            // CONTENIDO PESTAÑAS
            when (tabSeleccionada) {
                "Info" -> VistaInfoGeneral(obra, progresoCalculado, porcentajeTexto, totalTareas, tareasCompletadas)
                // En VistaFotosObra:
                "Fotos" -> VistaFotosObra(
                    fotos = todasLasFotos,
                    onAgregarFotoClick = { launcherGaleria.launch(arrayOf("image/*")) }
                )
                "Tareas" -> VistaTareasObra(
                    tareas = obra.tareas,
                    onToggleTarea = { tareaModificada ->
                        val nuevasTareas = obra.tareas.map {
                            if (it.id == tareaModificada.id) it.copy(completada = !it.completada) else it
                        }
                        onActualizarTareas(nuevasTareas)
                    },
                    onAgregarTarea = { nuevaDescripcion ->
                        val nuevaTarea = TareaItem(descripcion = nuevaDescripcion)
                        onActualizarTareas(obra.tareas + nuevaTarea)
                    }
                )
                "Informe" -> VistaInformeObra(context, obra, todasLasFotos, porcentajeTexto, totalTareas, tareasCompletadas)
            }
        }
    }
}

// VISTA INFO GENERAL
@Composable
private fun VistaInfoGeneral(
    obra: ObraItem,
    progreso: Float,
    porcentaje: Int,
    totalTareas: Int,
    tareasCompletadas: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CLIENTE ASIGNADO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoSecundario)
                Spacer(Modifier.height(4.dp))
                Text(obra.cliente, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Avance de Obra", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                    Text("$porcentaje%", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = VerdeVerona)
                }
                Spacer(Modifier.height(6.dp))
                Text("$tareasCompletadas de $totalTareas tareas completadas", fontSize = 12.sp, color = TextoSecundario)
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = VerdeMenta,
                    trackColor = GrisCampo
                )
            }
        }
    }
}

// VISTA FOTOS CORREGIDA Y CON GALERÍA ABAJO
@Composable
private fun VistaFotosObra(
    fotos: List<String>,
    onAgregarFotoClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Button(
            onClick = onAgregarFotoClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeVerona)
        ) {
            Text("+ Seleccionar / Subir Foto", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (fotos.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GrisTarjeta)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷", fontSize = 36.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("No hay fotos cargadas en esta obra", color = TextoSecundario, fontSize = 13.sp)
                    }
                }
            }
        } else {
            Text(
                text = "Fotos de la Obra (${fotos.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextoPrincipal
            )

            // GRILLA DE FOTOS ALMACENADAS
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                fotos.chunked(2).forEach { fila ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        fila.forEach { fotoUri ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(140.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                AsyncImage(
                                    model = fotoUri,
                                    contentDescription = "Foto Obra",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        if (fila.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// VISTA TAREAS
@Composable
private fun VistaTareasObra(
    tareas: List<TareaItem>,
    onToggleTarea: (TareaItem) -> Unit,
    onAgregarTarea: (String) -> Unit
) {
    var nuevaTareaTexto by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = nuevaTareaTexto,
                onValueChange = { nuevaTareaTexto = it },
                placeholder = { Text("Ej: Colocar grifería", color = TextoSecundario.copy(alpha = 0.6f), fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrisCampo,
                    unfocusedContainerColor = GrisCampo,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Button(
                onClick = {
                    if (nuevaTareaTexto.isNotBlank()) {
                        onAgregarTarea(nuevaTareaTexto)
                        nuevaTareaTexto = ""
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVerona),
                modifier = Modifier.height(52.dp)
            ) {
                Text("+ Agregar", fontWeight = FontWeight.Bold)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            if (tareas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tareas creadas aún", color = TextoSecundario, fontSize = 13.sp)
                }
            } else {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    tareas.forEach { tarea ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTarea(tarea) }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = tarea.completada,
                                onCheckedChange = { onToggleTarea(tarea) },
                                colors = CheckboxDefaults.colors(checkedColor = VerdeVerona)
                            )
                            Text(
                                text = tarea.descripcion,
                                fontSize = 14.sp,
                                color = if (tarea.completada) TextoSecundario else TextoPrincipal,
                                fontWeight = if (tarea.completada) FontWeight.Normal else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// VISTA INFORME CON ENVÍO DE FOTO POR WHATSAPP
@Composable
private fun VistaInformeObra(
    context: Context,
    obra: ObraItem,
    fotos: List<String>,
    porcentaje: Int,
    totalTareas: Int,
    tareasCompletadas: Int
) {
    val textoInforme = buildString {
        append("🏗️ *REFACCIONES VERONA - INFORME DE OBRA*\n")
        append("----------------------------------------\n")
        append("📌 *Obra:* ${obra.titulo}\n")
        append("👤 *Cliente:* ${obra.cliente}\n")
        append("📍 *Dirección:* ${obra.direccion}\n")
        append("🚦 *Estado:* ${obra.estado}\n")
        append("📊 *Avance:* $porcentaje% ($tareasCompletadas de $totalTareas tareas)\n\n")

        if (obra.tareas.isNotEmpty()) {
            append("📋 *DETALLE DE TAREAS:*\n")
            obra.tareas.forEach { tarea ->
                val icono = if (tarea.completada) "✅" else "⏳"
                append("$icono ${tarea.descripcion}\n")
            }
            append("\n")
        }
        append("_Generado desde Verona Tracker App_")
    }

    val ultimaFotoUri = fotos.lastOrNull()

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "VISTA PREVIA DEL INFORME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSecundario
                )

                // PREVISUALIZACIÓN DE LA FOTO EN EL INFORME
                if (!ultimaFotoUri.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        AsyncImage(
                            model = ultimaFotoUri,
                            contentDescription = "Foto informe",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Text(
                    text = textoInforme.replace("*", ""),
                    fontSize = 13.sp,
                    color = TextoPrincipal,
                    lineHeight = 18.sp
                )
            }
        }

        // BOTÓN: ENVIAR POR WHATSAPP CON ADJUNTO
        Button(
            onClick = {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textoInforme)
                    if (!ultimaFotoUri.isNullOrEmpty()) {
                        val uri = Uri.parse(ultimaFotoUri)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/*"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } else {
                        type = "text/plain"
                    }
                    setPackage("com.whatsapp")
                }

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val fallbackIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, textoInforme)
                        if (!ultimaFotoUri.isNullOrEmpty()) {
                            val uri = Uri.parse(ultimaFotoUri)
                            putExtra(Intent.EXTRA_STREAM, uri)
                            type = "image/*"
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        } else {
                            type = "text/plain"
                        }
                    }
                    context.startActivity(Intent.createChooser(fallbackIntent, "Compartir Informe"))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeMenta)
        ) {
            Text(
                text = if (!ultimaFotoUri.isNullOrEmpty()) "💬 Enviar Informe con Foto por WhatsApp" else "💬 Enviar Informe por WhatsApp",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // BOTÓN: COPIAR
        OutlinedButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Informe Obra", textoInforme)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "¡Informe copiado al portapapeles!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "📋 Copiar Texto del Informe",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VerdeVerona
            )
        }
    }
}