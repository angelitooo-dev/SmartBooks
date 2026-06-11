package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val context = application.applicationContext

    var correo by mutableStateOf("")
        private set
    var contrasena by mutableStateOf("")
        private set
    var isRememberMeChecked by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun onCorreoChange(nuevoCorreo: String) {
        correo = nuevoCorreo
    }

    fun onContrasenaChange(nuevaContrasena: String) {
        contrasena = nuevaContrasena
    }

    fun onRememberMeChange(checked: Boolean) {
        isRememberMeChecked = checked
    }

    fun iniciarSesion(onSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            errorMessage = "Por favor completa todos los campos."
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            authRepository.login(correo.trim(), contrasena).fold(
                onSuccess = { response ->
                    // Guardado seguro del Token JWT en DataStore (Requerimiento 3.4.4)
                    TokenManager.saveToken(context, response.token)
                    onSuccess()
                },
                onFailure = { errorMessage = it.localizedMessage }
            )
            isLoading = false
        }
    }

    fun solicitarCodigo(correo: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            authRepository.solicitarRestablecimiento(correo).fold(
                onSuccess = { onSuccess() },
                onFailure = { errorMessage = it.localizedMessage }
            )
            isLoading = false
        }
    }

    fun restablecerContrasena(codigo: String, nuevaPass: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            authRepository.restablecerContrasena(codigo, nuevaPass).fold(
                onSuccess = { onSuccess() },
                onFailure = { errorMessage = it.localizedMessage }
            )
            isLoading = false
        }
    }
}