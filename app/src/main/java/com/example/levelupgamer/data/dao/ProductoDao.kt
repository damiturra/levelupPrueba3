package com.example.levelupgamer.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.levelupgamer.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    /* ====== Escritura ====== */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(producto: Producto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(productos: List<Producto>)

    @Update
    suspend fun actualizarProducto(producto: Producto)

    @Delete
    suspend fun eliminarProducto(producto: Producto)

    @Query("DELETE FROM productos WHERE codigo = :codigo")
    suspend fun deleteByCodigo(codigo: String)

    /* ====== Lectura general ====== */
    @Query("SELECT * FROM productos")
    fun obtenerTodosLosProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    suspend fun obtenerProductoPorCodigo(codigo: String): Producto?

    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    fun obtenerProductoPorCodigoFlow(codigo: String): Flow<Producto?>

    @Query("SELECT * FROM productos WHERE categoriaId = :categoriaId")
    fun obtenerProductosPorCategoria(categoriaId: Int): Flow<List<Producto>>

    @Query("""
        SELECT * FROM productos 
        WHERE nombre LIKE '%' || :busqueda || '%' 
           OR descripcion LIKE '%' || :busqueda || '%'
    """)
    fun buscarProductos(busqueda: String): Flow<List<Producto>>

    @Query("""
        SELECT * FROM productos 
        WHERE categoriaId = :categoriaId 
          AND ( nombre LIKE '%' || :busqueda || '%' 
             OR descripcion LIKE '%' || :busqueda || '%' )
    """)
    fun buscarProductosPorCategoria(categoriaId: Int, busqueda: String): Flow<List<Producto>>

    @Query("SELECT * FROM productos ORDER BY precio ASC")
    fun obtenerProductosOrdenadosPorPrecioAsc(): Flow<List<Producto>>

    @Query("SELECT * FROM productos ORDER BY precio DESC")
    fun obtenerProductosOrdenadosPorPrecioDesc(): Flow<List<Producto>>

    /* ✅ Requiere que tu entidad Producto tenga el campo `calificacion` (Float/Int) */
    @Query("SELECT * FROM productos ORDER BY calificacion DESC")
    fun obtenerProductosOrdenadosPorCalificacion(): Flow<List<Producto>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

    /* ====== Panel Vendedor ====== */
    @Query("SELECT * FROM productos WHERE vendedorId = :vendedorId ORDER BY nombre ASC")
    fun observeByVendedor(vendedorId: Long): Flow<List<Producto>>
}
