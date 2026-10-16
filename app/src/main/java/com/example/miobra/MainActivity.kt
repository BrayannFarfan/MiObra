package com.example.miobra

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// --- PALETA DE COLORES REFACCIONES VERONA / ESTILO APPLE ---
private val VerdeVerona = Color(0xFF0F4C3A)
private val VerdeOscuro = Color(0xFF0A3327)
private val VerdeMenta = Color(0xFF2DC194)
private val DoradoAccent = Color(0xFFE0A96D)
private val GrisFondoApple = Color(0xFFF6F8F7)
private val GrisTarjeta = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF1C1D1F)
private val TextoSecundario = Color(0xFF6E7179)

class MainActivity : ComponentActivity() {

    private val viewModel: VeronaViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme {
                MisObrasApp(viewModel = viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MisObrasApp(viewModel: VeronaViewModel) {
    // Estado para controlar la pantalla de carga inicial (Splash)
    var mostrarSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1500) // 1.5 segundos de carga inicial
        mostrarSplash = false
    }

    if (mostrarSplash) {
        PantallaSplash()
    } else {
        var pantalla by remember { mutableStateOf("Inicio") }
        var obraSeleccionada by remember { mutableStateOf<ObraItem?>(null) }
        var clienteSeleccionado by remember { mutableStateOf<ClienteItem?>(null) }

        // --- OBSERVAMOS LA BASE DE DATOS LOCAL (ROOM) EN TIEMPO REAL ---
        val listaObras by viewModel.obras.collectAsState()
        val listaClientes by viewModel.clientes.collectAsState()

        // Sincroniza el objeto seleccionado si se actualiza la BD
        LaunchedEffect(listaObras) {
            obraSeleccionada?.let { seleccionada ->
                obraSeleccionada = listaObras.find { it.id == seleccionada.id }
            }
        }

        Scaffold(
            containerColor = GrisFondoApple,
            bottomBar = {
                val ocultarBottomBar = pantalla in listOf("Nuevo trabajo", "Nuevo cliente", "Detalle trabajo", "Detalle cliente")
                if (!ocultarBottomBar) {
                    NavigationBar(
                        containerColor = Color.White.copy(alpha = 0.95f),
                        tonalElevation = 8.dp
                    ) {
                        val opciones = listOf(
                            Triple("Inicio", "⌂", "Inicio"),
                            Triple("Trabajos", "▣", "Trabajos"),
                            Triple("Clientes", "♙", "Clientes")
                        )

                        opciones.forEach { opcion ->
                            val selected = pantalla == opcion.first
                            NavigationBarItem(
                                selected = selected,
                                onClick = { pantalla = opcion.first },
                                icon = {
                                    Text(
                                        opcion.second,
                                        fontSize = 20.sp,
                                        color = if (selected) VerdeVerona else TextoSecundario
                                    )
                                },
                                label = {
                                    Text(
                                        opcion.third,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) VerdeVerona else TextoSecundario
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = VerdeMenta.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            when (pantalla) {
                "Inicio" -> InicioObrasModerno(
                    padding = padding,
                    obras = listaObras,
                    nuevoTrabajo = {
                        clienteSeleccionado = null
                        pantalla = "Nuevo trabajo"
                    },
                    verTrabajos = { pantalla = "Trabajos" },
                    verClientes = { pantalla = "Clientes" },
                    onSeleccionarTrabajo = { obra ->
                        obraSeleccionada = obra
                        pantalla = "Detalle trabajo"
                    }
                )

                "Trabajos" -> PantallaTrabajos(
                    padding = padding,
                    obras = listaObras,
                    onCrearNuevoTrabajo = {
                        clienteSeleccionado = null
                        pantalla = "Nuevo trabajo"
                    },
                    onSeleccionarTrabajo = { obra ->
                        obraSeleccionada = obra
                        pantalla = "Detalle trabajo"
                    }
                )

                "Clientes" -> PantallaClientes(
                    padding = padding,
                    clientes = listaClientes,
                    onNuevoCliente = { pantalla = "Nuevo cliente" },
                    onSeleccionarCliente = { cliente ->
                        clienteSeleccionado = cliente
                        pantalla = "Detalle cliente"
                    }
                )

                "Nuevo trabajo" -> PantallaNuevoTrabajo(
                    padding = padding,
                    clientePreseleccionado = clienteSeleccionado,
                    onVolver = {
                        pantalla = if (clienteSeleccionado != null) "Detalle cliente" else "Trabajos"
                    },
                    onGuardarTrabajo = { obraCreada ->
                        viewModel.agregarObra(
                            titulo = obraCreada.titulo,
                            cliente = obraCreada.cliente,
                            direccion = obraCreada.direccion,
                            fecha = obraCreada.fecha,
                            imagenUrl = obraCreada.imagenUrl
                        )
                        pantalla = "Trabajos"
                    }
                )

                "Nuevo cliente" -> PantallaNuevoCliente(
                    padding = padding,
                    onVolver = { pantalla = "Clientes" },
                    onGuardarCliente = { clienteCreado ->
                        viewModel.agregarCliente(
                            nombre = clienteCreado.nombre,
                            telefono = clienteCreado.telefono,
                            direccion = clienteCreado.direccion,
                            email = clienteCreado.email
                        )
                        pantalla = "Clientes"
                    }
                )

                "Detalle trabajo" -> {
                    obraSeleccionada?.let { obra ->
                        PantallaDetalleTrabajo(
                            padding = padding,
                            obra = obra,
                            onVolver = { pantalla = "Trabajos" },
                            onCambiarEstado = { nuevoEstado ->
                                viewModel.cambiarEstadoObra(obra.id, nuevoEstado)
                            },
                            onAgregarFoto = { nuevaFotoUri ->
                                viewModel.agregarFotoObra(obra.id, nuevaFotoUri)
                            },
                            onActualizarTareas = { nuevasTareas ->
                                viewModel.actualizarTareasObra(obra.id, nuevasTareas)
                            }
                        )
                    }
                }

                "Detalle cliente" -> {
                    clienteSeleccionado?.let { cliente ->
                        val obrasDelCliente = listaObras.filter { it.cliente.contains(cliente.nombre, ignoreCase = true) }

                        PantallaDetalleCliente(
                            padding = padding,
                            cliente = cliente,
                            obrasDelCliente = obrasDelCliente,
                            onVolver = { pantalla = "Clientes" },
                            onSeleccionarObra = { obra ->
                                obraSeleccionada = obra
                                pantalla = "Detalle trabajo"
                            },
                            onNuevaObraParaCliente = {
                                pantalla = "Nuevo trabajo"
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- PANTALLA DE CARGA (SPLASH SCREEN) ---
@Composable
fun PantallaSplash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeVerona),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    color = VerdeVerona,
                    fontWeight = FontWeight.Bold,
                    fontSize = 46.sp
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "REFACCIONES VERONA",
                color = DoradoAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(12.dp))

            CircularProgressIndicator(
                color = VerdeMenta,
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp
            )
        }
    }
}

@Composable
fun InicioObrasModerno(
    padding: PaddingValues,
    obras: List<ObraItem>,
    nuevoTrabajo: () -> Unit,
    verTrabajos: () -> Unit,
    verClientes: () -> Unit,
    onSeleccionarTrabajo: (ObraItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // --- 1. CABECERA CON MARCA DE LA EMPRESA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "REFACCIONES VERONA",
                    color = VerdeVerona,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Gestión de Obras",
                    color = TextoPrincipal,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(VerdeVerona),
                contentAlignment = Alignment.Center
            ) {
                Text("V", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
        }

        // --- 2. TARJETA HERO: ANILLO DE PROGRESO GLOBAL ---
        val totalObras = obras.size
        val obrasTerminadas = obras.count { it.estado == "Terminado" }
        val obrasActivas = obras.count { it.estado == "En curso" }

        val porcentajeProgreso = if (totalObras > 0) {
            (obrasTerminadas.toFloat() / totalObras.toFloat() * 100).toInt()
        } else 0

        val anguloBarra = if (totalObras > 0) {
            (obrasTerminadas.toFloat() / totalObras.toFloat()) * 360f
        } else 0f

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = VerdeVerona),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "PROGRESO GENERAL",
                        color = DoradoAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$obrasActivas Obras Activas",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "$obrasTerminadas de $totalObras obras terminadas",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.15f),
                            style = Stroke(width = 8.dp.toPx())
                        )
                        drawArc(
                            color = VerdeMenta,
                            startAngle = -90f,
                            sweepAngle = anguloBarra,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$porcentajeProgreso%",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. ACCESOS RÁPIDOS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonAccesoRapido(
                titulo = "Crear Obra",
                icono = "+",
                containerColor = VerdeVerona,
                contentColor = Color.White,
                modifier = Modifier.weight(1f),
                onClick = nuevoTrabajo
            )
            BotonAccesoRapido(
                titulo = "Clientes",
                icono = "♙",
                containerColor = GrisTarjeta,
                contentColor = TextoPrincipal,
                modifier = Modifier.weight(1f),
                onClick = verClientes
            )
        }

        // --- 4. SECCIÓN: OBRAS ACTIVAS (CARRUSEL HORIZONTAL) ---
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Obras en Curso",
                    color = TextoPrincipal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ver todas",
                    color = VerdeVerona,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { verTrabajos() }
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(end = 10.dp)
            ) {
                items(obras) { obra ->
                    TarjetaObraHorizontal(
                        obra = obra,
                        onClick = { onSeleccionarTrabajo(obra) }
                    )
                }
            }
        }

        // --- 5. RESUMEN ESTADÍSTICO ---
        Text(
            "Resumen",
            color = TextoPrincipal,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaStat(
                etiqueta = "En Proceso",
                valor = "${obras.count { it.estado == "En curso" }}",
                colorIndicador = VerdeMenta,
                modifier = Modifier.weight(1f)
            )
            TarjetaStat(
                etiqueta = "Terminados",
                valor = "${obras.count { it.estado == "Terminado" }}",
                colorIndicador = DoradoAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            "Refacciones Verona · Control & Transparencia © 2026",
            color = TextoSecundario,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun TarjetaObraHorizontal(obra: ObraItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(VerdeVerona.copy(alpha = 0.8f), VerdeOscuro)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!obra.imagenUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = obra.imagenUrl,
                        contentDescription = obra.titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("📷 Foto Obra", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            Surface(
                color = if (obra.estado == "En curso") VerdeMenta.copy(alpha = 0.15f) else Color(0xFFFFF3E0),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = obra.estado,
                    color = if (obra.estado == "En curso") VerdeVerona else Color(0xFFE65100),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                obra.titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            Text(
                obra.cliente,
                fontSize = 12.sp,
                color = TextoSecundario
            )
            Text(
                obra.direccion,
                fontSize = 11.sp,
                color = TextoSecundario.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun BotonAccesoRapido(
    titulo: String,
    icono: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(icono, fontSize = 20.sp, color = contentColor)
            Text(titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

@Composable
fun TarjetaStat(
    etiqueta: String,
    valor: String,
    colorIndicador: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GrisTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(colorIndicador)
            )
            Spacer(Modifier.height(8.dp))
            Text(valor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextoPrincipal)
            Text(etiqueta, fontSize = 12.sp, color = TextoSecundario)
        }
    }
}