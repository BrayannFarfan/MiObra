package com.example.miobra

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val VerdeVerona = Color(0xFF0F4C3A)
private val GrisFondoApple = Color(0xFFF6F8F7)
private val GrisTarjeta = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF1C1D1F)
private val TextoSecundario = Color(0xFF6E7179)
private val GrisCampo = Color(0xFFEFEFF4)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNuevoTrabajo(
    padding: PaddingValues,
    clientePreseleccionado: ClienteItem? = null, // Recibe el cliente si viene del detalle
    onVolver: () -> Unit,
    onGuardarTrabajo: (ObraItem) -> Unit
) {
    val context = LocalContext.current

    // Calculamos la fecha actual automáticamente
    val fechaHoy = remember {
        try {
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            "13/10/2026"
        }
    }

    var nombreTrabajo by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf(clientePreseleccionado?.nombre ?: "") }
    var direccion by remember { mutableStateOf(clientePreseleccionado?.direccion ?: "") }
    var tipoTrabajo by remember { mutableStateOf("Plomería") }
    var fechaInicio by remember { mutableStateOf(fechaHoy) }
    var estadoSeleccionado by remember { mutableStateOf("En curso") }

    // Estado para la foto de la obra
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher con permiso PERSISTENTE para que la foto no venza
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
            imagenUri = it
        }
    }

    val tiposDeTrabajo = listOf("Plomería", "Pintura", "Albañilería", "Electricidad", "Remodelación Integral")
    val estadosPosibles = listOf("En curso", "Pendiente", "Terminado")
    var menuTipoExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisFondoApple)
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .wrapContentHeight()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // --- 1. CABECERA CON BOTÓN VOLVER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹ Volver",
                    color = VerdeVerona,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onVolver() }
                )
            }

            Text(
                text = "Nuevo Trabajo",
                color = TextoPrincipal,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            // --- 2. FORMULARIO EN TARJETA ESTILO APPLE ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- SELECTOR DE FOTO DE LA OBRA ---
                    EtiquetaCampo("FOTO DE LA OBRA")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GrisCampo)
                            .clickable { launcherGaleria.launch(arrayOf("image/*")) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenUri != null) {
                            AsyncImage(
                                model = imagenUri,
                                contentDescription = "Foto Obra",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📷", fontSize = 28.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Toca para seleccionar foto",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextoSecundario
                                )
                            }
                        }
                    }

                    // Nombre del Trabajo
                    EtiquetaCampo("NOMBRE DEL TRABAJO")
                    CampoTextoCustom(
                        valor = nombreTrabajo,
                        onValueChange = { nombreTrabajo = it },
                        placeholder = "Ej: Reparación de baño principal"
                    )

                    // Cliente
                    EtiquetaCampo("CLIENTE")
                    CampoTextoCustom(
                        valor = clienteSeleccionado,
                        onValueChange = { clienteSeleccionado = it },
                        placeholder = "Nombre del cliente"
                    )

                    // Dirección
                    EtiquetaCampo("DIRECCIÓN DE LA OBRA")
                    CampoTextoCustom(
                        valor = direccion,
                        onValueChange = { direccion = it },
                        placeholder = "Ej: Av. San Martín 123"
                    )

                    // Tipo de Trabajo
                    EtiquetaCampo("TIPO DE TRABAJO")
                    ExposedDropdownMenuBox(
                        expanded = menuTipoExpanded,
                        onExpandedChange = { menuTipoExpanded = !menuTipoExpanded }
                    ) {
                        TextField(
                            value = tipoTrabajo,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTipoExpanded) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GrisCampo,
                                unfocusedContainerColor = GrisCampo,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = menuTipoExpanded,
                            onDismissRequest = { menuTipoExpanded = false }
                        ) {
                            tiposDeTrabajo.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo) },
                                    onClick = {
                                        tipoTrabajo = tipo
                                        menuTipoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Fecha de Inicio
                    EtiquetaCampo("FECHA DE INICIO")
                    CampoTextoCustom(
                        valor = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        placeholder = "DD/MM/AAAA"
                    )

                    // Estado Inicial
                    EtiquetaCampo("ESTADO INICIAL")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        estadosPosibles.forEach { estado ->
                            val seleccionado = estadoSeleccionado == estado
                            Surface(
                                onClick = { estadoSeleccionado = estado },
                                shape = RoundedCornerShape(12.dp),
                                color = if (seleccionado) VerdeVerona else GrisCampo,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = estado,
                                    color = if (seleccionado) Color.White else TextoSecundario,
                                    fontSize = 12.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // --- 3. BOTÓN GUARDAR ---
            Button(
                onClick = {
                    if (nombreTrabajo.isNotBlank()) {
                        val nuevoItem = ObraItem(
                            titulo = nombreTrabajo,
                            cliente = clienteSeleccionado,
                            direccion = direccion.ifBlank { "Sin dirección" },
                            estado = estadoSeleccionado,
                            imagenUrl = imagenUri?.toString(),
                            fecha = fechaInicio
                        )

                        Toast.makeText(context, "¡Trabajo creado con éxito!", Toast.LENGTH_SHORT).show()
                        onGuardarTrabajo(nuevoItem)
                    } else {
                        Toast.makeText(context, "Por favor ingresa el nombre del trabajo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVerona)
            ) {
                Text(
                    text = "Guardar Trabajo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextoSecundario,
        letterSpacing = 0.8.sp
    )
}

@Composable
fun CampoTextoCustom(
    valor: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = valor,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextoSecundario.copy(alpha = 0.6f), fontSize = 14.sp) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GrisCampo,
            unfocusedContainerColor = GrisCampo,
            disabledContainerColor = GrisCampo,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}