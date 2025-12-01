package com.example.levelupgamer.ui.product

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.view.ProductoViewModel
import com.example.levelupgamer.view.CarritoViewModel
import com.example.levelupgamer.viewmodel.factories.ProductoVMFactory
import com.example.levelupgamer.viewmodel.factories.CarritoVMFactory
import com.example.levelupgamer.data.session.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoCodigo: String,
    productoVM: ProductoViewModel = viewModel(factory = ProductoVMFactory()),
    // ⬇️ tu factory requiere Application, lo tomamos del contexto
    carritoVM: CarritoViewModel = run {
        val app = LocalContext.current.applicationContext as Application
        viewModel(factory = CarritoVMFactory(app))
    }
) {
    var cantidad by remember { mutableStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Producto desde Room (Flow -> State)
    val producto by productoVM
        .obtenerProductoPorCodigoFlow(productoCodigo)
        .collectAsState(initial = null)

    // Inicializa carrito con descuento segun DUOC
    LaunchedEffect(Unit) {
        val desc = if (SessionManager.esDuoc) 20 else 0
        carritoVM.inicializarCarrito(SessionManager.currentUserId ?: 0, desc)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen del producto
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                val ctx = LocalContext.current
                val imgRes = remember(producto!!.codigo) {
                    ImageUtils.productImageRes(ctx, producto!!.codigo)
                }
                Image(
                    painter = painterResource(id = imgRes),
                    contentDescription = producto!!.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Info
            Column(modifier = Modifier.padding(16.dp)) {
                Text(producto!!.nombre, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                Text(
                    "$${"%,d".format(producto!!.precio)}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                Text("Descripción", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(producto!!.descripcion, style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(24.dp))

                // Cantidad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cantidad:", style = MaterialTheme.typography.titleMedium)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilledIconButton(
                                onClick = { if (cantidad > 1) cantidad-- },
                                enabled = cantidad > 1
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Disminuir")
                            }
                            Text(cantidad.toString(), style = MaterialTheme.typography.headlineSmall)
                            FilledIconButton(onClick = { cantidad++ }) {
                                Icon(Icons.Filled.Add, contentDescription = "Aumentar")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Agregar al carrito
                Button(
                    onClick = {
                        producto?.let { p ->
                            carritoVM.agregarProducto(p, cantidad)
                            cantidad = 1
                            scope.launch { snackbarHostState.showSnackbar("Producto agregado al carrito") }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Filled.AddShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("AGREGAR AL CARRITO")
                }
            }
        }
    }
}
