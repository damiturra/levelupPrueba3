// ui/product/ProductImages.kt
package com.example.levelupgamer.ui.product

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.levelupgamer.R

@Composable
@DrawableRes
fun productImageRes(codigo: String): Int {
    val ctx = LocalContext.current
    val key = codigo.trim().lowercase()   // ← convierte "AC001" -> "ac001"
    val resId = remember(key) {
        ctx.resources.getIdentifier(key, "drawable", ctx.packageName)
    }
    return if (resId != 0) resId else R.drawable.loguito // fallback si no existe
}
