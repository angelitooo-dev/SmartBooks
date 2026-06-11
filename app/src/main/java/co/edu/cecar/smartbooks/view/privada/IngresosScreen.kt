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
import androidx.compose.material.icons.outlined.AttachMoney
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
    val currency = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 }
    val catalogoLibros = viewModel.librosDisponibles

    LaunchedEffect(Unit) {
        viewModel.cargarIngresos()
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            TopAppBar(
                title = { Text("Historial De Ingresos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GrisOscuroTexto) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Volver", tint = AzulOscuroCDI, modifier = Modifier.size(18.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoFondo)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showModalCrear = true },
                containerColor = RojoInstitucional,
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp),
                text = { Text("+ Nuevo Ingreso", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
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
                        Text(text = state.message, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarIngresos() }, colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI)) {
                            Text("Reintentar")
                        }
                    }
                }
                is IngresosUiState.Success -> {
                    if (state.lista.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se registran lotes comprados en el sistema.", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.lista) { ingreso ->
                                val libroDelCatalogo = catalogoLibros.find { it.id == ingreso.libroId }
                                val nombreAMostrar = libroDelCatalogo?.nombre ?: "Libro ID: ${ingreso.libroId ?: "No asignado"}"

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Outlined.Layers, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                                                Text(text = "Lote #${ingreso.lote ?: 0}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AzulOscuroCDI)
                                            }
                                            Text(text = ingreso.fechaRegistro?.take(10) ?: "Reciente", fontSize = 11.sp, color = Color.Gray)
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(text = nombreAMostrar, fontSize = 15.sp, fontWeight = FontWeight.Black, color = GrisOscuroTexto)

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF3F4F6))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text("Unidades", fontSize = 11.sp, color = Color.Gray)
                                                Text("${ingreso.unidades ?: 0} unds", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GrisOscuroTexto)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Compra Unit.", fontSize = 11.sp, color = Color.Gray)
                                                Text(currency.format(ingreso.valorCompra ?: 0.0), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RojoInstitucional)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Venta Público", fontSize = 11.sp, color = Color.Gray)
                                                Text(currency.format(ingreso.valorVentaPublico ?: 0.0), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
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
                    Text("Nuevo Ingreso a Stock", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AzulOscuroCDI)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    if (viewModel.mensajeErrorFormulario != null) {
                        Text(text = viewModel.mensajeErrorFormulario!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    Text("Libro del Catálogo:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
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
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = libroExpandido,
                            onDismissRequest = { libroExpandido = false },
                            modifier = Modifier.background(BlancoFondo)
                        ) {
                            if (catalogoLibros.isEmpty()) {
                                DropdownMenuItem(text = { Text("Cargando catálogo...", color = Color.Gray) }, onClick = {})
                            } else {
                                catalogoLibros.forEach { libro ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(libro.nombre, fontWeight = FontWeight.Bold, color = GrisOscuroTexto)
                                                Text("Nivel: ${libro.nivel} • Edición: ${libro.edicion}", fontSize = 11.sp, color = Color.Gray)
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
                        label = { Text("Número del Lote") },
                        leadingIcon = { Icon(Icons.Outlined.Layers, contentDescription = null, tint = AzulOscuroCDI) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = viewModel.unidadesInput,
                        onValueChange = { viewModel.unidadesInput = it },
                        label = { Text("Cantidad Unidades") },
                        leadingIcon = { Icon(Icons.Outlined.Numbers, contentDescription = null, tint = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = viewModel.valorCompraInput,
                        onValueChange = { viewModel.valorCompraInput = it },
                        label = { Text("Costo Compra Unitario") },
                        leadingIcon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null, tint = RojoInstitucional) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = viewModel.valorVentaInput,
                        onValueChange = { viewModel.valorVentaInput = it },
                        label = { Text("Precio Venta Público") },
                        leadingIcon = { Icon(Icons.Outlined.LocalAtm, contentDescription = null, tint = Color(0xFF10B981)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.guardarNuevoIngreso { showModalCrear = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoInstitucional),
                    enabled = !viewModel.isGuardando
                ) {
                    if (viewModel.isGuardando) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Registrar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showModalCrear = false }, enabled = !viewModel.isGuardando) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = BlancoFondo,
            shape = RoundedCornerShape(16.dp)
        )
    }
}