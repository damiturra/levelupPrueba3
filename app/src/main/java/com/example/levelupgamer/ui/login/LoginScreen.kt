package com.example.levelupgamer.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.R
import com.example.levelupgamer.data.user.Role
import com.example.levelupgamer.viewmodel.LoginViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    var showPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LEVEL-UP GAMER") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            var pressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (pressed) 1.2f else 1f,
                animationSpec = tween(durationMillis = 150),
                label = "logoPressScale"
            )

            Image(
                painter = painterResource(id = R.drawable.logo_levelup),
                contentDescription = "Logo Level-Up Gamer",
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clickable { pressed = !pressed },
                contentScale = ContentScale.Fit
            )

            Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)
            Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            if (uiState.error != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = uiState.error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // ————— Usuarios de prueba
            var showDemo by rememberSaveable { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Usuarios de prueba", style = MaterialTheme.typography.labelLarge)
                TextButton(onClick = { showDemo = !showDemo }) {
                    Text(if (showDemo) "Ocultar" else "Ver")
                }
            }

            AnimatedVisibility(visible = showDemo) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val lineStyle = MaterialTheme.typography.bodySmall
                        Text("• damian@duoc.cl / 123456  (USER)", style = lineStyle)
                        Text("• jean@duoc.cl / 123456    (USER)", style = lineStyle)
                        Text("• damian@vendedor.cl / 123456  (VENDEDOR)", style = lineStyle)
                        Text("• jean@vendedor.cl / 123456    (VENDEDOR)", style = lineStyle)
                        Text("• damian@admin.cl / 123456  (ADMIN)", style = lineStyle)
                        Text("• jean@admin.cl / 123456    (ADMIN)", style = lineStyle)
                    }
                }
            }
            // ————————————————————————————————————————————————

            Button(
                onClick = {
                    viewModel.hacerLogin { nombreUsuario, _, role ->
                        when (role) {
                            Role.USER -> navController.navigate("homeUsuario/$nombreUsuario") {
                                popUpTo("login") { inclusive = true }
                            }
                            Role.VENDEDOR -> {
                                // Demo: mapea el correo a un vendedorId Long
                                val vendedorId = when (uiState.email.trim().lowercase()) {
                                    "damian@vendedor.cl" -> 1L
                                    "jean@vendedor.cl"   -> 2L
                                    else                 -> 1L // fallback
                                }
                                navController.navigate("homeVendedor/$vendedorId") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            Role.ADMIN -> {
                                navController.navigate("homeSupervisor") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("INICIAR SESIÓN")
                }
            }


            // Enlace claro para registrarse
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta? ")
                TextButton(onClick = { navController.navigate("registro") }) {
                    Text("Regístrate", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}
