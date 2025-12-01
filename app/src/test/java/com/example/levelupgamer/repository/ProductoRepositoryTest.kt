package com.example.levelupgamer.repository

import com.example.levelupgamer.data.dao.CategoriaDao
import com.example.levelupgamer.data.dao.ProductoDao
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.repository.ProductoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class ProductoRepositoryTest : StringSpec({

    // TEST 1: obtenerTodosLosProductos() debe emitir la lista simulada
    "obtenerTodosLosProductos debe retornar lista de productos simulada" {
        // Arrange
        val fakeProducts = listOf(
            Producto(
                codigo = "P001",
                nombre = "Teclado gamer",
                descripcion = "Teclado mecánico RGB",
                precio = 29990,
                categoriaId = 1,
                categoriaNombre = "Periféricos",
                vendedorId = 1L,
                calificacion = 5.0f
            ),
            Producto(
                codigo = "P002",
                nombre = "Mouse gamer",
                descripcion = "Mouse óptico 7200 DPI",
                precio = 19990,
                categoriaId = 1,
                categoriaNombre = "Periféricos",
                vendedorId = 1L,
                calificacion = 4.5f
            )
        )

        val productoDao = mockk<ProductoDao>()
        val categoriaDao = mockk<CategoriaDao>(relaxed = true)

        // El DAO devuelve un Flow con la lista fake
        every { productoDao.obtenerTodosLosProductos() } returns flowOf(fakeProducts)

        val repo = ProductoRepository(productoDao, categoriaDao)

        // Act + Assert
        runTest {
            val emisiones = mutableListOf<List<Producto>>()

            repo.obtenerTodosLosProductos().collect { lista ->
                emisiones.add(lista)
            }

            emisiones.size shouldBe 1
            emisiones[0] shouldContainExactly fakeProducts
        }
    }

    // TEST 2: obtenerProductoPorCodigo() debe delegar en el DAO y devolver el producto correcto
    "obtenerProductoPorCodigo debe retornar el producto correcto desde el dao" {
        // Arrange
        val fakeProducto = Producto(
            codigo = "P001",
            nombre = "Teclado gamer",
            descripcion = "Teclado mecánico RGB",
            precio = 29990,
            categoriaId = 1,
            categoriaNombre = "Periféricos",
            vendedorId = 1L,
            calificacion = 5.0f
        )

        val productoDao = mockk<ProductoDao>()
        val categoriaDao = mockk<CategoriaDao>(relaxed = true)

        coEvery { productoDao.obtenerProductoPorCodigo("P001") } returns fakeProducto

        val repo = ProductoRepository(productoDao, categoriaDao)

        // Act + Assert
        runTest {
            val resultado = repo.obtenerProductoPorCodigo("P001")
            resultado shouldBe fakeProducto
        }
    }
})
