package co.edu.cecar.smartbooks.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable object LoginRoute : NavKey
@Serializable object SolicitarOtpRoute : NavKey
@Serializable object RestablecerPasswordRoute : NavKey

@Serializable object DashboardRoute : NavKey
@Serializable object LibrosRoute : NavKey
@Serializable object ClientesRoute : NavKey
@Serializable object LotesRoute : NavKey
@Serializable object InventariosRoute : NavKey
@Serializable object UsuariosRoute : NavKey
@Serializable object PerfilUsuarioRoute : NavKey
@Serializable object VentasRoute : NavKey
@Serializable object IngresosRoute : NavKey