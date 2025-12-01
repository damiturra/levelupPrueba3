package com.example.levelupgamer.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.Categoria
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    // ---------- STATE PRINCIPAL ----------
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    var filtroState = FiltroState()
        private set

    // ---------- INIT ----------
    init {
        cargarCategorias()
        cargarProductos()
    }

    // ---------- CARGAS ----------
    private fun cargarCategorias() {
        viewModelScope.launch {
            repository.obtenerTodasLasCategorias().collect { list ->
                _categorias.value = list
            }
        }
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            repository.obtenerTodosLosProductos().collect { list ->
                _productos.value = list
            }
        }
    }

    fun cargarProductosPorCategoria(categoriaId: Int) {
        viewModelScope.launch {
            repository.obtenerProductosPorCategoria(categoriaId).collect { list ->
                _productos.value = list
            }
        }
    }

    // ---------- BÚSQUEDA Y ORDEN ----------
    fun buscarProductos(busqueda: String) {
        if (busqueda.isBlank()) {
            cargarProductos()
            return
        }
        viewModelScope.launch {
            repository.buscarProductos(busqueda).collect { list ->
                _productos.value = list
            }
        }
    }

    fun ordenarProductosPorPrecioAsc() {
        viewModelScope.launch {
            repository.obtenerProductosOrdenadosPorPrecioAsc().collect { list ->
                _productos.value = list
            }
        }
    }

    fun ordenarProductosPorPrecioDesc() {
        viewModelScope.launch {
            repository.obtenerProductosOrdenadosPorPrecioDesc().collect { list ->
                _productos.value = list
            }
        }
    }

    // ---------- PRODUCTO INDIVIDUAL ----------
    /** Flow directo desde Room (úsalo con collectAsState en la UI). */
    fun obtenerProductoPorCodigoFlow(codigo: String): Flow<Producto?> =
        repository.obtenerProductoPorCodigoFlow(codigo)

    /** Alternativa para mantener el seleccionado en el VM. */
    fun seleccionarProducto(codigo: String) {
        viewModelScope.launch {
            repository.obtenerProductoPorCodigoFlow(codigo).collect { p ->
                _productoSeleccionado.value = p
            }
        }
    }

    // ---------- MANEJO DE FILTROS ----------
    fun onBusquedaChange(value: String) {
        filtroState = filtroState.copy(busqueda = value)
        buscarProductos(value)
    }

    fun onCategoriaSeleccionadaChange(categoriaId: Int?) {
        filtroState = filtroState.copy(categoriaSeleccionada = categoriaId)
        if (categoriaId != null) {
            cargarProductosPorCategoria(categoriaId)
        } else {
            cargarProductos()
        }
    }

    fun onOrdenChange(orden: OrdenProductos) {
        filtroState = filtroState.copy(orden = orden)
        when (orden) {
            OrdenProductos.PRECIO_ASC  -> ordenarProductosPorPrecioAsc()
            OrdenProductos.PRECIO_DESC -> ordenarProductosPorPrecioDesc()
            OrdenProductos.NINGUNO     -> cargarProductos()
        }
    }

    fun limpiarFiltros() {
        filtroState = FiltroState()
        cargarProductos()
    }
}

// ---------- MODELOS DE UI ----------
data class FiltroState(
    val busqueda: String = "",
    val categoriaSeleccionada: Int? = null,
    val orden: OrdenProductos = OrdenProductos.NINGUNO
)

/**
 * OJO: quitamos CALIFICACION para que no rompa si tu entidad/DAO no lo soportan.
 */
enum class OrdenProductos {
    NINGUNO,
    PRECIO_ASC,
    PRECIO_DESC
}
