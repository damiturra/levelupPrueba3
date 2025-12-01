package com.example.levelupgamer.viewmodel.vendedor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class VendorProdUI(
    val vendedorId: Long,
    val isLoading: Boolean = false,
    val error: String? = null,
    val productos: List<Producto> = emptyList(),
    val editando: Producto? = null
)

class VendedorProductosViewModel(
    app: Application,
    private val repo: ProductoRepository,
    private val vendedorIdArg: Long
) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(
        VendorProdUI(vendedorId = vendedorIdArg, isLoading = true)
    )
    val ui: StateFlow<VendorProdUI> = _ui.asStateFlow()

    init {
        // Observa los productos del vendedor
        viewModelScope.launch {
            repo.observeByVendedor(vendedorIdArg)
                .catch { e ->
                    _ui.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { list ->
                    _ui.update { it.copy(productos = list, isLoading = false, error = null) }
                }
        }
    }

    /** Permite forzar un refresh visual (ej. mostrar spinner si quieres) */
    fun refresh() {
        _ui.update { it.copy(isLoading = true) }
        // Al estar colectando el Flow del repo, el UI se actualizará solo cuando cambie la DB.
        // Si aquí quisieras forzar algo adicional, lo harías con llamadas al repo.
    }

    /** Abre diálogo con un producto nuevo por defecto */
    fun nuevo() {
        _ui.update {
            it.copy(
                editando = Producto(
                    codigo = UUID.randomUUID().toString(),
                    nombre = "",
                    descripcion = "",
                    precio = 0,
                    categoriaId = 0,
                    categoriaNombre = "",
                    stock = 100,
                    imagenUrl = "",
                    fabricante = "",
                    calificacion = 0f,      // si tu entidad lo tiene
                    vendedorId = vendedorIdArg,
                    activo = true           // ✅ por defecto activo
                )
            )
        }
    }

    /** Abre diálogo con un producto existente */
    fun editar(p: Producto) {
        _ui.update { it.copy(editando = p) }
    }

    /** Cierra el diálogo */
    fun cancelar() {
        _ui.update { it.copy(editando = null) }
    }

    /** Guarda (crear/editar). Ahora sí aplica el `activo` recibido. */
    fun guardar(
        nombre: String,
        descripcion: String,
        precio: Int,
        categoriaId: Int,
        categoriaNombre: String,
        activo: Boolean
    ) = viewModelScope.launch {
        val ed = _ui.value.editando ?: return@launch
        _ui.update { it.copy(isLoading = true, error = null) }
        try {
            val p = ed.copy(
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                precio = precio,
                categoriaId = categoriaId,
                categoriaNombre = categoriaNombre.trim(),
                vendedorId = vendedorIdArg,
                activo = activo                // ✅ APLICADO!
            )
            // Usa upsert: crea si no existe, actualiza si existe
            repo.upsert(p)
            _ui.update { it.copy(editando = null, isLoading = false) }
        } catch (e: Exception) {
            _ui.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    /** Elimina por código (versión directa) */
    fun eliminar(codigo: String) = viewModelScope.launch {
        _ui.update { it.copy(isLoading = true, error = null) }
        try {
            repo.eliminarPorCodigo(codigo)
            _ui.update { it.copy(isLoading = false) }
        } catch (e: Exception) {
            _ui.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    /** Alternar estado activo in-place (útil para el switch de la card) */
    fun toggleActivo(codigo: String) = viewModelScope.launch {
        val actual = ui.value.productos.find { it.codigo == codigo } ?: return@launch
        try {
            repo.upsert(actual.copy(activo = !actual.activo))
        } catch (e: Exception) {
            _ui.update { it.copy(error = e.message) }
        }
    }
}
