package com.example.levelupgamer.data.repository

import com.example.levelupgamer.data.dao.CarritoDao
import com.example.levelupgamer.data.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

class CarritoRepository(
    private val dao: CarritoDao
) {

    /* ========= Observables ========= */

    /** Lista completa del carrito del usuario (para la UI de la lista). */
    fun observeItems(userId: Int): Flow<List<ItemCarrito>> =
        dao.observeByUser(userId)

    /** Cantidad total de ítems (para badges/toolbar). */
    fun observeCount(userId: Int): Flow<Int?> =
        dao.observeCount(userId)

    /** Subtotal (suma de precio*cantidad) para cálculos de resumen. */
    fun observeSubtotal(userId: Int): Flow<Int?> =
        dao.observeSubtotal(userId)

    /* ========= Consultas puntuales ========= */

    /** Busca un item específico por código de producto. */
    suspend fun findItem(userId: Int, productoCodigo: String): ItemCarrito? =
        dao.findItem(userId, productoCodigo)

    /* ========= Escritura ========= */

    /** Inserta/actualiza un ítem del carrito. */
    suspend fun upsert(item: ItemCarrito) =
        dao.upsert(item)

    /** Elimina un ítem concreto. */
    suspend fun delete(item: ItemCarrito) =
        dao.delete(item)

    /** Limpia todo el carrito del usuario. */
    suspend fun clear(userId: Int) =
        dao.clearByUser(userId)
}
