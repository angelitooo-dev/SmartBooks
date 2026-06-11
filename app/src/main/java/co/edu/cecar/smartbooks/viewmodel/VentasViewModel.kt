package co.edu.cecar.smartbooks.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.edu.cecar.smartbooks.core.security.TokenManager
import co.edu.cecar.smartbooks.data.model.*
import co.edu.cecar.smartbooks.data.repository.VentasRepository
import kotlinx.coroutines.launch

class VentasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VentasRepository()
    private val context = application.applicationContext


    var listaVentas = mutableStateOf<List<VentaResponse>>(emptyList())
    var ventaDetalle = mutableStateOf<VentaResponse?>(null)

    var isLoading = mutableStateOf(false)
    var listErrorMessage = mutableStateOf<String?>(null)
    var operacionExitosa = mutableStateOf(false)

    val carritoTemp = mutableStateListOf<CarritoItem>()

    fun cargarVentas() {
        viewModelScope.launch {
            isLoading.value = true
            listErrorMessage.value = null
            try {
                val token = TokenManager.getToken(context) ?: ""
                listaVentas.value = repository.obtenerVentas(token)
            } catch (e: Exception) {
                listErrorMessage.value = e.localizedMessage ?: "Error de red al cargar ventas"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun obtenerDetalleVenta(id: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val token = TokenManager.getToken(context) ?: ""
                ventaDetalle.value = repository.obtenerDetalleVenta(token, id)
            } catch (e: Exception) {
                listErrorMessage.value = "Error al obtener detalles: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun agregarAlCarrito(item: CarritoItem) {
        val indexExistente = carritoTemp.indexOfFirst { it.libroId == item.libroId && it.lote == item.lote }
        if (indexExistente != -1) {
            val antiguoItem = carritoTemp[indexExistente]
            carritoTemp[indexExistente] = antiguoItem.copy(cantidad = antiguoItem.cantidad + item.cantidad)
        } else {
            carritoTemp.add(item)
        }
    }

    fun removerDelCarrito(item: CarritoItem) {
        carritoTemp.remove(item)
    }

    fun setOperacionExitosa(valor: Boolean) {
        operacionExitosa.value = valor
    }

    fun procesarVenta(identificacionCliente: String, numeroComprobante: String, observaciones: String) {
        if (carritoTemp.isEmpty()) {
            listErrorMessage.value = "Debe añadir al menos un artículo a la lista para facturar."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            listErrorMessage.value = null
            operacionExitosa.value = false
            try {
                val token = TokenManager.getToken(context) ?: ""

                val itemsDto = carritoTemp.map {
                    RegistrarVentaItem(
                        libroId = it.libroId,
                        lote = it.lote,
                        cantidad = it.cantidad
                    )
                }

                val requestDto = RegistrarVenta(
                    identificacionCliente = identificacionCliente.trim(),
                    numeroComprobante = numeroComprobante.trim(),
                    observaciones = observaciones.trim(),
                    items = itemsDto
                )

                val exito = repository.registrarVenta(token, requestDto)
                if (exito) {
                    carritoTemp.clear()
                    operacionExitosa.value = true
                    cargarVentas()
                } else {
                    listErrorMessage.value = "El servidor rechazó la operación. Verifique stock e identificación."
                }
            } catch (e: Exception) {
                listErrorMessage.value = "Fallo de conexión crítico: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}