package com.example.miobra

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeVerona = Color(0xFF0F4C3A)
private val VerdeMenta = Color(0xFF2DC194)
private val GrisFondoApple = Color(0xFFF6F8F7)
private val GrisTarjeta = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF1C1D1F)
private val TextoSecundario = Color(0xFF6E7179)
private val GrisBuscador = Color(0xFFEFEFF4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaClientes(
    padding: PaddingValues,
    onNuevoCliente: () -> Unit,
    onSeleccionarCliente: (ClienteItem) -> Unit,
    clientes: List<ClienteItem>
) {
    var textoBusqueda by remember { mutableStateOf("") }

    val clientesFiltrados = clientes.filter { cliente ->
        cliente.nombre.contains(textoBusqueda, ignoreCase = true) ||
                cliente.telefono.contains(textoBusqueda) ||
                cliente.direccion.contains(textoBusqueda, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisFondoApple)
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clientes",
                    color = TextoPrincipal,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = VerdeVerona.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${clientesFiltrados.size}",
                        color = VerdeVerona,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // --- BUSCADOR ---
            TextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = {
                    Text("Buscar cliente por nombre o teléfono...", color = TextoSecundario, fontSize = 14.sp)
                },
                leadingIcon = { Text("🔍", fontSize = 16.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrisBuscador,
                    unfocusedContainerColor = GrisBuscador,
                    disabledContainerColor = GrisBuscador,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // --- LISTA DE CLIENTES ---
            if (clientesFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron clientes", color = TextoSecundario, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(clientesFiltrados) { cliente ->
                        TarjetaClienteItem(
                            cliente = cliente,
                            onClick = { onSeleccionarCliente(cliente) }
                        )
                    }
                }
            }
        }

        // --- BOTÓN FLOTANTE (+) ---
        FloatingActionButton(
            onClick = onNuevoCliente,
            containerColor = VerdeVerona,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(58.dp)
        ) {
            Text("+", fontSize = 28.sp, fontWeight = FontWeight.Normal)
        }
    }
}

@Composable
fun TarjetaClienteItem(cliente: ClienteItem, onClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con Inicial
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(VerdeVerona.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cliente.nombre.take(1).uppercase(),
                    color = VerdeVerona,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = cliente.telefono,
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = cliente.direccion,
                    fontSize = 12.sp,
                    color = TextoSecundario.copy(alpha = 0.8f)
                )
            }

            // Botones de acción rápida (WhatsApp / Llamar)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick = {
                        if (cliente.telefono.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${cliente.telefono.replace(" ", "").replace("-", "")}")
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Sin teléfono registrado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(GrisBuscador, CircleShape)
                ) {
                    Text("📞", fontSize = 14.sp)
                }

                IconButton(
                    onClick = {
                        if (cliente.telefono.isNotBlank()) {
                            val numeroLimpio = cliente.telefono.replace(" ", "").replace("-", "").replace("+", "")
                            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$numeroLimpio&text=${Uri.encode("Hola ${cliente.nombre}, te escribo desde Refacciones Verona.")}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Sin WhatsApp registrado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(VerdeMenta.copy(alpha = 0.2f), CircleShape)
                ) {
                    Text("💬", fontSize = 14.sp)
                }
            }
        }
    }
}