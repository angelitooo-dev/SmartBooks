package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import co.edu.cecar.smartbooks.data.model.LibroResponse
import co.edu.cecar.smartbooks.viewmodel.LibrosUiState
import co.edu.cecar.smartbooks.viewmodel.LibrosViewModel
import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.RojoVivoInstitucional
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo
import co.edu.cecar.smartbooks.ui.theme.GrisClaroTablas

val GrisOscuroTexto = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrosScreen(
    onVolver: () -> Unit,
    viewModel: LibrosViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                LibrosViewModel(app)
            }
        }
    )
) {
    var queryBusqueda by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.cargarLibros()
    }

    Scaffold(
        containerColor = GrisClaroTablas,
        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = "Gestion de Libros",
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
                    text = "Nuevo Libro",
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

            // Se eliminó la fila Row manual del título viejo para evitar duplicados.

            OutlinedTextField(
                value = queryBusqueda,
                onValueChange = { queryBusqueda = it },
                placeholder = { Text("Buscar por nombre, nivel...", color = Color.Gray.copy(0.7f), style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catálogo disponible",
                    style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI, fontSize = 18.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (val state = viewModel.uiState) {
                    is LibrosUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AzulOscuroCDI)
                        }
                    }
                    is LibrosUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.cargarLibros() }, colors = ButtonDefaults.buttonColors(containerColor = RojoVivoInstitucional), shape = RoundedCornerShape(8.dp)) {
                                Text("Reintentar", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                    is LibrosUiState.Success -> {
                        val librosFiltrados = state.libros.filter {
                            it.nombre.contains(queryBusqueda, ignoreCase = true) || it.nivel.contains(queryBusqueda, ignoreCase = true)
                        }

                        if (librosFiltrados.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No se encontraron libros", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 88.dp)
                            ) {
                                items(librosFiltrados) { libro ->
                                    TarjetaLibro(
                                        libro = libro,
                                        onEditarClick = { viewModel.libroSeleccionadoParaEditar = libro }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModel.libroSeleccionadoParaEditar?.let { libro ->
            EditarLibroDialog(
                libro = libro,
                onDismiss = { viewModel.libroSeleccionadoParaEditar = null },
                onConfirm = { nom, niv, tip, edi ->
                    viewModel.modificarLibro(libro.id, nom, niv, tip, edi)
                }
            )
        }

        if (viewModel.mostrarDialogoCrear) {
            CrearLibroDialog(
                onDismiss = { viewModel.mostrarDialogoCrear = false },
                onConfirm = { nom, niv, tip, edi, uni, lot, comp, vent ->
                    viewModel.guardarNuevoLibro(nom, niv, tip, edi, uni, lot, comp, vent)
                }
            )
        }
    }
}

@Composable
fun TarjetaLibro(libro: LibroResponse, onEditarClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = libro.nombre,
                        style = MaterialTheme.typography.titleLarge.copy(color = GrisOscuroTexto, fontSize = 18.sp)
                    )
                }

                IconButton(
                    onClick = onEditarClick,
                    modifier = Modifier
                        .background(GrisClaroTablas, CircleShape)
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = AzulOscuroCDI,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "NIVEL", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp))
                            Text(text = libro.nivel, style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto, fontWeight = FontWeight.Bold))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Layers, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "EDICIÓN", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp))
                            Text(text = libro.edicion, style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto))
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val tipoTexto = when (libro.tipo.trim()) {
                        "1" -> "StudentsBook"
                        "2" -> "Workbook"
                        else -> libro.tipo
                    }
                    val badgeColor = if (tipoTexto.lowercase().contains("work")) Color(0xFFEEF2FF) else Color(0xFFE0F2FE)
                    val badgeTextColor = if (tipoTexto.lowercase().contains("work")) Color(0xFF4F46E5) else Color(0xFF0284C7)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "TIPO", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp))
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .background(badgeColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = tipoTexto, color = badgeTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Numbers, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "N° LOTE", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp))
                            Text(text = libro.lote?.toString() ?: "N/A", style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto))
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
                    Icon(imageVector = Icons.Default.Inventory, contentDescription = null, tint = GrisOscuroTexto, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Disponibilidad:", style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto, fontWeight = FontWeight.Bold))
                }

                val units = libro.unidades ?: 0
                val unitsValidated = if (units < 0) 0 else units

                val (bgColor, textColor, textoStock) = when {
                    unitsValidated == 0 -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Agotado")
                    unitsValidated <= 5 -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "$unitsValidated disp.")
                    else -> Triple(Color(0xFFDCFCE7), Color(0xFF16A34A), "$unitsValidated unidades")
                }

                Box(
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = textoStock,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun FormFieldLibro(label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector, keyboardType: KeyboardType = KeyboardType.Text) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearLibroDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Int, Int, Int, Int) -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var niv by remember { mutableStateOf("") }
    var edi by remember { mutableStateOf("") }
    var uni by remember { mutableStateOf("") }
    var lot by remember { mutableStateOf("") }
    var comp by remember { mutableStateOf("") }
    var vent by remember { mutableStateOf("") }
    var errorValidacion by remember { mutableStateOf(false) }

    val opcionesTipo = listOf("StudentsBook", "Workbook")
    var tip by remember { mutableStateOf(opcionesTipo[0]) }
    var expandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = BlancoFondo,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AzulOscuroCDI.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Book, null, tint = AzulOscuroCDI, modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Registrar Nuevo Libro", style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorValidacion) {
                    Text("Completa todos los campos obligatorios.", color = Color.Red, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                FormFieldLibro("Nombre del Libro", nom, { nom = it }, Icons.Default.MenuBook)
                FormFieldLibro("Nivel", niv, { niv = it }, Icons.Default.School)

                ExposedDropdownMenuBox(
                    expanded = expandido,
                    onExpandedChange = { expandido = !expandido },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = tip,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Libro", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Default.Category, null, tint = AzulOscuroCDI, modifier = Modifier.size(20.dp)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulOscuroCDI,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = AzulOscuroCDI,
                            focusedTextColor = GrisOscuroTexto,
                            unfocusedTextColor = GrisOscuroTexto
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandido,
                        onDismissRequest = { expandido = false },
                        modifier = Modifier.background(BlancoFondo)
                    ) {
                        opcionesTipo.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion, style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto)) },
                                onClick = {
                                    tip = opcion
                                    expandido = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                FormFieldLibro("Edición", edi, { edi = it }, Icons.Default.Layers)
                FormFieldLibro("Unidades", uni, { uni = it }, Icons.Default.Inventory, KeyboardType.Number)
                FormFieldLibro("N° Lote", lot, { lot = it }, Icons.Default.Numbers, KeyboardType.Number)
                FormFieldLibro("Costo Compra", comp, { comp = it }, Icons.Default.ShoppingCart, KeyboardType.Number)
                FormFieldLibro("Precio Venta", vent, { vent = it }, Icons.Default.AttachMoney, KeyboardType.Number)

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        val u = uni.toIntOrNull()
                        val l = lot.toIntOrNull()
                        val c = comp.toIntOrNull()
                        val v = vent.toIntOrNull()

                        if (nom.isBlank() || niv.isBlank() || edi.isBlank() || u == null || l == null || c == null || v == null) {
                            errorValidacion = true
                        } else {
                            errorValidacion = false
                            onConfirm(nom, niv, tip, edi, u, l, c, v)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoVivoInstitucional),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar Libro", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarLibroDialog(
    libro: LibroResponse,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var nom by remember { mutableStateOf(libro.nombre) }
    var niv by remember { mutableStateOf(libro.nivel) }
    var edi by remember { mutableStateOf(libro.edicion) }

    val opcionesTipo = listOf("StudentsBook", "Workbook")
    var tip by remember {
        mutableStateOf(
            if (libro.tipo.trim() == "1") "StudentsBook"
            else if (libro.tipo.trim() == "2") "Workbook"
            else libro.tipo
        )
    }
    var expandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = BlancoFondo,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AzulOscuroCDI.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.EditNote, null, tint = AzulOscuroCDI, modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Modificar Libro", style = MaterialTheme.typography.titleLarge.copy(color = AzulOscuroCDI))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FormFieldLibro("Nombre del Libro", nom, { nom = it }, Icons.Default.MenuBook)
                FormFieldLibro("Nivel", niv, { niv = it }, Icons.Default.School)

                ExposedDropdownMenuBox(
                    expanded = expandido,
                    onExpandedChange = { expandido = !expandido },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = tip,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Default.Category, null, tint = AzulOscuroCDI, modifier = Modifier.size(20.dp)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulOscuroCDI,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = AzulOscuroCDI,
                            focusedTextColor = GrisOscuroTexto,
                            unfocusedTextColor = GrisOscuroTexto
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandido,
                        onDismissRequest = { expandido = false },
                        modifier = Modifier.background(BlancoFondo)
                    ) {
                        opcionesTipo.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion, style = MaterialTheme.typography.bodyMedium.copy(color = GrisOscuroTexto)) },
                                onClick = {
                                    tip = opcion
                                    expandido = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                FormFieldLibro("Edición", edi, { edi = it }, Icons.Default.Layers)

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { onConfirm(nom, niv, tip, edi) },
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
            TextButton(onClick = onDismiss) { Text("Cancelar", style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray)) }
        }
    )
}