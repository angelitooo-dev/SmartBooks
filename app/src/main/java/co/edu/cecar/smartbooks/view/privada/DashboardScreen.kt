package co.edu.cecar.smartbooks.view.privada

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import co.edu.cecar.smartbooks.R
import co.edu.cecar.smartbooks.ui.theme.RojoInstitucional
import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo

import co.edu.cecar.smartbooks.viewmodel.DashboardUiState
import co.edu.cecar.smartbooks.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onIrALibros: () -> Unit,
    onIrAClientes: () -> Unit,
    onIrALotes: () -> Unit,
    onIrAInventarios: () -> Unit,
    onIrAUsuarios: () -> Unit,
    onIrAVentas: () -> Unit,
    onIrAIngresos: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                DashboardViewModel(app)
            }
        }
    )
) {
    LaunchedEffect(Unit) {
        viewModel.cargarDatosDashboard()
    }

    var itemSeleccionado by remember { mutableStateOf(-1) }
    var menuUsuarioExpandido by remember { mutableStateOf(false) }
    var showDialogPerfil by remember { mutableStateOf(false) }

    when (val state = viewModel.uiState) {
        is DashboardUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RojoInstitucional)
            }
        }
        is DashboardUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.cargarDatosDashboard() },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoInstitucional)
                    ) {
                        Text("Reintentar", color = Color.White)
                    }
                }
            }
        }
        is DashboardUiState.Success -> {
            val data = state.contadores
            val listaVentasReales = state.ultimasVentas
            val totalVentasMesReal = state.ventasDelMesCount
            val totalIngresosMesReal = state.ingresosDelMesSum

            val nombreUsuario = data.mensajeBienvenida?.substringAfter(", ")?.trim() ?: "Usuario"
            val currency = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply { maximumFractionDigits = 0 }

            Scaffold(
                modifier = modifier,
                containerColor = Color(0xFFF3F4F6),
                topBar = {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            Box(modifier = Modifier.padding(start = 12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .clickable { menuUsuarioExpandido = true }
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccountCircle,
                                        contentDescription = "Usuario",
                                        tint = AzulOscuroCDI,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(text = "Hola,", fontSize = 10.sp, color = Color.Gray)
                                        Text(
                                            text = nombreUsuario,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrisOscuroTexto,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.widthIn(max = 90.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = menuUsuarioExpandido,
                                    onDismissRequest = { menuUsuarioExpandido = false },
                                    modifier = Modifier.background(BlancoFondo).widthIn(min = 180.dp)
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                                        Column {
                                            Text(text = "Usuario Registrado", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = nombreUsuario, fontSize = 14.sp, fontWeight = FontWeight.Black, color = GrisOscuroTexto)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(text = "Sesión activa correctamente", fontSize = 11.sp, color = AzulOscuroCDI)
                                        }
                                    }
                                    HorizontalDivider(color = Color(0xFFE5E7EB))
                                    DropdownMenuItem(
                                        text = { Text("Ver mi perfil", fontSize = 13.sp, color = GrisOscuroTexto) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Outlined.ManageAccounts,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = AzulOscuroCDI
                                            )
                                        },
                                        onClick = {
                                            menuUsuarioExpandido = false
                                            showDialogPerfil = true
                                        }
                                    )
                                }
                            }
                        },
                        title = {
                            Image(
                                painter = painterResource(id = R.drawable.cdi_logo),
                                contentDescription = "Logo CDI",
                                modifier = Modifier.width(185.dp).height(55.dp), // Logo ampliado visualmente
                                contentScale = ContentScale.Fit
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.padding(end = 12.dp).background(Color(0xFFFEE2E2), CircleShape).size(36.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Logout, contentDescription = "Salir", tint = RojoInstitucional, modifier = Modifier.size(18.dp))
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BlancoFondo),
                        modifier = Modifier.height(75.dp)
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = BlancoFondo, tonalElevation = 8.dp) {
                        NavigationBarItem(
                            selected = itemSeleccionado == 0,
                            onClick = { itemSeleccionado = 0; onIrAClientes() },
                            label = { Text("Clientes", fontSize = 11.sp) },
                            icon = { Icon(Icons.Outlined.People, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = itemSeleccionado == 1,
                            onClick = { itemSeleccionado = 1; onIrALibros() },
                            label = { Text("Libros", fontSize = 11.sp) },
                            icon = { Icon(Icons.Outlined.MenuBook, contentDescription = null) }
                        )
                        NavigationBarItem(
                            selected = itemSeleccionado == 2,
                            onClick = { itemSeleccionado = 2; onIrAVentas() },
                            label = { Text("Ventas", fontSize = 11.sp) },
                            icon = { Icon(Icons.Outlined.LocalMall, contentDescription = null) }
                        )
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Black, color = GrisOscuroTexto)
                    Text(text = data.mensajeBienvenida ?: "Bienvenido al sistema", color = Color.Gray, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            DashboardGridCard("Total Clientes", data.totalClientes.toString(), Icons.Outlined.People, RojoInstitucional, modifier = Modifier.clickable { onIrAClientes() })
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            DashboardGridCard("Libros", data.librosRegistrados.toString(), Icons.Outlined.MenuBook, AzulOscuroCDI, modifier = Modifier.clickable { onIrALibros() })
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            DashboardGridCard("Ventas Mes", totalVentasMesReal.toString(), Icons.Outlined.LocalMall, RojoInstitucional, modifier = Modifier.clickable { onIrAVentas() })
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            DashboardGridCard("Ingresos Mes", currency.format(totalIngresosMesReal), Icons.Outlined.AttachMoney, AzulOscuroCDI, isCurrency = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Últimas ventas de hoy", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GrisOscuroTexto)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (listaVentasReales.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("No se registran ventas hoy", color = Color.Gray, fontSize = 13.sp)
                        }
                    } else {
                        listaVentasReales.forEach { venta ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).clickable { onIrAVentas() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = BlancoFondo)
                            ) {
                                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Factura #${venta.numeroComprobante ?: "S/N"}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${venta.fecha?.take(10) ?: ""} • ${venta.clienteNombre ?: "Cliente"}", color = Color.Gray, fontSize = 11.sp, maxLines = 1)
                                    }
                                    Text(currency.format(venta.total ?: 0.0), fontWeight = FontWeight.Black, color = RojoInstitucional, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    // RESTAURADO: Sección de Otros módulos
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Otros módulos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GrisOscuroTexto)
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilaModuloAdicional("Gestión de Ingresos", Icons.Outlined.AttachMoney, onIrAIngresos)
                        FilaModuloAdicional("Gestión de Lotes", Icons.Outlined.Layers, onIrALotes)
                        FilaModuloAdicional("Control de Inventario", Icons.Outlined.Inventory2, onIrAInventarios)
                        FilaModuloAdicional("Control de Usuarios", Icons.Outlined.ManageAccounts, onIrAUsuarios)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (showDialogPerfil) {
                AlertDialog(
                    onDismissRequest = { showDialogPerfil = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(26.dp))
                            Text("Mi Perfil Autenticado", style = MaterialTheme.typography.titleLarge, color = AzulOscuroCDI, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(color = Color(0xFFE5E7EB))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.Badge, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Identificación", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text("N/A", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Nombre Completo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(nombreUsuario, style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Correo Electrónico", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text("admincdi@yopmail.com", style = MaterialTheme.typography.bodyMedium, color = GrisOscuroTexto)
                                }
                            }

                            HorizontalDivider(color = Color(0xFFE5E7EB))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AzulOscuroCDI.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(22.dp))
                                Column {
                                    Text("Rol del Sistema", style = MaterialTheme.typography.labelSmall, color = AzulOscuroCDI)
                                    Text("Admin", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = AzulOscuroCDI)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showDialogPerfil = false },
                            colors = ButtonDefaults.buttonColors(containerColor = AzulOscuroCDI),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Entendido", color = Color.White)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = BlancoFondo
                )
            }
        }
    }
}

@Composable
fun FilaModuloAdicional(titulo: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoFondo)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = AzulOscuroCDI, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GrisOscuroTexto)
            }
            Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
fun DashboardGridCard(title: String, value: String, icon: ImageVector, color: Color, isCurrency: Boolean = false, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().height(115.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = value, fontSize = if (isCurrency) 16.sp else 20.sp, fontWeight = FontWeight.Black, color = GrisOscuroTexto, maxLines = 1)
            }
        }
    }
}