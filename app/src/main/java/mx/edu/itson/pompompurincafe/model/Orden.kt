package mx.edu.itson.pompompurincafe.model

data class Orden(
    var id: String = "",
    var mesa: Int = 0,
    var numPersonas: Int = 0,
    var tipo: String = "",
    var productos: MutableList<ItemOrden> = mutableListOf(),
    var pagada: Boolean = false
) {
    val total: Double
        get() = productos.sumOf { it.subtotal }

    val cantidadTotalProductos: Int
        get() = productos.sumOf { it.cantidad }

    fun actualizarNumPersonas() {
        if (tipo == "PERSONA") {
            numPersonas = productos.map { it.cliente }.distinct().size
        } else {
            numPersonas = 1
        }
    }
}
