package com.example.miobra
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeVerona = Color(0xFF0F4C3A)
private val GrisFondoApple = Color(0xFFF6F8F7)
private val GrisTarjeta = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF1C1D1F)
private val TextoSecundario = Color(0xFF6E7179)
private val GrisCampo = Color(0xFFEFEFF4)

@Composable
fun PantallaNuevoCliente(
    padding: PaddingValues,
    onVolver: () -> Unit,
    onGuardarCliente: (ClienteItem) -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

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
                text = "Nuevo Cliente",
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
                    // Nombre completo
                    EtiquetaCampo("NOMBRE Y APELLIDO")
                    CampoTextoCustom(
                        valor = nombre,
                        onValueChange = { nombre = it },
                        placeholder = "Ej: Juan Pérez"
                    )

                    // Teléfono
                    EtiquetaCampo("TELÉFONO / WHATSAPP")
                    CampoTextoCustom(
                        valor = telefono,
                        onValueChange = { telefono = it },
                        placeholder = "Ej: +54 11 1234-5678"
                    )

                    // Dirección principal
                    EtiquetaCampo("DIRECCIÓN / DOMICILIO")
                    CampoTextoCustom(
                        valor = direccion,
                        onValueChange = { direccion = it },
                        placeholder = "Ej: Av. San Martín 123, CABA"
                    )

                    // Email (Opcional)
                    EtiquetaCampo("CORREO ELECTRÓNICO (OPCIONAL)")
                    CampoTextoCustom(
                        valor = email,
                        onValueChange = { email = it },
                        placeholder = "Ej: cliente@email.com"
                    )

                    // Notas o comentarios adicionales
                    EtiquetaCampo("NOTAS ADICIONALES")
                    TextField(
                        value = notas,
                        onValueChange = { notas = it },
                        placeholder = { Text("Detalles particulares del cliente...", color = TextoSecundario.copy(alpha = 0.6f), fontSize = 14.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GrisCampo,
                            unfocusedContainerColor = GrisCampo,
                            disabledContainerColor = GrisCampo,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )
                }
            }

            // --- 3. BOTÓN GUARDAR CLIENTE ---
            Button(
                onClick = {
                    if (nombre.isNotBlank() && telefono.isNotBlank()) {
                        val nuevoCliente = ClienteItem(
                            id = System.currentTimeMillis().toString(),
                            nombre = nombre,
                            telefono = telefono,
                            direccion = direccion.ifBlank { "Sin dirección" },
                            email = email
                        )
                        Toast.makeText(
                            context,
                            "¡Cliente guardado con éxito!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onGuardarCliente(nuevoCliente)
                    }else{
                        Toast.makeText(
                            context,
                            "Por favor, ingresá el nombre del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVerona)
            ) {
                Text(
                    text = "Guardar Cliente",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}