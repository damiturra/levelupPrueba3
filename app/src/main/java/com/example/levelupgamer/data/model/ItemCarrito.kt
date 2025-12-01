package com.example.levelupgamer.data.model

import androidx.room.*

@Entity(
    tableName = "carrito",
    indices = [Index("userId"), Index("productoCodigo")]
)
data class ItemCarrito(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    // 🔹 clave del usuario dueño del carrito (0 = invitado)
    @ColumnInfo(name = "userId") val userId: Int = 0,

    @ColumnInfo(name = "productoCodigo") val productoCodigo: String,
    @ColumnInfo(name = "productoNombre") val productoNombre: String,
    @ColumnInfo(name = "productoPrecio") val productoPrecio: Int,
    @ColumnInfo(name = "cantidad") val cantidad: Int = 1,

    // opcional, por si usas ordenamiento/limpieza
    @ColumnInfo(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)
