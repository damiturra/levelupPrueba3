package com.example.levelupgamer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoriaId: Int,
    val categoriaNombre: String,
    val stock: Int = 100,
    val imagenUrl: String = "",
    val fabricante: String = "",
    val calificacion: Float = 0f,
    val vendedorId: Long = 0,
    val activo: Boolean = true
) {
    companion object {
        fun obtenerProductosDefault(): List<Producto> {
            return listOf(
                Producto(
                    codigo = "JM001",
                    nombre = "Catan",
                    descripcion = "Un clásico juego de estrategia donde los jugadores compiten por colonizar y expandirse en la isla de Catan. Ideal para 3-4 jugadores y perfecto para noches de juego en familia o con amigos.",
                    precio = 29990,
                    categoriaId = 1,
                    categoriaNombre = "Juegos de Mesa",
                    fabricante = "Catan Studio",
                    calificacion = 4.5f
                ),
                Producto(
                    codigo = "JM002",
                    nombre = "Carcassonne",
                    descripcion = "Un juego de colocación de fichas donde los jugadores construyen el paisaje alrededor de la fortaleza medieval de Carcassonne. Ideal para 2-5 jugadores y fácil de aprender.",
                    precio = 24990,
                    categoriaId = 1,
                    categoriaNombre = "Juegos de Mesa",
                    fabricante = "Z-Man Games",
                    calificacion = 4.3f
                ),
                Producto(
                    codigo = "AC001",
                    nombre = "Controlador Inalámbrico Xbox Series X",
                    descripcion = "Ofrece una experiencia de juego cómoda con botones mapeables y una respuesta táctil mejorada. Compatible con consolas Xbox y PC.",
                    precio = 59990,
                    categoriaId = 2,
                    categoriaNombre = "Accesorios",
                    fabricante = "Microsoft",
                    calificacion = 4.7f
                ),
                Producto(
                    codigo = "AC002",
                    nombre = "Auriculares Gamer HyperX Cloud II",
                    descripcion = "Proporcionan un sonido envolvente de calidad con un micrófono desmontable y almohadillas de espuma viscoelástica para mayor comodidad durante largas sesiones de juego.",
                    precio = 79990,
                    categoriaId = 2,
                    categoriaNombre = "Accesorios",
                    fabricante = "HyperX",
                    calificacion = 4.8f
                ),
                Producto(
                    codigo = "CO001",
                    nombre = "PlayStation 5",
                    descripcion = "La consola de última generación de Sony, que ofrece gráficos impresionantes y tiempos de carga ultrarrápidos para una experiencia de juego inmersiva.",
                    precio = 549990,
                    categoriaId = 3,
                    categoriaNombre = "Consolas",
                    fabricante = "Sony",
                    calificacion = 4.9f
                ),
                Producto(
                    codigo = "CG001",
                    nombre = "PC Gamer ASUS ROG Strix",
                    descripcion = "Un potente equipo diseñado para los gamers más exigentes, equipado con los últimos componentes para ofrecer un rendimiento excepcional en cualquier juego.",
                    precio = 1299990,
                    categoriaId = 4,
                    categoriaNombre = "Computadores Gamers",
                    fabricante = "ASUS",
                    calificacion = 4.8f
                ),
                Producto(
                    codigo = "SG001",
                    nombre = "Silla Gamer Secretlab Titan",
                    descripcion = "Diseñada para el máximo confort, esta silla ofrece un soporte ergonómico y personalización ajustable para sesiones de juego prolongadas.",
                    precio = 349990,
                    categoriaId = 5,
                    categoriaNombre = "Sillas Gamers",
                    fabricante = "Secretlab",
                    calificacion = 4.7f
                ),
                Producto(
                    codigo = "MS001",
                    nombre = "Mouse Gamer Logitech G502 HERO",
                    descripcion = "Con sensor de alta precisión y botones personalizables, este mouse es ideal para gamers que buscan un control preciso y personalización.",
                    precio = 49990,
                    categoriaId = 6,
                    categoriaNombre = "Mouse",
                    fabricante = "Logitech",
                    calificacion = 4.6f
                ),
                Producto(
                    codigo = "MP001",
                    nombre = "Mousepad Razer Goliathus Extended Chroma",
                    descripcion = "Ofrece un área de juego amplia con iluminación RGB personalizable, asegurando una superficie suave y uniforme para el movimiento del mouse.",
                    precio = 29990,
                    categoriaId = 7,
                    categoriaNombre = "Mousepad",
                    fabricante = "Razer",
                    calificacion = 4.4f
                ),
                Producto(
                    codigo = "PP001",
                    nombre = "Polera Gamer Personalizada 'Level-Up'",
                    descripcion = "Una camiseta cómoda y estilizada, con la posibilidad de personalizarla con tu gamer tag o diseño favorito.",
                    precio = 14990,
                    categoriaId = 8,
                    categoriaNombre = "Poleras Personalizadas",
                    fabricante = "Level-Up Gamer",
                    calificacion = 4.2f
                )
            )
        }
    }
}
