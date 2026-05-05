package mx.edu.itson.pompompurincafe.model

import com.google.firebase.database.Exclude

/**
 * Representa un producto dentro de una orden.
 */
data class ItemOrden(

    /** Identificador único del item */
    var itemId: String = "",

    /** ID del platillo asociado */
    var platilloId: Int = 0,

    /** Nombre del platillo */
    var nombre: String = "",

    /** Precio unitario del producto */
    var precioUnitario: Double = 0.0,

    /** Cantidad solicitada */
    var cantidad: Int = 0,

    /** Subtotal calculado (precio * cantidad) */
    var subtotal: Double = 0.0,

    /** Nombre del cliente que pidió el producto */
    var cliente: String = "",

    /** Indica si el producto ya fue pagado */
    var pagado: Boolean = false
) {

    /**
     * Propiedad calculada en memoria (no se guarda en Firebase).
     */
    @get:Exclude
    var platillo: Platillo
        get() = Platillo(
            id = platilloId,
            nombre = nombre,
            precio = precioUnitario
        )
        set(value) {
            platilloId = value.id
            nombre = value.nombre
            precioUnitario = value.precio
        }

    companion object {

        /**
         * Crea un ItemOrden a partir de un Platillo.
         */
        fun desde(platillo: Platillo, cantidad: Int, cliente: String): ItemOrden {
            val sub = platillo.precio * cantidad
            return ItemOrden(
                platilloId = platillo.id,
                nombre = platillo.nombre,
                precioUnitario = platillo.precio,
                cantidad = cantidad,
                subtotal = sub,
                cliente = cliente
            )
        }
    }
}