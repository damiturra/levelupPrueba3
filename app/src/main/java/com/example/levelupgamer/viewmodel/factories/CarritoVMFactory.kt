package com.example.levelupgamer.viewmodel.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.repository.CarritoRepository
import com.example.levelupgamer.view.CarritoViewModel

/**
 * Factory para CarritoViewModel que NO depende de un singleton Application.
 * Debes pasar el Application al crearla.
 *
 * Ejemplo de uso en Compose:
 *   val app = (LocalContext.current.applicationContext as Application)
 *   val carritoVM: CarritoViewModel = viewModel(factory = CarritoVMFactory(app))
 */
class CarritoVMFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getDatabase(application)
        val repo = CarritoRepository(db.carritoDao())
        return CarritoViewModel(repo) as T
    }
}
