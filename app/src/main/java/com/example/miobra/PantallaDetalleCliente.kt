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
private val DoradoAccent = Color(0xFFE0A96D)
private val GrisFondoApple = Color(0xFFF6F8F7)
private val GrisTarjeta = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF1C1D1F)
private val TextoSecundario = Color(0xFF6E7179)
private val GrisCampo = Color(0xFFEFEFF4)

@Composable
fun PantallaDetalleCliente(
    padding: PaddingValues,
    cliente: ClienteItem,
    obrasDelCliente: List<ObraItem>,
    onVolver: () -> Unit,
    onSeleccionarObra: (ObraItem) -> Unit,
    onNuevaObraParaCliente: () -> Unit
) {
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
            // --- 1. CABECERA CON BOTÓN VOLVER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹ Volver a clientes",
                    color = VerdeVerona,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onVolver() }
                )
            }

            // --- 2. TARJETA PERFIL DEL CLIENTE ---
            val context = LocalContext.current
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar grande con iniciales
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(VerdeVerona.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cliente.nombre.take(1).uppercase(),
                            color = VerdeVerona,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }

                    Text(
                        text = cliente.nombre,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    // Datos de contacto
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("📞 ${cliente.telefono}", fontSize = 14.sp, color = TextoSecundario, fontWeight = FontWeight.Medium)
                        Text("📍 ${cliente.direccion}", fontSize = 13.sp, color = TextoSecundario)
                        if (cliente.email.isNotBlank()) {
                            Text("✉️ ${cliente.email}", fontSize = 13.sp, color = TextoSecundario)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Botones de acción rápida (Llamar, WhatsApp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                .size(48.dp)
                                .background(GrisCampo, CircleShape)
                        ) {
                            Text("📞", fontSize = 18.sp)
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
                                .size(48.dp)
                                .background(VerdeMenta.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Text("💬", fontSize = 18.sp)
                        }
                    }
                }
            }

            // --- 3. SECCIÓN: HISTORIAL DE OBRAS / TRABAJOS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial de Obras",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Surface(
                    color = VerdeVerona.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${obrasDelCliente.size}",
                        color = VerdeVerona,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (obrasDelCliente.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Este cliente aún no tiene obras registradas",
                        color = TextoSecundario,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(obrasDelCliente) { obra ->
                        ItemTrabajo(
                            obra = obra,
                            onClick = { onSeleccionarObra(obra) }
                        )
                    }
                }
            }

            // --- 4. BOTÓN NUEVA OBRA PARA ESTE CLIENTE ---
            Button(
                onClick = onNuevaObraParaCliente,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVerona)
            ) {
                Text(
                    text = "+ Nueva Obra para ${cliente.nombre.split(" ").first()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}