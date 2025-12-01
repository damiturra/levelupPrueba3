package com.example.levelupgamer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.levelupgamer.data.dao.CarritoDao
import com.example.levelupgamer.data.dao.CategoriaDao
import com.example.levelupgamer.data.dao.ProductoDao
import com.example.levelupgamer.data.dao.VendedorDao
import com.example.levelupgamer.data.model.Categoria
import com.example.levelupgamer.data.model.ItemCarrito
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.data.model.VendedorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Producto::class,
        Categoria::class,
        ItemCarrito::class,
        VendedorEntity::class
    ],
    version = 3,            // 👈 sube a 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun carritoDao(): CarritoDao
    abstract fun vendedorDao(): VendedorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 1 -> 2: agrega vendedorId y crea índice único en codigo
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE productos ADD COLUMN vendedorId INTEGER NOT NULL DEFAULT 0")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_productos_codigo ON productos(codigo)")
            }
        }

        // 2 -> 3: agrega 'activo' (INTEGER 0/1)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE productos ADD COLUMN activo INTEGER NOT NULL DEFAULT 1")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelupgamer_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    // .fallbackToDestructiveMigration() // <- solo si quieres wipe en desarrollo
                    .addCallback(SeedIfEmptyCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class SeedIfEmptyCallback : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        val categoriaDao = database.categoriaDao()
                        val productoDao = database.productoDao()
                        val vendedorDao = database.vendedorDao()

                        if (vendedorDao.contarVendedores() == 0) {
                            vendedorDao.insert(
                                com.example.levelupgamer.data.model.VendedorEntity(
                                    nombre = "Vendedor Demo",
                                    email = "demo@levelupgamer.cl",
                                    activo = true
                                )
                            )
                        }
                        if (categoriaDao.contarCategorias() == 0) {
                            categoriaDao.insertarCategorias(
                                com.example.levelupgamer.data.model.Categoria.obtenerCategoriasDefault()
                            )
                        }
                        if (productoDao.contarProductos() == 0) {
                            // ⬇️ aquí estaba el problema
                            productoDao.upsertAll(
                                com.example.levelupgamer.data.model.Producto.obtenerProductosDefault()
                            )
                        }
                    }
                }
            }
        }
    }
}
