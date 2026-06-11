package co.edu.cecar.smartbooks.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import co.edu.cecar.smartbooks.view.privada.*
import co.edu.cecar.smartbooks.view.publica.LoginScreen
import co.edu.cecar.smartbooks.view.publica.SolicitarOtpScreen
import co.edu.cecar.smartbooks.view.publica.RestablecerPasswordScreen
import co.edu.cecar.smartbooks.viewmodel.LoginViewModel

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel = viewModel()
) {
    val backStack = rememberNavBackStack(LoginRoute)

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<LoginRoute> {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(DashboardRoute)
                    },
                    onRecuperarPass = {
                        backStack.add(SolicitarOtpRoute)
                    }
                )
            }

            entry<SolicitarOtpRoute> {
                SolicitarOtpScreen(
                    viewModel = loginViewModel,
                    onCodigoEnviado = {
                        backStack.add(RestablecerPasswordRoute)
                    },
                    onVolver = { backStack.removeLastOrNull() }
                )
            }

            entry<RestablecerPasswordRoute> {
                RestablecerPasswordScreen(
                    viewModel = loginViewModel,
                    onPasswordCambiado = {
                        backStack.clear()
                        backStack.add(LoginRoute)
                    },
                    onVolver = { backStack.removeLastOrNull() }
                )
            }

            entry<DashboardRoute> {
                DashboardScreen(
                    onLogout = {
                        backStack.clear()
                        backStack.add(LoginRoute)
                    },
                    onIrAClientes = { backStack.add(ClientesRoute) },
                    onIrALibros = { backStack.add(LibrosRoute) },
                    onIrAVentas = { backStack.add(VentasRoute) },
                    onIrALotes = { backStack.add(LotesRoute) },
                    onIrAInventarios = { backStack.add(InventariosRoute) },
                    onIrAUsuarios = { backStack.add(UsuariosRoute) },
                    onIrAIngresos = { backStack.add(IngresosRoute) }
                )
            }

            entry<LibrosRoute> { LibrosScreen(onVolver = { backStack.removeLastOrNull() }) }
            entry<ClientesRoute> { ClientesScreen(onVolver = { backStack.removeLastOrNull() }) }
            entry<LotesRoute> { LotesScreen(onVolver = { backStack.removeLastOrNull() }) }
            entry<InventariosRoute> { InventariosScreen(onVolver = { backStack.removeLastOrNull() }) }
            entry<VentasRoute> { VentasScreen(onVolver = { backStack.removeLastOrNull() }) }
            entry<UsuariosRoute> { UsuariosScreen(onVolver = { backStack.removeLastOrNull() }) }

            entry<IngresosRoute> {
                IngresosScreen(
                    onVolver = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}