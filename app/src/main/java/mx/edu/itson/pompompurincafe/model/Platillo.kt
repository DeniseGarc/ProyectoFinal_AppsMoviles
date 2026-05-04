package mx.edu.itson.pompompurincafe.model

data class Platillo(
    val id: Int = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagen: Int = 0,
    val categoria: String = ""
)
