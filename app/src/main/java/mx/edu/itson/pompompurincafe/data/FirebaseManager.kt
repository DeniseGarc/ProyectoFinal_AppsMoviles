package mx.edu.itson.pompompurincafe.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.edu.itson.pompompurincafe.model.Comensal
import mx.edu.itson.pompompurincafe.model.ItemOrden
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.model.Platillo

/**
 * Maneja todas las operaciones relacionadas con Firebase Realtime Database.
 */
object FirebaseManager {

    /** Instancia de la base de datos */
    private val database = FirebaseDatabase.getInstance()

    /** Referencia al nodo de órdenes */
    private val ordenesRef = database.getReference("ordenes")

    /** Referencia al nodo del menú */
    private val menuRef = database.getReference("menu")

    /**
     * Obtiene el menú completo desde Firebase.
     */
    fun obtenerMenu(callback: (List<Platillo>) -> Unit) {
        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Platillo>()
                for (postSnapshot in snapshot.children) {
                    val platillo = postSnapshot.getValue(Platillo::class.java)
                    if (platillo != null) lista.add(platillo)
                }
                callback(lista)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Guarda o actualiza una orden en Firebase.
     */
    fun guardarOrden(orden: Orden, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val id = if (orden.id.isEmpty()) ordenesRef.push().key ?: "" else orden.id
        orden.id = id

        if (orden.fecha == 0L) {
            orden.fecha = System.currentTimeMillis()
        }

        orden.productos.forEach { item ->
            if (item.itemId.isEmpty()) {
                item.itemId = ordenesRef.child(id).child("productos").push().key ?: ""
            }
        }

        orden.actualizarNumPersonas()
        orden.calcularTotales()

        if (orden.tipoCuenta == "individual") {
            orden.comensales = construirComensales(orden)
        }

        ordenesRef.child(id).setValue(orden)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error") }
    }

    /**
     * Construye la lista de comensales agrupando productos por cliente.
     */
    private fun construirComensales(orden: Orden): MutableList<Comensal> {
        val comensalesExistentes = orden.comensales?.associateBy { it.nombre } ?: emptyMap()
        val productosPorCliente = orden.productos.groupBy { it.cliente }

        return productosPorCliente.entries.mapIndexed { index, (clienteNombre, items) ->
            val existente = comensalesExistentes[clienteNombre]
            val subtotal = items.sumOf { it.subtotal }
            val propina = existente?.propina ?: 0.0

            Comensal(
                id = existente?.id ?: "comensal_${index + 1}",
                nombre = clienteNombre,
                subtotal = subtotal,
                propina = propina,
                total = subtotal + propina,
                pagado = existente?.pagado ?: false,
                productosIds = items.map { it.itemId }
            )
        }.toMutableList()
    }

    /**
     * Obtiene una orden específica por su ID.
     */
    fun obtenerOrden(id: String, callback: (Orden?) -> Unit) {
        ordenesRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.getValue(Orden::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    /**
     * Agrega un producto a una orden existente.
     */
    fun agregarProductoAOrden(ordenId: String, nuevoItem: ItemOrden) {
        ordenesRef.child(ordenId).get().addOnSuccessListener { snapshot ->
            val orden = snapshot.getValue(Orden::class.java)
            orden?.let {

                if (nuevoItem.itemId.isEmpty()) {
                    nuevoItem.itemId = ordenesRef.child(ordenId).child("productos").push().key ?: ""
                }

                val itemExistente = it.productos.find { p ->
                    p.platilloId == nuevoItem.platilloId &&
                            p.cliente == nuevoItem.cliente &&
                            !p.pagado
                }

                if (itemExistente != null) {
                    itemExistente.cantidad += nuevoItem.cantidad
                    itemExistente.subtotal = itemExistente.precioUnitario * itemExistente.cantidad
                } else {
                    it.productos.add(nuevoItem)
                }

                it.actualizarNumPersonas()
                it.calcularTotales()

                if (it.tipoCuenta == "individual") {
                    it.comensales = construirComensales(it)
                }

                ordenesRef.child(ordenId).setValue(it)
            }
        }
    }

    /**
     * Se suscribe a cambios en todas las órdenes.
     */
    fun suscribirseAOrdenes(onDataChange: (List<Orden>) -> Unit) {
        ordenesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Orden>()
                for (postSnapshot in snapshot.children) {
                    val orden = postSnapshot.getValue(Orden::class.java)
                    if (orden != null) lista.add(orden)
                }
                onDataChange(lista)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}