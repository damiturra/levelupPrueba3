package com.example.levelupgamer.viewmodel

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * PRUEBAS UNITARIAS PARA PerfilViewModel
 *
 * Objetivo:
 *  - Probar el comportamiento de setNombreInicial y onNombreChange sobre el uiState.
 *
 * Escenario 1:
 *  - Estado inicial: nombre vacío ("").
 *  - Acción: setNombreInicial("Damian").
 *  - Resultado esperado: uiState.nombre = "Damian".
 *
 * Escenario 2:
 *  - Estado inicial: el usuario ya escribió un nombre ("Usuario Editado").
 *  - Acción: setNombreInicial("Damian").
 *  - Resultado esperado: uiState.nombre se mantiene en "Usuario Editado"
 *    (es decir, setNombreInicial NO debe sobreescribir un nombre ya seteado).
 */
class PerfilViewModelTest {

    private lateinit var viewModel: PerfilViewModel

    @BeforeEach
    fun setup() {
        viewModel = PerfilViewModel()
    }

    //----------------------------------------------------------------------------
    // TEST 1: setNombreInicial debe rellenar el nombre si está vacío
    //----------------------------------------------------------------------------
    @Test
    fun `setNombreInicial debe establecer el nombre cuando esta vacio`() {
        // Estado inicial del ViewModel
        val estadoInicial = viewModel.uiState
        assertEquals("", estadoInicial.nombre)

        // Act
        viewModel.setNombreInicial("Damian")

        // Assert
        val estadoFinal = viewModel.uiState
        assertEquals(
            "Damian",
            estadoFinal.nombre,
            "Cuando el nombre está vacío, setNombreInicial debe establecer el valor recibido"
        )
    }

    //----------------------------------------------------------------------------
    // TEST 2: setNombreInicial NO debe sobrescribir un nombre ya establecido
    //----------------------------------------------------------------------------
    @Test
    fun `setNombreInicial no debe cambiar un nombre ya establecido`() {
        // Arrange: simulamos que el usuario ya escribió un nombre en el perfil
        viewModel.onNombreChange("Usuario Editado")
        val antes = viewModel.uiState
        assertEquals("Usuario Editado", antes.nombre)

        // Act: llamamos nuevamente a setNombreInicial
        viewModel.setNombreInicial("Damian")

        // Assert: el nombre debe seguir siendo el que el usuario escribió
        val despues = viewModel.uiState
        assertEquals(
            "Usuario Editado",
            despues.nombre,
            "Si el nombre ya fue ingresado, setNombreInicial NO debe sobrescribirlo"
        )
    }
}
