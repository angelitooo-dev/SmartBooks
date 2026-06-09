package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.cecar.smartbooks.viewmodel.LotesViewModel

import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.RojoVivoInstitucional
import co.edu.cecar.smartbooks.ui.theme.GrisClaroTablas
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo
import co.edu.cecar.smartbooks.ui.theme.GrisMedioBordes

import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Numbers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotesScreen(
    onVolver: () -> Unit,
    viewModel: LotesViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                LotesViewModel(app)
            }
        }
    )
) {
    val lotes by viewModel.lotes
    val isLoading by viewModel.isLoading
    val listErrorMessage = viewModel.listErrorMessage.value

    var showDialog by remember { mutableStateOf(false) }
    var nuevoLoteText by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarLotes()
    }

    Scaffold(
        containerColor = GrisClaroTablas,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión de Lotes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuroCDI
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlancoFondo
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = "Nuevo Lote",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = BlancoFondo,
                        modifier = Modifier.size(22.dp)
                    )
                },
                onClick = {
                    localError = null
                    nuevoLoteText = ""
                    showDialog = true
                },
                containerColor = RojoVivoInstitucional,
                contentColor = BlancoFondo,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier.padding(bottom = 12.dp, end = 4.dp),
                expanded = !isLoading
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            if (listErrorMessage != null) {
                Text(
                    text = listErrorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 12.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lotes registrados",
                    style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI, fontSize = 18.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading && !showDialog) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulOscuroCDI)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GrisMedioBordes.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AzulOscuroCDI)
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Layers, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("NÚMERO DE LOTE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.5.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("LOTE ACTUAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.5.sp)
                            }
                        }

                        if (lotes.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No hay registros disponibles", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 88.dp)
                            ) {
                                itemsIndexed(lotes) { index, elemento ->
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp, vertical = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = GrisClaroTablas,
                                                    shape = CircleShape,
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.DateRange,
                                                            contentDescription = null,
                                                            tint = AzulOscuroCDI,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(14.dp))
                                                Text(
                                                    text = "Lote #${elemento.lote}",
                                                    color = GrisOscuroTexto,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                            }

                                            val containerBg = if (elemento.actual) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                            val textCl = if (elemento.actual) Color(0xFF16A34A) else Color(0xFFDC2626)
                                            val statusIcon = if (elemento.actual) Icons.Default.Check else Icons.Default.Clear
                                            val labelText = if (elemento.actual) "Vigente" else "Inactivo"

                                            Surface(
                                                color = containerBg,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.padding(end = 4.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = statusIcon,
                                                        contentDescription = null,
                                                        tint = textCl,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = labelText,
                                                        color = textCl,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }

                                        if (index < lotes.lastIndex) {
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = BlancoFondo,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = AzulOscuroCDI.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Icon(imageVector = Icons.Outlined.Layers, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Registrar Nuevo Lote", fontWeight = FontWeight.Bold, color = AzulOscuroCDI, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Asigne el identificador numérico correspondiente para el control de inventario actual.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = nuevoLoteText,
                        onValueChange = {
                            nuevoLoteText = it
                            localError = null
                        },
                        label = { Text("Número de Lote", style = MaterialTheme.typography.bodyMedium) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        leadingIcon = { Icon(Icons.Outlined.Numbers, null, tint = AzulOscuroCDI, modifier = Modifier.size(20.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulOscuroCDI,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = AzulOscuroCDI
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    localError?.let {
                        Text(text = it, color = RojoVivoInstitucional, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(color = AzulOscuroCDI, modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        enabled = nuevoLoteText.isNotEmpty() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = RojoVivoInstitucional),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        onClick = {
                            val numero = nuevoLoteText.toIntOrNull()
                            if (numero != null) {
                                viewModel.crearLote(numero) { exito, mensajeError ->
                                    if (exito) showDialog = false else localError = mensajeError
                                }
                            }
                        }
                    ) {
                        Text("Guardar Lote", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(enabled = !isLoading, onClick = { showDialog = false }) {
                    Text("Cancelar", style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray))
                }
            }
        )
    }
}