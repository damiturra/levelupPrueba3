package com.example.levelupgamer.ui.home

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.ui.product.ImageUtils
import com.example.levelupgamer.view.CarritoViewModel
import com.example.levelupgamer.view.ProductoViewModel
import com.example.levelupgamer.viewmodel.factories.CarritoVMFactory
import com.example.levelupgamer.viewmodel.factories.ProductoVMFactory
import androidx.compose.material3.ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    username: String,
    esDuoc: Boolean,
    viewModel: ProductoViewModel = viewModel(factory = ProductoVMFactory())
) {
    // Productos desde Room
    val productos by viewModel.productos.collectAsState(initial = emptyList())

    // Carrito + contador (factory requiere Application)
    val app = LocalContext.current.applicationContext as Application
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoVMFactory(app))
    val count by carritoVM.cantidadItems.collectAsState(initial = 0)

    // Inicializa carrito con descuento DUOC (fallback userId = 0 si no hay sesión)
    LaunchedEffect(Unit) {
        val desc = if (SessionManager.esDuoc) 20 else 0
        carritoVM.inicializarCarrito(SessionManager.currentUserId ?: 0, desc)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("LEVEL-UP GAMER") },
                actions = {
                    IconButton(onClick = { navController.navigate("mapaSucursales") }) {
                        Icon(Icons.Filled.Map, contentDescription = "Mapa sucursales")
                    }

                    IconButton(onClick = { navController.navigate("carrito") }) {
                        BadgedBox(badge = { if (count > 0) Badge { Text(count.toString()) } }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                        }
                    }

                    IconButton(onClick = { navController.navigate("perfil/$username") }) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }

                    IconButton(onClick = {
                        SessionManager.clear()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar Sesión")
                    }

                    IconButton(onClick = { navController.navigate("scanner") }) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear QR")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header con bienvenida
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (esDuoc)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hola, $username 👋",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (esDuoc) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Verified,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Usuario Duoc - 20% de descuento",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { navController.navigate("mapaSucursales") }) {
                        Icon(Icons.Filled.Map, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver sucursales cercanas")
                    }
                }
            }

            // Grilla de productos
            val ctx = LocalContext.current
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(productos, key = { it.codigo }) { p ->
                    val imgRes = remember(p.codigo) { ImageUtils.productImageRes(ctx, p.codigo) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("detalle_producto/${p.codigo}") },
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Image(
                                painter = painterResource(id = imgRes),
                                contentDescription = p.nombre,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = p.nombre,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${"%,d".format(p.precio)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}
