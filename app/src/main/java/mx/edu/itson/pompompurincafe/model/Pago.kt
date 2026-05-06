package mx.edu.itson.pompompurincafe.model

/**
 * Representa un registro de pago realizado en el sistema.
 */
data class Pago(
    /** Identificador único del pago */
    var id: String = "",
    
    /** ID de la orden a la que pertenece */
    var ordenId: String = "",
    
    /** ID del comensal (si el pago es individual) */
    var comensalId: String? = null,
    
    /** Monto base pagado */
    var monto: Double = 0.0,
    
    /** Propina incluida en este pago */
    var propina: Double = 0.0,
    
    /** Total cobrado (monto + propina) */
    var total: Double = 0.0,
    
    /** Fecha y hora del pago */
    var fecha: Long = 0,
    
    /** Correo del mesero que procesó el pago */
    var mesero: String = ""
)