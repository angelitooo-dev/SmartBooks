package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.cecar.smartbooks.viewmodel.InventariosViewModel

import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.RojoVivoInstitucional
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo
import co.edu.cecar.smartbooks.ui.theme.GrisClaroTablas
import co.edu.cecar.smartbooks.ui.theme.GrisMedioBordes

import androidx.compose.foundation.layout.Spacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventariosScreen(
    onVolver: () -> Unit,
    viewModel: InventariosViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                InventariosViewModel(app)
            }
        }
    )
) {
    val inventario by viewModel.listaInventario
    val isLoading by viewModel.isLoading
    val listErrorMessage = viewModel.listErrorMessage.value

    val totalLibros by viewModel.totalLibros
    val bajoStock by viewModel.bajoStock
    val stockTotalUnits by viewModel.stockTotalUnits

    var loteInput by remember { mutableStateOf("") }
    var filtroActivoText by remember { mutableStateOf("Sem 1 · 2026 ✦") }

    val kpisScrollState = rememberScrollState()
    val filtroEtiquetasScrollState = rememberScrollState()
    val tableHorizontalScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.cargarInventario(20261)
    }

    Scaffold(
        containerColor = GrisClaroTablas,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión de Inventario",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = AzulOscuroCDI,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = AzulOscuroCDI,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            loteInput = ""
                            filtroActivoText = ""
                            viewModel.cargarInventario(null)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Restablecer",
                            tint = AzulOscuroCDI,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoFondo)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(kpisScrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GrisMedioBordes.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(RojoVivoInstitucional))
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Libros",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$totalLibros",
                                    color = GrisOscuroTexto,
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                            Surface(color = RojoVivoInstitucional.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(36.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Book, contentDescription = null, tint = RojoVivoInstitucional, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GrisMedioBordes.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(AzulOscuroCDI))
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Bajo Stock",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$bajoStock",
                                    color = GrisOscuroTexto,
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                            Surface(color = AzulOscuroCDI.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(36.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GrisMedioBordes.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(RojoVivoInstitucional))
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Stock Total",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$stockTotalUnits",
                                    color = GrisOscuroTexto,
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                            Surface(color = RojoVivoInstitucional.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(36.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Inventory, contentDescription = null, tint = RojoVivoInstitucional, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GrisMedioBordes.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.FilterList, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FILTRAR POR LOTE",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = loteInput,
                            onValueChange = { loteInput = it },
                            placeholder = { Text("Ej: 20261", style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)) },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = AzulOscuroCDI, modifier = Modifier.size(20.dp)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AzulOscuroCDI,
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = AzulOscuroCDI
                            )
                        )
                        Button(
                            onClick = {
                                val numeroLote = loteInput.toIntOrNull()
                                viewModel.cargarInventario(numeroLote)
                                filtroActivoText = when (numeroLote) {
                                    20252 -> "Sem 2 · 2025"
                                    20261 -> "Sem 1 · 2026 ✦"
                                    20262 -> "Sem 2 · 2026"
                                    else -> ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI),
                            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Buscar", style = MaterialTheme.typography.labelLarge.copy(color = Color.White))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(filtroEtiquetasScrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sugerenciasLotes = listOf(
                            "Sem 2 · 2025" to 20252,
                            "Sem 1 · 2026 ✦" to 20261,
                            "Sem 2 · 2026" to 20262
                        )
                        sugerenciasLotes.forEach { (itemSemestre, codigoLote) ->
                            val seleccionado = itemSemestre == filtroActivoText
                            Surface(
                                color = if (seleccionado) AzulOscuroCDI else GrisClaroTablas,
                                shape = CircleShape,
                                border = BorderStroke(1.dp, if (seleccionado) AzulOscuroCDI else GrisMedioBordes.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable {
                                    filtroActivoText = itemSemestre
                                    loteInput = codigoLote.toString()
                                    viewModel.cargarInventario(codigoLote)
                                }
                            ) {
                                Text(
                                    text = itemSemestre,
                                    color = if (seleccionado) Color.White else GrisOscuroTexto,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                LinearProgressIndicator(color = RojoVivoInstitucional, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (listErrorMessage != null) {
                Text(
                    text = listErrorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GrisMedioBordes.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().horizontalScroll(tableHorizontalScrollState)) {

                    Row(
                        modifier = Modifier
                            .width(860.dp)
                            .background(AzulOscuroCDI)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("LIBRO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(2.4f))
                        Text("NIVEL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(0.9f), textAlign = TextAlign.Center)
                        Text("EDICIÓN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(0.9f))
                        Text("TIPO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(1.1f))
                        Text("LOTE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(0.8f))
                        Text("ING.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(0.9f))
                        Text("VEND.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(0.9f))
                        Text("STOCK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.3.sp, modifier = Modifier.weight(1.1f))
                    }

                    if (inventario.isEmpty() && !isLoading) {
                        Box(modifier = Modifier.width(860.dp).weight(1f), contentAlignment = Alignment.Center) {
                            Text("No se encontraron registros de inventario.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.width(860.dp).weight(1f)) {
                            itemsIndexed(inventario) { index, fila ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val libro = fila.libroNavigationDetalle

                                        Text(
                                            text = libro?.nombre ?: "Sin Datos",
                                            color = GrisOscuroTexto,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                            modifier = Modifier.weight(2.4f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Box(
                                            modifier = Modifier.weight(0.9f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Surface(
                                                color = AzulOscuroCDI.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, AzulOscuroCDI.copy(alpha = 0.2f))
                                            ) {
                                                Text(
                                                    text = libro?.nivel ?: "-",
                                                    color = AzulOscuroCDI,
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        Text(
                                            text = libro?.edicion ?: "-",
                                            color = GrisOscuroTexto,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                            modifier = Modifier.weight(0.9f)
                                        )

                                        Text(
                                            text = libro?.tipo ?: "-",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                            modifier = Modifier.weight(1.1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "${fila.lote ?: 0}",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                            modifier = Modifier.weight(0.8f)
                                        )

                                        Box(modifier = Modifier.weight(0.9f)) {
                                            Surface(color = Color(0xFFE8F8F5), shape = RoundedCornerShape(4.dp)) {
                                                Text(
                                                    text = "${fila.cantidadIngresada ?: 0}",
                                                    color = Color(0xFF16A34A),
                                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Box(modifier = Modifier.weight(0.9f)) {
                                            Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(4.dp)) {
                                                Text(
                                                    text = "${fila.cantidadVendida ?: 0}",
                                                    color = Color(0xFF2563EB),
                                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Box(modifier = Modifier.weight(1.1f)) {
                                            val stockValue = fila.stockDisponible ?: 0
                                            val stockBg = if (stockValue <= 5) Color(0xFFFEF2F2) else Color(0xFFE8F8F5)
                                            val stockColor = if (stockValue <= 5) Color(0xFFDC2626) else Color(0xFF16A34A)
                                            Surface(color = stockBg, shape = RoundedCornerShape(4.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    if (stockValue <= 5) {
                                                        Icon(Icons.Default.Warning, null, tint = stockColor, modifier = Modifier.size(11.dp))
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                    }
                                                    Text(
                                                        text = "$stockValue",
                                                        color = stockColor,
                                                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (index < inventario.lastIndex) {
                                        HorizontalDivider(color = GrisClaroTablas, thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}