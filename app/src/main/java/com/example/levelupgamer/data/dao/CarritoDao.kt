package com.example.levelupgamer.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.levelupgamer.data.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    /* ========= Observables por usuario ========= */

    /** Todos los ítems del carrito del usuario (para la lista). */
    @Query("SELECT * FROM carrito WHERE userId = :userId ORDER BY id ASC")
    fun observeByUser(userId: Int): Flow<List<ItemCarrito>>

    /** Cantidad total de ítems (para el badge). */
    @Query("SELECT SUM(cantidad) FROM carrito WHERE userId = :userId")
    fun observeCount(userId: Int): Flow<Int?>

    /** Subtotal = suma de (precio * cantidad) (para el resumen). */
    @Query("SELECT SUM(cantidad * productoPrecio) FROM carrito WHERE userId = :userId")
    fun observeSubtotal(userId: Int): Flow<Int?>

    /* ========= Consultas puntuales ========= */

    /** Ítem concreto por usuario y producto. */
    @Query("SELECT * FROM carrito WHERE userId = :userId AND productoCodigo = :productoCodigo LIMIT 1")
    suspend fun findItem(userId: Int, productoCodigo: String): ItemCarrito?

    /* ========= Escritura ========= */

    /** Inserta/actualiza un ítem del carrito. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ItemCarrito)

    /** Elimina un ítem concreto. */
    @Delete
    suspend fun delete(item: ItemCarrito)

    /** Limpia todo el carrito del usuario. */
    @Query("DELETE FROM carrito WHERE userId = :userId")
    suspend fun clearByUser(userId: Int)
}
