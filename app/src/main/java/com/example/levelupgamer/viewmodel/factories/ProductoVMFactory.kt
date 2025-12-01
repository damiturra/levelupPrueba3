package com.example.levelupgamer.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupgamer.view.ProductoViewModel
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.repository.ProductoRepository
import android.app.Application

/**
 * Factory simple para instanciar ProductoViewModel con su repository.
 * Úsala así desde Compose:
 *   val vm: ProductoViewModel = viewModel(factory = ProductoVMFactory())
 */
class ProductoVMFactory(
    private val app: Application? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Obtén Application de dos formas: por parámetro o por reflexión del proceso actual.
        val application = app ?: run {
            // En Compose normalmente no pasamos app; Room se resuelve con getApplicationContext
            val clazz = Class.forName("android.app.ActivityThread")
            val method = clazz.getMethod("currentApplication")
            method.invoke(null) as Application
        }

        val db = AppDatabase.getDatabase(application)
        val repo = ProductoRepository(
            productoDao = db.productoDao(),
            categoriaDao = db.categoriaDao()
        )

        return ProductoViewModel(repo) as T
    }
}
