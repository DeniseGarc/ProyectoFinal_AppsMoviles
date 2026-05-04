package mx.edu.itson.pompompurincafe.model

data class ItemOrden(
    val platillo: Platillo = Platillo(),
    var cantidad: Int = 0,
    var cliente: String = ""
) {
    val subtotal: Double
        get() = platillo.precio * cantidad
}
