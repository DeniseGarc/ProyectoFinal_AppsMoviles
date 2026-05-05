package mx.edu.itson.pompompurincafe.model

/**
 * Representa a un comensal dentro de una orden.
 */
data class Comensal(

    /** Identificador único del comensal */
    var id: String = "",

    /** Nombre del comensal */
    var nombre: String = "",

    /** Subtotal de los productos consumidos */
    var subtotal: Double = 0.0,

    /** Propina asignada al comensal */
    var propina: Double = 0.0,

    /** Total a pagar (subtotal + propina) */
    var total: Double = 0.0,

    /** Indica si el comensal ya realizó su pago */
    var pagado: Boolean = false,

    /** Lista de IDs de los productos asociados al comensal */
    var productosIds: List<String> = emptyList()
)