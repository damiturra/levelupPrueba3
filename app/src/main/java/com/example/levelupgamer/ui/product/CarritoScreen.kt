package com.example.levelupgamer.ui.product

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.view.CarritoViewModel
import com.example.levelupgamer.viewmodel.factories.CarritoVMFactory
import com.example.levelupgamer.data.session.SessionManager
import kotlin.math.abs

@Composable
fun CarritoScreen(
    navController: NavController
) {
    // ✅ Obtén Application y pásalo al Factory
    val app = LocalContext.current.applicationContext as Application
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoVMFactory(app))

    // Inicializa el carrito para el usuario logeado (usa fallback 0 si no hay sesión)
    LaunchedEffect(Unit) {
        val desc = if (SessionManager.esDuoc) 20 else 0
        carritoVM.inicializarCarrito(SessionManager.currentUserId ?: 0, desc)
    }

    val items by carritoVM.items.collectAsState()
    val resumen by carritoVM.resumen.collectAsState()

    Scaffold(
        topBar = {
            SmallTopBar(
                title = "Mi Carrito",
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (items.isNotEmpty() && resumen != null) {
                Surface(tonalElevation = 3.dp) {
                    Column(Modifier.padding(16.dp)) {
                        SummaryRow("Subtotal:", resumen!!.subtotal)
                        if (resumen!!.descuentoPorcentaje > 0 && resumen!!.descuentoMonto > 0) {
                            SummaryRow("Descuento (${resumen!!.descuentoPorcentaje}%)", -resumen!!.descuentoMonto)
                        }
                        SummaryRow("Base imponible:", resumen!!.baseImponible)
                        SummaryRow("IVA (${resumen!!.ivaPorcentaje}%):", resumen!!.ivaMonto)
                        Divider(Modifier.padding(vertical = 8.dp))
                        SummaryRow("Total:", resumen!!.total, highlight = true)

                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                carritoVM.finalizarCompra {
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Filled.ShoppingCartCheckout, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("FINALIZAR COMPRA")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            EmptyCartState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(item.productoNombre, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "$${"%,d".format(item.productoPrecio)} x ${item.cantidad}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Subtotal: $${"%,d".format(item.productoPrecio * item.cantidad)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledIconButton(
                                    onClick = { carritoVM.decrementarCantidad(item) },
                                    enabled = item.cantidad > 1
                                ) { Icon(Icons.Filled.Remove, contentDescription = "Menos") }

                                Spacer(Modifier.width(8.dp))
                                Text(
                                    item.cantidad.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(28.dp)
                                )
                                Spacer(Modifier.width(8.dp))

                                FilledIconButton(onClick = { carritoVM.incrementarCantidad(item) }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Más")
                                }

                                Spacer(Modifier.width(8.dp))

                                IconButton(onClick = { carritoVM.eliminarItem(item) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

/* ====== Helpers UI ====== */

@Composable
private fun SmallTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(shadowElevation = 4.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            } else Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            Row(content = actions)
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Int, highlight: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        val text = if (amount >= 0) {
            "$${"%,d".format(amount)}"
        } else {
            "-$${"%,d".format(kotlin.math.abs(amount))}"
        }
        Text(
            text = text,
            style = if (highlight) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
            color = if (highlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
private fun EmptyCartState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
