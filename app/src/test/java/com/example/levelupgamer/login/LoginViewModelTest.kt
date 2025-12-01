package com.example.levelupgamer.login

import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role
import com.example.levelupgamer.viewmodel.LoginViewModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @BeforeEach
    fun setup() {
        // Dejamos la sesión limpia antes de cada prueba
        SessionManager.clear()
        viewModel = LoginViewModel()
    }

    // TEST 1: Campos vacíos → debe mostrar error y NO llamar al callback
    @Test
    fun `hacerLogin con campos vacios debe mostrar error y no llamar callback`() {
        var callbackLlamado = false

        viewModel.hacerLogin { _, _, _ ->
            callbackLlamado = true
        }

        val state = viewModel.uiState

        assertEquals(
            "Completa todos los campos",
            state.error,
            "Debe mostrar mensaje de error cuando email o password están vacíos"
        )
        assertFalse(callbackLlamado, "El callback de éxito no debe llamarse con campos vacíos")

        // Sesión sigue vacía
        assertNull(SessionManager.currentUserId)
        assertNull(SessionManager.currentUserName)
    }

    // TEST 2: Credenciales válidas → actualiza SessionManager y llama callback
    @Test
    fun `hacerLogin con credenciales validas debe actualizar sesion y llamar callback`() {
        // Credenciales válidas que tienes en UsuariosManager
        viewModel.onEmailChange("damian@duoc.cl")
        viewModel.onPasswordChange("123456")

        var callbackLlamado = false
        var nombreCallback: String? = null
        var esDuocCallback: Boolean? = null
        var rolCallback: Role? = null

        viewModel.hacerLogin { nombre, esDuoc, rol ->
            callbackLlamado = true
            nombreCallback = nombre
            esDuocCallback = esDuoc
            rolCallback = rol
        }

        val state = viewModel.uiState

        // No debería haber error
        assertNull(state.error)
        // Callback llamado
        assertTrue(callbackLlamado)
        assertEquals("Damian", nombreCallback)
        assertEquals(true, esDuocCallback)
        assertEquals(Role.USER, rolCallback)

        // SessionManager actualizado
        assertEquals(1, SessionManager.currentUserId)
        assertEquals("Damian", SessionManager.currentUserName)
        assertTrue(SessionManager.esDuoc)
        assertEquals(Role.USER, SessionManager.role)
    }
}

