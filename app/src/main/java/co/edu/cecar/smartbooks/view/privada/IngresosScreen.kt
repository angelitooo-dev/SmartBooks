package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocalAtm
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo
import co.edu.cecar.smartbooks.ui.theme.GrisClaroTablas
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.RojoInstitucional
import co.edu.cecar.smartbooks.ui.theme.RojoVivoInstitucional
import co.edu.cecar.smartbooks.viewmodel.IngresosUiState
import co.edu.cecar.smartbooks.viewmodel.IngresosViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IngresosViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                IngresosViewModel(app)
            }
        }
    )
) {
    var showModalCrear by remember { mutableStateOf(false) }
    val currency = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 }
    val catalogoLibros = viewModel.librosDisponibles

    LaunchedEffect(Unit) {
        viewModel.cargarIngresos()
    }

    Scaffold(
        modifier = modifier.statusBarsPadding(), // Añadido aquí para empujar toda la pantalla abajo y pintar la barra de estado de blanco
        containerColor = GrisClaroTablas,
        topBar = {
            Surface(
                color = BlancoFondo, // Asegura el blanco absoluto arriba del título
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BlancoFondo)
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = AzulOscuroCDI,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Historial De Ingresos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulOscuroCDI
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showModalCrear = true },
                containerColor = RojoVivoInstitucional,
                contentColor = BlancoFondo,
                shape = RoundedCornerShape(16.dp),
                text = {
                    Text(
                        text = "+ Nuevo Ingreso",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                },
                icon = {}
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = viewModel.uiState) {
                is IngresosUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RojoInstitucional)
                }
                is IngresosUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarIngresos() }, colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI)) {
                            Text("Reintentar", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                is IngresosUiState.Success -> {
                    val totalIngresos = state.lista.size
                    val unidadesTotales = state.lista.sumOf { it.unidades ?: 0 }

                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = RojoInstitucional, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = totalIngresos.toString(), style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, color = AzulOscuroCDI, fontWeight = FontWeight.Bold))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "Total Ingresos", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Outlined.Layers, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = unidadesTotales.toString(), style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, color = AzulOscuroCDI, fontWeight = FontWeight.Bold))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "Unidades Totales", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                                    }
                                }
                            }
                        }

                        if (state.lista.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                    Text("No se registran lotes comprados en el sistema.", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                                }
                            }
                        } else {
                            items(state.lista) { ingreso ->
                                val libroDelCatalogo = catalogoLibros.find { it.id == ingreso.libroId }
                                val nombreLibro = libroDelCatalogo?.nombre ?: "Libro Desconocido"

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = nombreLibro,
                                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, color = RojoInstitucional, fontWeight = FontWeight.Bold),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Outlined.Numbers, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(text = "N° LOTE", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray.copy(alpha = 0.7f), fontSize = 10.sp))
                                                        Text(text = "Lote #${ingreso.lote ?: 0}", style = MaterialTheme.typography.bodyMedium.copy(color = AzulOscuroCDI, fontWeight = FontWeight.Bold))
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(text = "FECHA REGISTRO", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray.copy(alpha = 0.7f), fontSize = 10.sp))
                                                        Text(text = ingreso.fechaRegistro?.take(10) ?: "N/A", style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto))
                                                    }
                                                }
                                            }

                                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Outlined.AttachMoney, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(text = "COSTO ADQUISICIÓN", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray.copy(alpha = 0.7f), fontSize = 10.sp))
                                                        Text(text = currency.format(ingreso.valorCompra ?: 0.0), style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto))
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Outlined.LocalAtm, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(text = "VALOR VENTA", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray.copy(alpha = 0.7f), fontSize = 10.sp))
                                                        Text(text = currency.format(ingreso.valorVentaPublico ?: 0.0), style = MaterialTheme.typography.bodyMedium.copy(color = AzulOscuroCDI, fontWeight = FontWeight.Bold))
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Outlined.Inventory2, contentDescription = null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(text = "Unidades Ingresadas", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray.copy(alpha = 0.7f)))
                                            }

                                            val units = ingreso.unidades ?: 0
                                            Box(
                                                modifier = Modifier
                                                    .background(RojoInstitucional.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "$units unds",
                                                    color = RojoInstitucional,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    maxLines = 1
                                                )
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
    }

    if (showModalCrear) {
        var libroExpandido by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!viewModel.isGuardando) showModalCrear = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = AzulOscuroCDI)
                    Text("Nuevo Ingreso a Stock", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, color = AzulOscuroCDI))
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (viewModel.mensajeErrorFormulario != null) {
                        Text(text = viewModel.mensajeErrorFormulario!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold))
                    }

                    Text("Libro del Catálogo:", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold))
                    ExposedDropdownMenuBox(
                        expanded = libroExpandido,
                        onExpandedChange = { libroExpandido = !libroExpandido }
                    ) {
                        OutlinedTextField(
                            value = viewModel.libroSeleccionado?.nombre ?: "Toca para elegir un libro...",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = AzulOscuroCDI) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = libroExpandido) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedTextColor = GrisOscuroTexto,
                                unfocusedTextColor = GrisOscuroTexto
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = libroExpandido,
                            onDismissRequest = { libroExpandido = false },
                            modifier = Modifier.background(BlancoFondo)
                        ) {
                            if (catalogoLibros.isEmpty()) {
                                DropdownMenuItem(text = { Text("Cargando catálogo...", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)) }, onClick = {})
                            } else {
                                catalogoLibros.forEach { libro ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(libro.nombre, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = GrisOscuroTexto))
                                                Text("Nivel: ${libro.nivel} • Edición: ${libro.edicion}", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                                            }
                                        },
                                        onClick = {
                                            viewModel.libroSeleccionado = libro
                                            libroExpandido = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = viewModel.loteInput,
                        onValueChange = { viewModel.loteInput = it },
                        label = { Text("Número del Lote", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Outlined.Layers, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, unfocusedBorderColor = Color.LightGray)
                    )

                    OutlinedTextField(
                        value = viewModel.unidadesInput,
                        onValueChange = { viewModel.unidadesInput = it },
                        label = { Text("Cantidad Unidades", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Outlined.Numbers, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, unfocusedBorderColor = Color.LightGray)
                    )

                    OutlinedTextField(
                        value = viewModel.valorCompraInput,
                        onValueChange = { viewModel.valorCompraInput = it },
                        label = { Text("Costo Compra Unitario", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, unfocusedBorderColor = Color.LightGray)
                    )

                    OutlinedTextField(
                        value = viewModel.valorVentaInput,
                        onValueChange = { viewModel.valorVentaInput = it },
                        label = { Text("Precio Venta Público", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Outlined.LocalAtm, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, unfocusedBorderColor = Color.LightGray)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.guardarNuevoIngreso { showModalCrear = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoInstitucional),
                    enabled = !viewModel.isGuardando,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (viewModel.isGuardando) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BlancoFondo, strokeWidth = 2.dp)
                    } else {
                        Text("Registrar", style = MaterialTheme.typography.labelLarge)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showModalCrear = false }, enabled = !viewModel.isGuardando) {
                    Text("Cancelar", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                }
            },
            containerColor = BlancoFondo,
            shape = RoundedCornerShape(16.dp)
        )
    }
}