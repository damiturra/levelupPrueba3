package com.example.levelupgamer.data.session

import com.example.levelupgamer.data.user.Role

/**
 * Gestor de sesión en memoria.
 * - currentUserId/currentUserName: usuario autenticado (null si invitado)
 * - esDuoc: flag para descuentos/beneficios
 * - role: USER / VENDEDOR / ADMIN
 * - currentVendedorId: útil para panel de vendedor
 *
 * Helpers:
 * - safeUserId(): Int -> devuelve un ID seguro (-1 si no hay sesión)
 * - requireUserId(): Int -> lanza error si no hay sesión (para flujos que exigen login)
 */
object SessionManager {
    var currentUserId: Int? = null
    var currentUserName: String? = null
    var esDuoc: Boolean = false
    var role: Role = Role.USER

    // Para el panel de vendedor
    var currentVendedorId: Long? = null

    fun clear() {
        currentUserId = null
        currentUserName = null
        esDuoc = false
        role = Role.USER
        currentVendedorId = null
    }

    /** Devuelve un ID seguro para el carrito; -1 si no hay sesión (modo invitado). */
    fun safeUserId(): Int = currentUserId ?: -1

    /** Lanza error claro si no hay usuario logeado (útil si quieres exigir login). */
    fun requireUserId(): Int = currentUserId
        ?: error("No hay usuario logeado en SessionManager.currentUserId")
}
