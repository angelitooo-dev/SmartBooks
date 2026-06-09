package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.cecar.smartbooks.data.model.ClienteCreateRequest
import co.edu.cecar.smartbooks.data.model.ClienteResponse
import co.edu.cecar.smartbooks.data.model.ClienteUpdateRequest
import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.RojoVivoInstitucional
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.GrisClaroTablas
import co.edu.cecar.smartbooks.viewmodel.ClientesUiState
import co.edu.cecar.smartbooks.viewmodel.ClientesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    onVolver: () -> Unit,
    viewModel: ClientesViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                ClientesViewModel(app)
            }
        }
    )
) {
    val state = viewModel.uiState
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarClientes()
    }

    Scaffold(
        containerColor = GrisClaroTablas,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión de Clientes",
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
                onClick = { viewModel.mostrarDialogoCrear = true },
                containerColor = RojoVivoInstitucional,
                contentColor = BlancoFondo,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier.padding(bottom = 12.dp, end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = BlancoFondo,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Nuevo Cliente",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.buscarClientes(searchText)
                },
                placeholder = {
                    Text("Buscar cliente por nombre o identificación...", color = Color.Gray.copy(0.7f), style = MaterialTheme.typography.bodyMedium)
                },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulOscuroCDI,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = BlancoFondo,
                    unfocusedContainerColor = BlancoFondo
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Catálogo disponible",
                style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI, fontSize = 18.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (state) {
                    is ClientesUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AzulOscuroCDI)
                        }
                    }
                    is ClientesUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Error: ${state.message}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    is ClientesUiState.Success -> {
                        if (state.clientes.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No se encontraron clientes registrados",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 88.dp)
                            ) {
                                items(state.clientes) { cliente ->
                                    TarjetaCliente(
                                        cliente = cliente,
                                        onEditarClick = { viewModel.clienteSeleccionadoParaEditar = cliente }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.mostrarDialogoCrear) {
        CrearClienteDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.mostrarDialogoCrear = false },
            onGuardar = { request -> viewModel.guardarNuevoCliente(request) }
        )
    }

    viewModel.clienteSeleccionadoParaEditar?.let { cliente ->
        EditarClienteDialog(
            viewModel = viewModel,
            cliente = cliente,
            onDismiss = { viewModel.clienteSeleccionadoParaEditar = null },
            onGuardar = { request -> viewModel.modificarCliente(cliente.identificacion, request) }
        )
    }
}

@Composable
fun TarjetaCliente(cliente: ClienteResponse, onEditarClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, null, tint = AzulOscuroCDI, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(cliente.identificacion, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = AzulOscuroCDI, fontSize = 15.sp))
                        Text(cliente.nombres, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = GrisOscuroTexto))
                    }
                }
                IconButton(
                    onClick = onEditarClick,
                    modifier = Modifier.background(GrisClaroTablas, CircleShape).size(34.dp)
                ) {
                    Icon(Icons.Default.Edit, "Editar", tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CELULAR", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                    Text(cliente.celular, style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto, fontSize = 13.sp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("NACIMIENTO", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                    Text(cliente.fechaNacimiento.take(10), style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto, fontSize = 13.sp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("CORREO ELECTRÓNICO", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp))
            Text(cliente.email, style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto, fontSize = 13.sp), maxLines = 1)
        }
    }
}

@Composable
fun FormFieldCliente(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
        leadingIcon = { Icon(icon, null, tint = AzulOscuroCDI, modifier = Modifier.size(20.dp)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulOscuroCDI,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = AzulOscuroCDI
        )
    )
}

@Composable
fun CrearClienteDialog(
    viewModel: ClientesViewModel,
    onDismiss: () -> Unit,
    onGuardar: (ClienteCreateRequest) -> Unit
) {
    var identificacion by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val errorFinal = viewModel.dialogErrorMessage ?: localErrorMessage

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = BlancoFondo,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AzulOscuroCDI.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Person, null, tint = AzulOscuroCDI, modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Registrar Cliente", style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorFinal != null) {
                    Text(errorFinal, color = Color.Red, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
                FormFieldCliente("Identificación", identificacion, { identificacion = it }, Icons.Default.Badge, KeyboardType.Number)
                FormFieldCliente("Nombres", nombres, { nombres = it }, Icons.Default.Person)
                FormFieldCliente("Correo", email, { email = it }, Icons.Default.Email, KeyboardType.Email)
                FormFieldCliente("Celular", celular, { celular = it }, Icons.Default.PhoneAndroid, KeyboardType.Phone)
                FormFieldCliente("Fecha Nacimiento (YYYY-MM-DD)", fechaNacimiento, { fechaNacimiento = it }, Icons.Default.Cake)

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (identificacion.isBlank() || nombres.isBlank() || email.isBlank() ||
                            celular.isBlank() || fechaNacimiento.isBlank()
                        ) {
                            localErrorMessage = "Todos los campos son obligatorios"
                        } else {
                            val regexFecha = Regex("""\d{4}-\d{2}-\d{2}""")
                            if (!regexFecha.matches(fechaNacimiento)) {
                                localErrorMessage = "La fecha debe estar en formato YYYY-MM-DD"
                            } else {
                                localErrorMessage = null
                                val request = ClienteCreateRequest(
                                    identificacion.trim(),
                                    nombres.trim(),
                                    email.trim(),
                                    celular.trim(),
                                    fechaNacimiento.trim()
                                )
                                onGuardar(request)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoVivoInstitucional),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar Cliente", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray))
            }
        }
    )
}

@Composable
fun EditarClienteDialog(
    viewModel: ClientesViewModel,
    cliente: ClienteResponse,
    onDismiss: () -> Unit,
    onGuardar: (ClienteUpdateRequest) -> Unit
) {
    var nombres by remember { mutableStateOf(cliente.nombres) }
    var email by remember { mutableStateOf(cliente.email) }
    var celular by remember { mutableStateOf(cliente.celular) }
    var fechaNacimiento by remember { mutableStateOf(cliente.fechaNacimiento.take(10)) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val errorFinal = viewModel.dialogErrorMessage ?: localErrorMessage

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = BlancoFondo,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AzulOscuroCDI.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Person, null, tint = AzulOscuroCDI, modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Editar Cliente", style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorFinal != null) {
                    Text(errorFinal, color = Color.Red, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
                FormFieldCliente("Nombres", nombres, { nombres = it }, Icons.Default.Person)
                FormFieldCliente("Correo", email, { email = it }, Icons.Default.Email, KeyboardType.Email)
                FormFieldCliente("Celular", celular, { celular = it }, Icons.Default.PhoneAndroid, KeyboardType.Phone)
                FormFieldCliente("Fecha Nacimiento (YYYY-MM-DD)", fechaNacimiento, { fechaNacimiento = it }, Icons.Default.Cake)

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (nombres.isNotBlank() && email.isNotBlank() &&
                            celular.isNotBlank() && fechaNacimiento.isNotBlank()
                        ) {
                            localErrorMessage = null
                            val request = ClienteUpdateRequest(
                                nombres.trim(),
                                email.trim(),
                                celular.trim(),
                                fechaNacimiento.trim()
                            )
                            onGuardar(request)
                        } else {
                            localErrorMessage = "Todos los campos son obligatorios"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoVivoInstitucional),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar Cambios", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray))
            }
        }
    )
}