package com.example.levelupgamer.ui.product

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.levelupgamer.R

object ImageUtils {
    /**
     * Devuelve el drawable que corresponde al código del producto.
     * Busca un recurso en res/drawable con el nombre del código en minúsculas (p.ej. "AC001" -> "ac001").
     * Si no existe, usa un placeholder (loguito).
     */
    @DrawableRes
    fun productImageRes(context: Context, codigo: String): Int {
        val name = codigo.trim().lowercase() // "AC001" -> "ac001"
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.loguito
    }
}
