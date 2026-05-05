package mx.edu.itson.pompompurincafe.model

/**
 * Representa una orden dentro del sistema.
 */
data class Orden(

    /** Identificador único de la orden */
    var id: String = "",

    /** Número de mesa asociada */
    var mesa: Int = 0,

    /** Tipo de cuenta ("mesa" o "individual") */
    var tipoCuenta: String = "",

    /** Fecha de creación en milisegundos */
    var fecha: Long = 0,

    /** Número de personas asociadas a la orden */
    var numPersonas: Int? = null,

    /** Subtotal de la orden */
    var subtotal: Double = 0.0,

    /** Propina aplicada a la orden */
    var propina: Double? = null,

    /** Total a pagar (subtotal + propina) */
    var total: Double = 0.0,

    /** Indica si la orden ha sido pagada completamente */
    var pagada: Boolean = false,

    /** Lista de productos incluidos en la orden */
    var productos: MutableList<ItemOrden> = mutableListOf(),

    /** Lista de comensales (solo para cuentas individuales) */
    var comensales: MutableList<Comensal>? = null
) {

    /**
     * Calcula los subtotales y el total de la orden.
     */
    fun calcularTotales() {
        productos.forEach { item ->
            item.subtotal = item.platillo.precio * item.cantidad
        }

        subtotal = productos.sumOf { it.subtotal }
        total = subtotal + (propina ?: 0.0)
    }

    /**
     * Actualiza el número de personas en función de los comensales.
     */
    fun actualizarNumPersonas() {
        if (tipoCuenta == "individual") {
            numPersonas = comensales?.size ?: numPersonas
        }
    }

    /**
     * Determina si la orden está completamente pagada.
     */
    fun estaPagadaCompletamente(): Boolean {
        return if (tipoCuenta == "individual") {
            comensales?.all { it.pagado } ?: pagada
        } else {
            pagada
        }
    }
}