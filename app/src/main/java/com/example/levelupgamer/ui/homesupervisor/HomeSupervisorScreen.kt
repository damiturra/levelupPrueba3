package com.example.levelupgamer.ui.homesupervisor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.data.model.VendedorEntity
import com.example.levelupgamer.viewmodel.admin.AdminVendedoresViewModel
import androidx.compose.material.icons.filled.Logout
import com.example.levelupgamer.data.session.SessionManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVendedoresScreen(
    navController: NavController,
    vm: AdminVendedoresViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin — Vendedores (CRUD)") },
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

                    // 🔹 Nuevo vendedor
                    IconButton(onClick = { vm.nueva() }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo")
                    }
                }
            )

        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (ui.error != null) {
                AssistChip(onClick = { /* retry? */ }, label = { Text(ui.error ?: "") })
                Spacer(Modifier.height(8.dp))
            }
            if (ui.isLoading && ui.vendedores.isEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ui.vendedores, key = { it.id }) { v ->
                    VendedorRow(
                        v = v,
                        onEdit = { vm.editar(v) },
                        onDelete = { vm.eliminar(v.id) }
                    )
                }
            }
        }
    }

    // ✅ aquí estaba el typo (iui → ui) y además evitamos smart cast con let
    ui.editando?.let { ed: VendedorEntity ->
        VendedorDialog(
            inicial = ed,
            onDismiss = { vm.cancelar() },
            onSave = { nombre, email, activo ->
                vm.guardar(nombre, email, activo)
            }
        )
    }
}

@Composable
private fun VendedorRow(
    v: VendedorEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(v.nombre, style = MaterialTheme.typography.titleMedium)
                Text(v.email, style = MaterialTheme.typography.bodyMedium)
                Text(
                    if (v.activo) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VendedorDialog(
    inicial: VendedorEntity,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue(inicial.nombre)) }
    var email by remember { mutableStateOf(TextFieldValue(inicial.email)) }
    var activo by remember { mutableStateOf(inicial.activo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(nombre.text, email.text, activo) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(if (inicial.id == 0L) "Nuevo vendedor" else "Editar vendedor") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
                Row {
                    Checkbox(checked = activo, onCheckedChange = { activo = it })
                    Spacer(Modifier.width(6.dp))
                    Text("Activo")
                }
            }
        }
    )
}
