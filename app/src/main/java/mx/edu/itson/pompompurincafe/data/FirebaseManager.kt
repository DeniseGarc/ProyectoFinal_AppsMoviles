package mx.edu.itson.pompompurincafe.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.edu.itson.pompompurincafe.model.ItemOrden
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.model.Platillo

object FirebaseManager {
    private val database = FirebaseDatabase.getInstance()
    private val ordenesRef = database.getReference("ordenes")
    private val menuRef = database.getReference("menu")

    /**
     * Obtiene los platillos del menú desde Firebase
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

    fun guardarOrden(orden: Orden, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val id = if (orden.id.isEmpty()) ordenesRef.push().key ?: "" else orden.id
        orden.id = id
        orden.actualizarNumPersonas()
        ordenesRef.child(id).setValue(orden)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error") }
    }

    fun obtenerOrden(id: String, callback: (Orden?) -> Unit) {
        ordenesRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.getValue(Orden::class.java))
            }
            override fun onCancelled(error: DatabaseError) { callback(null) }
        })
    }

    fun agregarProductoAOrden(ordenId: String, nuevoItem: ItemOrden) {
        ordenesRef.child(ordenId).get().addOnSuccessListener { snapshot ->
            val orden = snapshot.getValue(Orden::class.java)
            orden?.let {
                val itemExistente = it.productos.find { p -> 
                    p.platillo.id == nuevoItem.platillo.id && p.cliente == nuevoItem.cliente 
                }
                if (itemExistente != null) {
                    itemExistente.cantidad += nuevoItem.cantidad
                } else {
                    it.productos.add(nuevoItem)
                }
                it.actualizarNumPersonas()
                ordenesRef.child(ordenId).setValue(it)
            }
        }
    }

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
