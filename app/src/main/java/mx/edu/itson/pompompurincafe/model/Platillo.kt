package mx.edu.itson.pompompurincafe.model

import com.google.firebase.database.Exclude

/**
 * Representa un platillo del menú.
 */
data class Platillo(

    /** Identificador único del platillo */
    val id: Int = 0,

    /** Nombre del platillo */
    val nombre: String = "",

    /** Descripción del platillo */
    val descripcion: String = "",

    /** Precio del platillo */
    val precio: Double = 0.0,

    /** Recurso de imagen (no se guarda en Firebase) */
    @get:Exclude val imagen: Int = 0,

    /** Categoría del platillo (Bebidas, Postres, Platillos, etc.) */
    val categoria: String = ""
)