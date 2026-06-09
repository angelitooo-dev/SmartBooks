package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import co.edu.cecar.smartbooks.data.remote.RegisterUsuarioDto
import co.edu.cecar.smartbooks.data.remote.UpdateUsuarioDto
import co.edu.cecar.smartbooks.data.remote.UsuarioResponse
import co.edu.cecar.smartbooks.viewmodel.UsuariosViewModel

// IMPORTACIÓN DE COLORES
import co.edu.cecar.smartbooks.ui.theme.RojoInstitucional
import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.GrisClaroTablas
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo
import co.edu.cecar.smartbooks.ui.theme.GrisMedioBordes
import co.edu.cecar.smartbooks.ui.theme.RojoVivoInstitucional

val ColorVerdeActivo = Color(0xFF16A34A)
val FondoVerdeActivo = Color(0xFFE8F8F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    onVolver: () -> Unit,
    viewModel: UsuariosViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                UsuariosViewModel(app)
            }
        }
    )
) {
    val lista = viewModel.listaUsuarios.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.mensajeError.value
    val operacionExitosa = viewModel.operacionExitosa.value
    val perfil = viewModel.perfilAutenticado.value

    var filtroRolSelected by remember { mutableStateOf("Todos") }

    var showDialogCrear by remember { mutableStateOf(false) }
    var showDialogPerfil by remember { mutableStateOf(false) }
    var usuarioParaEditar by remember { mutableStateOf<UsuarioResponse?>(null) }
    var usuarioParaVerDetalle by remember { mutableStateOf<UsuarioResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    if (operacionExitosa) {
        LaunchedEffect(Unit) {
            showDialogCrear = false
            usuarioParaEditar = null
            viewModel.cargarUsuarios()
            viewModel.operacionExitosa.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestión de Usuarios",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            color = AzulOscuroCDI
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = AzulOscuroCDI, modifier = Modifier.size(22.dp))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.cargarPerfilUsuarioAutenticado()
                        showDialogPerfil = true
                    }) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = "Mi Perfil", tint = AzulOscuroCDI, modifier = Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlancoFondo)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialogCrear = true },
                containerColor = RojoInstitucional,
                contentColor = BlancoFondo,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Nuevo",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
                    )
                }
            }
        },
        containerColor = GrisClaroTablas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // FILTROS CHIPS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Todos", "Admin", "Vendedor").forEach { rolOp ->
                    val seleccionado = (filtroRolSelected == rolOp)

                    FilterChip(
                        selected = seleccionado,
                        onClick = { filtroRolSelected = rolOp },
                        label = {
                            Text(
                                text = rolOp,
                                style = if (seleccionado) MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                else MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                            )
                        },
                        modifier = Modifier,
                        enabled = true,
                        leadingIcon = {
                            if (seleccionado) {
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                            } else {
                                val icono = when(rolOp) {
                                    "Admin" -> Icons.Outlined.AdminPanelSettings
                                    "Vendedor" -> Icons.Outlined.Badge
                                    else -> Icons.Outlined.PeopleAlt
                                }
                                Icon(icono, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            }
                        },
                        trailingIcon = null,
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulOscuroCDI.copy(alpha = 0.08f),
                            selectedLabelColor = AzulOscuroCDI,
                            selectedLeadingIconColor = AzulOscuroCDI
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionado,
                            borderColor = if (seleccionado) AzulOscuroCDI.copy(alpha = 0.5f) else GrisMedioBordes.copy(alpha = 0.4f),
                            selectedBorderColor = AzulOscuroCDI,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        ),
                        elevation = null,
                        interactionSource = null
                    )
                }
            }

            if (isLoading && lista.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulOscuroCDI, strokeWidth = 3.dp)
                }
            } else if (error != null) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BlancoFondo),
                        border = BorderStroke(1.dp, RojoInstitucional.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Warning, null, tint = RojoInstitucional, modifier = Modifier.size(32.dp))
                            Text(error, style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                            Button(
                                onClick = { viewModel.cargarUsuarios() },
                                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("Reintentar", color = BlancoFondo, style = MaterialTheme.typography.labelLarge) }
                        }
                    }
                }
            } else {
                val filtrados = lista.filter { user ->
                    val idRolNumerico = viewModel.obtenerIdRol(user)
                    when (filtroRolSelected) {
                        "Admin" -> idRolNumerico == 1
                        "Vendedor" -> idRolNumerico == 0
                        else -> true
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtrados) { usuario ->
                        UsuarioWebCardItem(
                            usuario = usuario,
                            textoRol = viewModel.obtenerTextoRol(usuario),
                            onVerDetalle = { usuarioParaVerDetalle = usuario },
                            onEditar = { usuarioParaEditar = usuario },
                            onCambiarEstado = { viewModel.cambiarEstadoUsuario(usuario.id ?: 0) }
                        )
                    }
                }
            }
        }
    }

    if (showDialogCrear) {
        FormUsuarioRealDialog(
            titulo = "Registrar Colaborador",
            usuarioExistente = null,
            viewModel = viewModel,
            onDismiss = { showDialogCrear = false },
            onGuardarCrear = { dto -> viewModel.registrarNuevoUsuario(dto) },
            onGuardarEditar = { _, _ -> }
        )
    }

    if (usuarioParaEditar != null) {
        FormUsuarioRealDialog(
            titulo = "Modificar Perfil",
            usuarioExistente = usuarioParaEditar,
            viewModel = viewModel,
            onDismiss = { usuarioParaEditar = null },
            onGuardarCrear = { _ -> },
            onGuardarEditar = { id, dto -> viewModel.editarUsuario(id, dto) }
        )
    }

    if (usuarioParaVerDetalle != null) {
        DetalleUsuarioDialog(
            usuario = usuarioParaVerDetalle!!,
            textoRol = viewModel.obtenerTextoRol(usuarioParaVerDetalle),
            onDismiss = { usuarioParaVerDetalle = null }
        )
    }

    if (showDialogPerfil) {
        AlertDialog(
            onDismissRequest = { showDialogPerfil = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Outlined.AccountCircle, null, tint = AzulOscuroCDI, modifier = Modifier.size(26.dp))
                    Text("Mi Perfil Autenticado", style = MaterialTheme.typography.titleLarge, color = AzulOscuroCDI)
                }
            },
            text = {
                if (perfil == null) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AzulOscuroCDI)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(color = GrisClaroTablas)

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Outlined.Badge, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Identificación", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text(perfil.identificacion ?: "N/A", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Outlined.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Nombre Completo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text(perfil.nombres ?: "N/A", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Outlined.Email, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Correo Electrónico", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text(perfil.email ?: "N/A", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                            }
                        }

                        HorizontalDivider(color = GrisClaroTablas)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().background(AzulOscuroCDI.copy(alpha = 0.05f), RoundedCornerShape(8.dp)).padding(10.dp)
                        ) {
                            Icon(Icons.Outlined.AdminPanelSettings, null, tint = AzulOscuroCDI, modifier = Modifier.size(22.dp))
                            Column {
                                Text("Rol del Sistema", style = MaterialTheme.typography.labelSmall, color = AzulOscuroCDI)
                                Text(viewModel.obtenerTextoRol(perfil), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = AzulOscuroCDI)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialogPerfil = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Entendido", style = MaterialTheme.typography.labelLarge) }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = BlancoFondo
        )
    }
}

@Composable
fun UsuarioWebCardItem(
    usuario: UsuarioResponse,
    textoRol: String,
    onVerDetalle: () -> Unit,
    onEditar: () -> Unit,
    onCambiarEstado: () -> Unit
) {
    val isAdmin = textoRol.equals("Admin", ignoreCase = true)
    val isActivo = usuario.activo == true

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onVerDetalle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoFondo),
        border = BorderStroke(1.dp, GrisMedioBordes.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CC ${usuario.identificacion ?: "N/A"}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(usuario.nombres ?: "Desconocido", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = GrisOscuroTexto))
                    Text(usuario.email ?: "Sin correo", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = AzulOscuroCDI, modifier = Modifier.size(20.dp))
                    }
                    Switch(
                        checked = isActivo,
                        onCheckedChange = { onCambiarEstado() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BlancoFondo,
                            checkedTrackColor = ColorVerdeActivo,
                            uncheckedThumbColor = BlancoFondo,
                            uncheckedTrackColor = GrisMedioBordes.copy(alpha = 0.6f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = GrisClaroTablas, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = if (isAdmin) RojoInstitucional.copy(alpha = 0.08f) else AzulOscuroCDI.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isAdmin) "Admin" else "Vendedor",
                        color = if (isAdmin) RojoInstitucional else AzulOscuroCDI,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    )
                }

                Surface(
                    color = if (isActivo) FondoVerdeActivo else RojoVivoInstitucional.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isActivo) "Activo" else "Inactivo",
                        color = if (isActivo) ColorVerdeActivo else RojoVivoInstitucional,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    )
                }
            }
        }
    }
}

// 📝 DIÁLOGO: FORMULARIO CREAR / EDITAR USUARIO (MEJORADO)
@Composable
fun FormUsuarioRealDialog(
    titulo: String,
    usuarioExistente: UsuarioResponse?,
    viewModel: UsuariosViewModel,
    onDismiss: () -> Unit,
    onGuardarCrear: (RegisterUsuarioDto) -> Unit,
    onGuardarEditar: (Int, UpdateUsuarioDto) -> Unit
) {
    var nombres by remember { mutableStateOf(usuarioExistente?.nombres ?: "") }
    var identificacion by remember { mutableStateOf(usuarioExistente?.identificacion ?: "") }
    var email by remember { mutableStateOf(usuarioExistente?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf(if (usuarioExistente != null) viewModel.obtenerIdRol(usuarioExistente) else 0) }
    var activo by remember { mutableStateOf(usuarioExistente?.activo ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (usuarioExistente == null) Icons.Outlined.PersonAdd else Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = AzulOscuroCDI,
                    modifier = Modifier.size(24.dp)
                )
                Text(titulo, style = MaterialTheme.typography.titleLarge, color = AzulOscuroCDI)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = GrisClaroTablas)

                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombre Completo") },
                    leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                )

                if (usuarioExistente == null) {
                    OutlinedTextField(
                        value = identificacion,
                        onValueChange = { identificacion = it },
                        label = { Text("Cédula de Ciudadanía") },
                        leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                )

                if (usuarioExistente == null) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña de Acceso") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AzulOscuroCDI, focusedLabelColor = AzulOscuroCDI)
                    )
                }

                HorizontalDivider(color = GrisClaroTablas)

                // Segmento de Asignación de Rol
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Rol Asignado", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { rolSeleccionado = 0 }) {
                            RadioButton(selected = rolSeleccionado == 0, onClick = { rolSeleccionado = 0 }, colors = RadioButtonDefaults.colors(selectedColor = AzulOscuroCDI))
                            Text("Vendedor", style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { rolSeleccionado = 1 }) {
                            RadioButton(selected = rolSeleccionado == 1, onClick = { rolSeleccionado = 1 }, colors = RadioButtonDefaults.colors(selectedColor = AzulOscuroCDI))
                            Text("Administrador", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                if (usuarioExistente != null) {
                    HorizontalDivider(color = GrisClaroTablas)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.PowerSettingsNew, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Text("Usuario Habilitado", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = activo,
                            onCheckedChange = { activo = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BlancoFondo, checkedTrackColor = ColorVerdeActivo)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombres.isNotBlank() && email.isNotBlank()) {
                        if (usuarioExistente == null) {
                            onGuardarCrear(RegisterUsuarioDto(identificacion, nombres, email, password, rolSeleccionado))
                        } else {
                            onGuardarEditar(usuarioExistente.id ?: 0, UpdateUsuarioDto(nombres, email, rolSeleccionado, activo))
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Guardar Cambios", style = MaterialTheme.typography.labelLarge) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", style = MaterialTheme.typography.labelLarge, color = RojoInstitucional)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = BlancoFondo
    )
}

// 🔍 DIÁLOGO: DETALLE FICHA DE USUARIO (MEJORADO)
@Composable
fun DetalleUsuarioDialog(usuario: UsuarioResponse, textoRol: String, onDismiss: () -> Unit) {
    val isAdmin = textoRol.equals("Admin", ignoreCase = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Outlined.ContactPage, null, tint = AzulOscuroCDI, modifier = Modifier.size(24.dp))
                Text("Ficha del Colaborador", style = MaterialTheme.typography.titleLarge, color = AzulOscuroCDI)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = GrisClaroTablas)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Fingerprint, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Column {
                        Text("ID Interno Sistema", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("# ${usuario.id ?: "N/A"}", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Person, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Nombre Completo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(usuario.nombres ?: "Desconocido", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Badge, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Documento de Identidad", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(usuario.identificacion ?: "N/A", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Email, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Correo Corporativo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(usuario.email ?: "N/A", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                    }
                }

                HorizontalDivider(color = GrisClaroTablas)

                // Doble Fila de Badges para Estado y Rol Estéticos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = if (isAdmin) RojoInstitucional.copy(alpha = 0.08f) else AzulOscuroCDI.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rol", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(if (isAdmin) "Administrador" else "Vendedor", color = if (isAdmin) RojoInstitucional else AzulOscuroCDI, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        color = if (usuario.activo == true) FondoVerdeActivo else RojoVivoInstitucional.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Estado", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(if (usuario.activo == true) "Activo" else "Inactivo", color = if (usuario.activo == true) ColorVerdeActivo else RojoVivoInstitucional, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar Ficha", style = MaterialTheme.typography.labelLarge) }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = BlancoFondo
    )
}