package com.example.levelupgamer.ui.homevendedor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.viewmodel.vendedor.VendedorProductosViewModel
import androidx.compose.material.icons.filled.Logout
import com.example.levelupgamer.data.session.SessionManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorProductosScreen(
    navController: NavController,
    vendedorId: Long
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val vm: VendedorProductosViewModel = viewModel(
        key = "vendor-$vendedorId",
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val app = context.applicationContext as android.app.Application
                val db = com.example.levelupgamer.data.database.AppDatabase.getDatabase(app)
                val repo = com.example.levelupgamer.data.repository.ProductoRepository(
                    db.productoDao(),
                    db.categoriaDao()
                )
                return VendedorProductosViewModel(
                    app = app,
                    repo = repo,
                    vendedorIdArg = vendedorId
                ) as T
            }
        }
    )

    val ui by vm.ui.collectAsState()

    // Controles locales de UI
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var soloActivos by remember { mutableStateOf(false) }

    // Filtrado visual (no toca el VM)
    val visibles = remember(ui.productos, query, soloActivos) {
        ui.productos
            .filter { if (soloActivos) it.activo else true }
            .filter {
                val q = query.text.trim().lowercase()
                if (q.isBlank()) true
                else it.nombre.lowercase().contains(q) ||
                        it.codigo.lowercase().contains(q) ||
                        it.categoriaNombre.lowercase().contains(q)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis productos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // 🔹 Cerrar sesión
                    IconButton(onClick = {
                        SessionManager.clear()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }

                    // 🔹 Botón Nuevo (lo que ya tenías)
                    IconButton(onClick = { vm.nuevo() }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.nuevo() }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo producto")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Búsqueda
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (query.text.isNotEmpty()) {
                        IconButton(onClick = { query = TextFieldValue("") }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                },
                singleLine = true,
                placeholder = { Text("Buscar por nombre, código o categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            // Filtro + loading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { soloActivos = !soloActivos },
                    label = { Text(if (soloActivos) "Mostrando activos" else "Todos") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (soloActivos) Icons.Default.CheckCircle else Icons.Default.Inventory2,
                            contentDescription = null
                        )
                    }
                )
                if (ui.isLoading && ui.productos.isEmpty()) {
                    LinearProgressIndicator(Modifier.fillMaxWidth().height(4.dp))
                }
            }

            if (visibles.isEmpty()) {
                EmptyState(
                    isFiltered = query.text.isNotBlank() || soloActivos,
                    onClearFilters = {
                        query = TextFieldValue("")
                        soloActivos = false
                    },
                    onCreate = { vm.nuevo() }
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(visibles, key = { it.codigo }) { p ->
                        ProductoCard(
                            p = p,
                            onEdit = { vm.editar(p) },
                            onDelete = { vm.eliminar(p.codigo) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo Crear/Editar (evita smart cast)
    ui.editando?.let { ed ->
        ProductoDialog(
            inicial = ed,
            onDismiss = { vm.cancelar() },
            onSave = { nombre, descripcion, precio, categoriaId, categoriaNombre, activo ->
                vm.guardar(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    categoriaId = categoriaId,
                    categoriaNombre = categoriaNombre,
                    activo = activo
                )
            }
        )
    }
}

/* ---------- Cards y diálogos ---------- */

@Composable
private fun ProductoCard(
    p: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(p.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text("Código: ${p.codigo}", style = MaterialTheme.typography.bodySmall)
                Text("Categoría: ${p.categoriaNombre}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = {},
                        label = { Text("$${p.precio}") },
                        leadingIcon = { Icon(Icons.Default.Sell, null) }
                    )
                    Spacer(Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(if (p.activo) "Activo" else "Inactivo") },
                        icon = {
                            Icon(
                                if (p.activo) Icons.Default.CheckCircle else Icons.Default.Block,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoDialog(
    inicial: Producto,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int, String, Boolean) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue(inicial.nombre)) }
    var descripcion by remember { mutableStateOf(TextFieldValue(inicial.descripcion)) }
    var precio by remember { mutableStateOf(TextFieldValue(inicial.precio.toString())) }
    var categoriaId by remember { mutableStateOf(TextFieldValue(inicial.categoriaId.toString())) }
    var categoriaNombre by remember { mutableStateOf(TextFieldValue(inicial.categoriaNombre)) }
    var activo by remember { mutableStateOf(inicial.activo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val p = precio.text.toIntOrNull() ?: 0
                val cId = categoriaId.text.toIntOrNull() ?: 0
                onSave(nombre.text, descripcion.text, p, cId, categoriaNombre.text, activo)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(if (inicial.nombre.isBlank()) "Nuevo producto" else "Editar producto") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (entero)") }, singleLine = true)
                OutlinedTextField(value = categoriaId, onValueChange = { categoriaId = it }, label = { Text("Categoría Id") }, singleLine = true)
                OutlinedTextField(value = categoriaNombre, onValueChange = { categoriaNombre = it }, label = { Text("Categoría nombre") }, singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = activo, onCheckedChange = { activo = it })
                    Spacer(Modifier.width(6.dp))
                    Text("Activo")
                }
            }
        }
    )
}

@Composable
private fun EmptyState(
    isFiltered: Boolean,
    onClearFilters: () -> Unit,
    onCreate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Inventory2,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isFiltered) "No hay resultados" else "Aún no tienes productos",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isFiltered) {
                OutlinedButton(onClick = onClearFilters) {
                    Icon(Icons.Default.FilterAltOff, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Limpiar filtros")
                }
            }
            Button(onClick = onCreate) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Crear producto")
            }
        }
    }
}
