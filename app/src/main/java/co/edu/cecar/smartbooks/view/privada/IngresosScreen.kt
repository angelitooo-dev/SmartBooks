package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
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
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.RojoInstitucional
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
    val currency = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 } }
    val catalogoLibros = viewModel.librosDisponibles

    LaunchedEffect(Unit) {
        viewModel.cargarIngresos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ingresos de Inventario",
                        style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI, fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Volver", tint = AzulOscuroCDI)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoFondo)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showModalCrear = true },
                containerColor = RojoInstitucional,
                contentColor = BlancoFondo,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo Ingreso") }
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF9FAFB))) {
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
                        Icon(imageVector = Icons.Outlined.Layers, contentDescription = null, tint = RojoInstitucional, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))

                        // SOLUCIÓN AL ERROR DE REFERENCIA: Texto seguro que no romperá la compilación
                        Text(text = "Error al conectar con el servidor", color = GrisOscuroTexto, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.cargarIngresos() },
                            colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI)
                        ) {
                            Text("Reintentar Carga")
                        }
                    }
                }
                is IngresosUiState.Success -> {
                    if (state.lista.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay registros de ingresos.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        // SOLUCIÓN AL ERROR DEL LAZYCOLUMN: Todo estructurado correctamente dentro de su contenedor nativo
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.lista) { ingreso ->
                                val libroDelCatalogo = catalogoLibros.find { it.id == ingreso.libroId }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = libroDelCatalogo?.nombre ?: "Libro ID: ${ingreso.libroId ?: 0}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AzulOscuroCDI),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Surface(
                                                color = AzulOscuroCDI.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "Lote: ${ingreso.lote ?: 0}",
                                                    color = AzulOscuroCDI,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = Color(0xFFE5E7EB))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Unidades", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Outlined.Numbers, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("${ingreso.unidades ?: 0}", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto, fontWeight = FontWeight.Medium)
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Costo Compra", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                                Text(currency.format(ingreso.valorCompra ?: 0.0), style = MaterialTheme.typography.bodyMedium, color = RojoInstitucional, fontWeight = FontWeight.Bold)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("P. Venta Público", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                                // SOLUCIÓN AL UNRESOLVED REFERENCE: Se usa 'valorVentaPublico' que coincide exactamente con tu IngresoResponse
                                                Text(currency.format(ingreso.valorVentaPublico ?: 0.0), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
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
                    Text("Registrar Ingreso", style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI, fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewModel.mensajeErrorFormulario != null) {
                        Surface(
                            color = RojoInstitucional.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = viewModel.mensajeErrorFormulario ?: "",
                                color = RojoInstitucional,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

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
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                        )
                        ExposedDropdownMenu(
                            expanded = libroExpandido,
                            onDismissRequest = { libroExpandido = false },
                            modifier = Modifier.background(BlancoFondo)
                        ) {
                            if (catalogoLibros.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay libros disponibles") },
                                    onClick = {}
                                )
                            } else {
                                catalogoLibros.forEach { libro ->
                                    DropdownMenuItem(
                                        text = { Text(libro.nombre) },
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
                        label = { Text("Código de Lote (Solo Números)") },
                        leadingIcon = { Icon(Icons.Outlined.Layers, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                    )

                    OutlinedTextField(
                        value = viewModel.unidadesInput,
                        onValueChange = { viewModel.unidadesInput = it },
                        label = { Text("Unidades Disponibles") },
                        leadingIcon = { Icon(Icons.Outlined.Numbers, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                    )

                    OutlinedTextField(
                        value = viewModel.valorCompraInput,
                        onValueChange = { viewModel.valorCompraInput = it },
                        label = { Text("Valor Unitario Compra ($)") },
                        leadingIcon = { Icon(Icons.Outlined.LocalAtm, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                    )

                    OutlinedTextField(
                        value = viewModel.valorVentaInput,
                        onValueChange = { viewModel.valorVentaInput = it },
                        label = { Text("Valor Unitario Venta Público ($)") },
                        leadingIcon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
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
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BlancoFondo, strokeWidth = 2.dp)
                    } else {
                        Text("Guardar Ingreso")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showModalCrear = false }, enabled = !viewModel.isGuardando) {
                    Text("Cancelar", color = RojoInstitucional)
                }
            },
            containerColor = BlancoFondo,
            shape = RoundedCornerShape(16.dp)
        )
    }
}