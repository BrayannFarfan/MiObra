package com.example.miobra

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun PantallaTrabajos(
    padding: PaddingValues,
    onCrearNuevoTrabajo: () -> Unit,
    onSeleccionarTrabajo: (ObraItem) -> Unit,
    obras: List<ObraItem>
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    /*val todasLasObras = remember {
        listOf(
            ObraItem("Reparación de baño", "Juan Pérez", "Av. San Martín 123", "En curso"),
            ObraItem("Pintura exterior", "María Gómez", "Los Álamos 456", "Pendiente"),
            ObraItem("Cambio de cañerías", "Carlos Ruiz", "Rivadavia 789", "Terminado"),
            ObraItem("Remodelación cocina", "Ana Martínez", "Belgrano 1020", "En curso"),
            ObraItem("Instalación eléctrica", "Lucas Torres", "Sarmiento 345", "Pendiente")
        )
    }*/

    val obrasFiltradas = obras.filter { obra ->
        val coincideBusqueda = obra.titulo.contains(textoBusqueda, ignoreCase = true) ||
                obra.cliente.contains(textoBusqueda, ignoreCase = true) ||
                obra.direccion.contains(textoBusqueda, ignoreCase = true)

        val coincideFiltro = when (filtroSeleccionado) {
            "En curso" -> obra.estado == "En curso"
            "Pendientes" -> obra.estado == "Pendiente"
            "Terminados" -> obra.estado == "Terminado"
            else -> true
        }

        coincideBusqueda && coincideFiltro
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis Trabajos",
                    color = TextoPrincipal,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = VerdeVerona.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${obrasFiltradas.size}",
                        color = VerdeVerona,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            TextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = {
                    Text(
                        "Buscar por trabajo, cliente o dirección...",
                        color = TextoSecundario,
                        fontSize = 14.sp
                    )
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

            val opcionesFiltro = listOf("Todos", "En curso", "Pendientes", "Terminados")

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(opcionesFiltro) { opcion ->
                    val seleccionado = filtroSeleccionado == opcion
                    Surface(
                        onClick = { filtroSeleccionado = opcion },
                        shape = RoundedCornerShape(20.dp),
                        color = if (seleccionado) VerdeVerona else GrisTarjeta,
                        tonalElevation = if (seleccionado) 0.dp else 1.dp
                    ) {
                        Text(
                            text = opcion,
                            color = if (seleccionado) Color.White else TextoSecundario,
                            fontSize = 13.sp,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (obrasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron trabajos", color = TextoSecundario, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(obrasFiltradas) { obra ->
                        ItemTrabajo(obra = obra, onClick = { onSeleccionarTrabajo(obra) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onCrearNuevoTrabajo,
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
fun ItemTrabajo(obra: ObraItem, onClick: () -> Unit) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = obra.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    BadgeEstado(estado = obra.estado)
                }

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👤 ", fontSize = 12.sp)
                    Text(text = obra.cliente, fontSize = 13.sp, color = TextoSecundario, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍 ", fontSize = 12.sp)
                    Text(text = obra.direccion, fontSize = 12.sp, color = TextoSecundario.copy(alpha = 0.8f))
                }
            }

            Text(
                text = "›",
                fontSize = 24.sp,
                color = TextoSecundario.copy(alpha = 0.4f),
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun BadgeEstado(estado: String) {
    val (fondo, texto) = when (estado) {
        "En curso" -> Pair(VerdeMenta.copy(alpha = 0.15f), VerdeVerona)
        "Pendiente" -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        else -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
    }

    Surface(color = fondo, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = estado,
            color = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}