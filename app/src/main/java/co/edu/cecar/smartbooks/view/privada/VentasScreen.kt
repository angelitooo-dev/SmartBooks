package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.cecar.smartbooks.data.model.*
import co.edu.cecar.smartbooks.viewmodel.LibrosViewModel
import co.edu.cecar.smartbooks.viewmodel.LibrosUiState
import co.edu.cecar.smartbooks.viewmodel.VentasViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    onVolver: () -> Unit,
    viewModel: VentasViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                VentasViewModel(app)
            }
        }
    ),
    librosViewModel: LibrosViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                LibrosViewModel(app)
            }
        }
    )
) {
    val context = LocalContext.current

    var versionCarrito by remember { mutableStateOf(0) }
    val ventas by viewModel.listaVentas
    val isLoading by viewModel.isLoading
    val ventaDetalle by viewModel.ventaDetalle
    val listErrorMessage by viewModel.listErrorMessage
    val operacionExitosa by viewModel.operacionExitosa
    val librosState = librosViewModel.uiState

    var modoFormularioActivo by remember { mutableStateOf(false) }
    var mostrarModalDetalle by remember { mutableStateOf(false) }
    val copFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 } }

    // --- ESTADOS DE FILTROS ---
    var libroSeleccionadoFiltro by remember { mutableStateOf("Todos los libros") }
    var libroFiltroDropdownExpandido by remember { mutableStateOf(false) }

    var fechaDesdeInput by remember { mutableStateOf("") }
    var fechaHastaInput by remember { mutableStateOf("") }

    var filtroLibro by remember { mutableStateOf("Todos los libros") }
    var filtroFechaDesde by remember { mutableStateOf("") }
    var filtroFechaHasta by remember { mutableStateOf("") }

    // --- DIÁLOGOS NATIVOS DE FECHA ---
    val calendar = Calendar.getInstance()

    val datePickerDialogDesde = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val m = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
            val d = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            fechaDesdeInput = "$year-$m-$d"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val datePickerDialogHasta = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val m = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
            val d = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            fechaHastaInput = "$year-$m-$d"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // --- ESTADOS DEL FORMULARIO ---
    var identificacionCliente by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var cantidadInput by remember { mutableStateOf("1") }
    var libroSeleccionado by remember { mutableStateOf<LibroResponse?>(null) }
    var libroDropdownExpandido by remember { mutableStateOf(false) }
    var loteDropdownExpandido by remember { mutableStateOf(false) }

    // --- CÁLCULOS ASOCIADOS ---
    val prefijoComprobante = "FAC-2026-"
    val numeroComprobanteAUTOMATICO: String = remember(ventas) {
        val ultimoNumero = ventas.mapNotNull { v ->
            v.numeroComprobante?.replace(prefijoComprobante, "")?.trim()?.toIntOrNull()
        }.maxOrNull() ?: 0
        "$prefijoComprobante${(ultimoNumero + 1).toString().padStart(6, '0')}"
    }
    var numeroComprobante by remember(numeroComprobanteAUTOMATICO) { mutableStateOf(numeroComprobanteAUTOMATICO) }

    val totalPagarCalculated = remember(versionCarrito, viewModel.carritoTemp.size) {
        viewModel.carritoTemp.sumOf { it.cantidad * it.precioUnitario }
    }

    val listadoLibrosRaw = remember(librosState) {
        if (librosState is LibrosUiState.Success) librosState.libros else emptyList()
    }
    val librosUnicosParaSeleccion = remember(listadoLibrosRaw) {
        listadoLibrosRaw.distinctBy { it.nombre }
    }
    val lotesDisponibles by remember(libroSeleccionado, listadoLibrosRaw) {
        derivedStateOf {
            if (libroSeleccionado == null) emptyList()
            else listadoLibrosRaw.filter { it.nombre == libroSeleccionado?.nombre }
        }
    }
    var loteSeleccionado by remember { mutableStateOf<LibroResponse?>(null) }

    LaunchedEffect(lotesDisponibles) {
        loteSeleccionado = lotesDisponibles.firstOrNull()
    }

    val subtotalCalculated = remember(cantidadInput, libroSeleccionado) {
        val cantidad = cantidadInput.toIntOrNull() ?: 0
        val precio = (libroSeleccionado?.valorVentaPublico ?: 0).toDouble()
        cantidad * precio
    }

    LaunchedEffect(Unit) {
        viewModel.cargarVentas()
        librosViewModel.cargarLibros()
    }

    LaunchedEffect(operacionExitosa) {
        if (operacionExitosa) {
            modoFormularioActivo = false
            viewModel.setOperacionExitosa(false)
        }
    }

    // --- LÓGICA DE FILTRADO ---
    val ventasFiltradas = ventas.filter { venta ->
        val matchesLibro = if (filtroLibro != "Todos los libros") {
            venta.items?.any { item ->
                val nombreDelLibro = item.nombreLibro ?: ""
                nombreDelLibro.lowercase() == filtroLibro.lowercase()
            } ?: false
        } else true

        val fechaVentaStr = venta.fecha?.take(10)

        val matchesDesde = if (filtroFechaDesde.isNotBlank() && fechaVentaStr != null) {
            fechaVentaStr >= filtroFechaDesde
        } else true

        val matchesHasta = if (filtroFechaHasta.isNotBlank() && fechaVentaStr != null) {
            fechaVentaStr <= filtroFechaHasta
        } else true

        matchesLibro && matchesDesde && matchesHasta
    }

    if (!modoFormularioActivo) {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = "Terminal de Ventas",
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF1E293B))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.carritoTemp.clear()
                        modoFormularioActivo = true
                    },
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nueva Venta", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        ) { pv ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // PANEL DE FILTROS
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = fechaDesdeInput,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Desde") },
                                trailingIcon = {
                                    IconButton(onClick = { datePickerDialogDesde.show() }) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = fechaHastaInput,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hasta") },
                                trailingIcon = {
                                    IconButton(onClick = { datePickerDialogHasta.show() }) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        ExposedDropdownMenuBox(
                            expanded = libroFiltroDropdownExpandido,
                            onExpandedChange = { libroFiltroDropdownExpandido = !libroFiltroDropdownExpandido }
                        ) {
                            OutlinedTextField(
                                value = libroSeleccionadoFiltro,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Libro") },
                                leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF64748B)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = libroFiltroDropdownExpandido) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = libroFiltroDropdownExpandido,
                                onDismissRequest = { libroFiltroDropdownExpandido = false }
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Default.AllInclusive, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    text = { Text("Todos los libros") },
                                    onClick = {
                                        libroSeleccionadoFiltro = "Todos los libros"
                                        libroFiltroDropdownExpandido = false
                                    }
                                )
                                librosUnicosParaSeleccion.forEach { item ->
                                    DropdownMenuItem(
                                        leadingIcon = { Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                        text = { Text(item.nombre) },
                                        onClick = {
                                            libroSeleccionadoFiltro = item.nombre
                                            libroFiltroDropdownExpandido = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    filtroLibro = libroSeleccionadoFiltro
                                    filtroFechaDesde = fechaDesdeInput
                                    filtroFechaHasta = fechaHastaInput
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B429F)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buscar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    libroSeleccionadoFiltro = "Todos los libros"
                                    fechaDesdeInput = ""
                                    fechaHastaInput = ""
                                    filtroLibro = "Todos los libros"
                                    filtroFechaDesde = ""
                                    filtroFechaHasta = ""
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF475569)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Limpiar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // HISTORIAL DE ENCABEZADO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF1E293B), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Historial de Facturación",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(color = Color(0xFFE2E8F0), shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "${ventasFiltradas.size} ítems",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (listErrorMessage != null) Text(listErrorMessage!!, color = Color.Red)
                if (isLoading) LinearProgressIndicator(color = Color(0xFFDC2626), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ventasFiltradas.forEach { venta ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF3B429F), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = venta.numeroComprobante ?: "N/A", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 15.sp)
                                    }
                                    Text(text = copFormatter.format(venta.total ?: 0.0), fontWeight = FontWeight.Black, color = Color(0xFFDC2626), fontSize = 16.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Cliente: ${(venta.cliente ?: venta.clienteNombre ?: "Consumidor Final").uppercase()}",
                                        color = Color(0xFF475569), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .drawBehind {
                                            val strokeWidth = 1.dp.toPx()
                                            drawLine(
                                                color = Color(0xFFF1F5F9),
                                                start = Offset(0f, size.height - strokeWidth / 2),
                                                end = Offset(size.width, size.height - strokeWidth / 2),
                                                strokeWidth = strokeWidth
                                            )
                                        }
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = venta.fecha?.take(10) ?: "", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    }

                                    Row(
                                        modifier = Modifier
                                            .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                                            .clickable {
                                                venta.id?.let { id ->
                                                    viewModel.obtenerDetalleVenta(id)
                                                    mostrarModalDetalle = true
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Ver Detalle", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // ==========================================
    // FORMULARIO: REGISTRAR NUEVA VENTA
    // ==========================================
    else {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = { Text("Registrar Factura", color = Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    navigationIcon = { IconButton(onClick = { modoFormularioActivo = false }) { Icon(Icons.Default.ArrowBack, contentDescription = null) } },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                Surface(color = Color.White, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { modoFormularioActivo = false }, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancelar", color = Color.Gray)
                        }

                        Button(
                            onClick = {
                                if (identificacionCliente.isNotBlank() && numeroComprobante.isNotBlank()) {
                                    viewModel.procesarVenta(identificacionCliente, numeroComprobante, observaciones)
                                }
                            },
                            enabled = viewModel.carritoTemp.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), disabledContainerColor = Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar Venta", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { pv ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = Color(0xFF3B429F))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Datos de Facturación", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        }
                        OutlinedTextField(
                            value = identificacionCliente, onValueChange = { identificacionCliente = it },
                            label = { Text("Identificación Cliente *") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = numeroComprobante, onValueChange = { numeroComprobante = it },
                            label = { Text("N° Comprobante *") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = Color.Gray) }
                        )
                        OutlinedTextField(
                            value = observaciones, onValueChange = { observaciones = it },
                            label = { Text("Observaciones de la venta") }, modifier = Modifier.fillMaxWidth(), maxLines = 2,
                            leadingIcon = { Icon(Icons.Default.Comment, contentDescription = null, tint = Color.Gray) }
                        )
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF3B429F))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Selección de Productos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        }

                        ExposedDropdownMenuBox(
                            expanded = libroDropdownExpandido,
                            onExpandedChange = { libroDropdownExpandido = !libroDropdownExpandido }
                        ) {
                            OutlinedTextField(
                                value = libroSeleccionado?.nombre ?: "Seleccionar Libro",
                                onValueChange = {}, readOnly = true, label = { Text("Libro") },
                                leadingIcon = { Icon(Icons.Default.Book, contentDescription = null, tint = Color(0xFF64748B)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = libroDropdownExpandido) },
                                shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = libroDropdownExpandido, onDismissRequest = { libroDropdownExpandido = false }) {
                                librosUnicosParaSeleccion.forEach { item ->
                                    DropdownMenuItem(text = { Text(item.nombre) }, onClick = { libroSeleccionado = item; libroDropdownExpandido = false })
                                }
                            }
                        }

                        if (lotesDisponibles.size > 1) {
                            ExposedDropdownMenuBox(
                                expanded = loteDropdownExpandido,
                                onExpandedChange = { loteDropdownExpandido = !loteDropdownExpandido }
                            ) {
                                OutlinedTextField(
                                    value = "Lote: ${loteSeleccionado?.lote ?: "N/A"} - Stock: ${loteSeleccionado?.unidades ?: 0}",
                                    onValueChange = {}, readOnly = true, label = { Text("Lote Disponible") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loteDropdownExpandido) },
                                    shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(expanded = loteDropdownExpandido, onDismissRequest = { loteDropdownExpandido = false }) {
                                    lotesDisponibles.forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text("Lote: ${item.lote} - Disp: ${item.unidades} (${copFormatter.format(item.valorVentaPublico)})") },
                                            onClick = { loteSeleccionado = item; loteDropdownExpandido = false }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = cantidadInput,
                                onValueChange = { cantidadInput = it },
                                label = { Text("Cantidad") }, shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f)
                            )

                            Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.End) {
                                Text("Precio Unitario", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(text = copFormatter.format(libroSeleccionado?.valorVentaPublico ?: 0), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Subtotal", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(text = copFormatter.format(subtotalCalculated), fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF3B429F))
                            }
                        }

                        Button(
                            onClick = {
                                val target = loteSeleccionado ?: libroSeleccionado
                                val cant = cantidadInput.toIntOrNull() ?: 0
                                if (target != null && cant > 0) {
                                    // CORRECCIÓN: Se pasa 'target.lote' directamente como Int, sin .toString()
                                    val nuevoItem = CarritoItem(
                                        libroId = target.id,
                                        cantidad = cant,
                                        precioUnitario = (target.valorVentaPublico ?: 0).toDouble(),
                                        libroTitulo = target.nombre,
                                        lote = target.lote ?: 0
                                    )
                                    viewModel.carritoTemp.add(nuevoItem)
                                    versionCarrito++
                                    cantidadInput = "1"
                                }
                            },
                            enabled = libroSeleccionado != null,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B429F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Añadir al Carrito", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF3B429F))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Resumen de Compra", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        }

                        if (viewModel.carritoTemp.isEmpty()) {
                            Text("No has añadido libros a esta factura aún.", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
                        } else {
                            viewModel.carritoTemp.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.libroTitulo ?: "", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF1E293B))
                                        Text("${item.cantidad} x ${copFormatter.format(item.precioUnitario)}", fontSize = 12.sp, color = Color(0xFF64748B))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(copFormatter.format(item.cantidad * item.precioUnitario), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                        IconButton(onClick = {
                                            viewModel.carritoTemp.removeAt(index)
                                            versionCarrito++
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL A PAGAR:", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF1E293B))
                                Text(copFormatter.format(totalPagarCalculated), fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DIÁLOGO: DETALLE DE FACTURA SELECCIONADA
    // ==========================================
    if (mostrarModalDetalle && ventaDetalle != null) {
        Dialog(onDismissRequest = { mostrarModalDetalle = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Detalle de Factura", color = Color(0xFF1E293B), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        IconButton(onClick = { mostrarModalDetalle = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("COMPROBANTE: ${ventaDetalle?.numeroComprobante ?: ""}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("CLIENTE ID: ${ventaDetalle?.cliente ?: ""}", fontSize = 13.sp, color = Color(0xFF475569))
                        Text("FECHA: ${ventaDetalle?.fecha?.take(16) ?: ""}", fontSize = 13.sp, color = Color(0xFF475569))
                        if (!ventaDetalle?.observaciones.isNullOrBlank()) {
                            Text("OBSERVACIONES: ${ventaDetalle?.observaciones}", fontSize = 13.sp, color = Color(0xFF475569))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Text("ARTÍCULOS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF64748B))

                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ventaDetalle?.items?.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.nombreLibro ?: "Libro desconocido", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                                    Text("Cant: ${item.cantidad}  •  U: ${copFormatter.format(item.precioUnitario ?: 0.0)}", fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                                Text(
                                    text = copFormatter.format((item.cantidad ?: 0) * (item.precioUnitario ?: 0.0)),
                                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL FACTURADO", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF1E293B))
                        Text(copFormatter.format(ventaDetalle?.total ?: 0.0), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}