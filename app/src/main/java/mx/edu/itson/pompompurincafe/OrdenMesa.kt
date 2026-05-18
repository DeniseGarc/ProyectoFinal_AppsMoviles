package mx.edu.itson.pompompurincafe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.ItemOrden
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow
import java.util.Locale

/**
 * Actividad que muestra el desglose y resumen de los platillos agregados a una mesa entera.
 * Permite ajustar cantidades, borrar elementos y confirmar el envío del pedido.
 */
class OrdenMesa : ComponentActivity() {

    /**
     * Inicializa la pantalla de resumen de orden.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                ResumenOrdenScreen()
            }
        }
    }
}

/**
 * Renglón individual para cada platillo dentro de la lista de revisión.
 * Incluye flechas para modificar la cantidad directamente o un botón para eliminarlo.
 */
@Composable
fun OrdenListaPlatillos(
    item: ItemOrden,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, brown)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Panel de control numérico para subir, bajar o remover el producto
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Aumentar",
                        tint = brown,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onUpdateQuantity(item.cantidad + 1) }
                    )
                    Text(
                        text = "${item.cantidad}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = brown
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Disminuir",
                        tint = brown,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                if (item.cantidad > 1) {
                                    onUpdateQuantity(item.cantidad - 1)
                                } else {
                                    onRemove()
                                }
                            }
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.platillo.nombre,
                    fontSize = 16.sp,
                    color = brown,
                    modifier = Modifier.width(200.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = brown,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

/**
 * Encabezado de información que muestra de forma destacada el número de mesa y el costo total.
 */
@Composable
fun InfoMesa(orden: Orden) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Mesa ${orden.mesa}",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = brown
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Total", fontSize = 16.sp, color = brown)
            Text(
                "$${String.format(Locale.US, "%.2f", orden.total)}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = brown
            )
        }
    }
}

/**
 * Componente que estructura la lista de platillos ordenados y las opciones de edición.
 * Muestra el estado de la comanda en tiempo real y gestiona las actualizaciones en Firebase.
 */
@Composable
fun ResumenOrdenScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    // Obtiene los datos clave pasados por el Intent desde la pantalla previa
    val ordenId = activity?.intent?.getStringExtra("ordenId") ?: ""
    val tipoOrden = activity?.intent?.getStringExtra("tipoOrden") ?: "MESA"

    // Estado local para almacenar la información de la orden activa
    var ordenActual by remember { mutableStateOf<Orden?>(null) }

    // Escucha y actualiza los datos de la orden en tiempo real desde Firebase
    LaunchedEffect(ordenId) {
        if (ordenId.isNotEmpty()) {
            FirebaseManager.obtenerOrden(ordenId) { ordenActual = it }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Fondo()

        Column(modifier = Modifier.fillMaxSize()) {
            HeaderBanner()

            ordenActual?.let { orden ->
                InfoMesa(orden)

                Row(
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(brown, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${orden.mesa}",
                            color = white,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Mesa",
                        color = brown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón para regresar al menú y seguir agregando más cosas
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(yellow, RoundedCornerShape(4.dp))
                            .clickable {
                                val intent = Intent(context, Menu::class.java)
                                intent.putExtra("tipoOrden", tipoOrden)
                                intent.putExtra("ordenId", ordenId)
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Edit, null, tint = brown, modifier = Modifier.size(18.dp))
                    }
                }

                // Listado en columna que renderiza todos los productos agregados
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(orden.productos) { item ->
                        OrdenListaPlatillos(
                            item = item,
                            onUpdateQuantity = { nuevaCantidad ->
                                // Busca el producto seleccionado y actualiza su cantidad en Firebase
                                val nuevaLista = orden.productos.map {
                                    if (it == item) it.copy(cantidad = nuevaCantidad) else it
                                }.toMutableList()
                                val ordenActualizada = orden.copy(productos = nuevaLista)
                                FirebaseManager.guardarOrden(ordenActualizada, {}, {})
                            },
                            onRemove = {
                                // Elimina por completo el producto seleccionado de la lista
                                val nuevaLista = orden.productos.toMutableList()
                                nuevaLista.remove(item)
                                val ordenActualizada = orden.copy(productos = nuevaLista)
                                FirebaseManager.guardarOrden(ordenActualizada, {}, {})
                            }
                        )
                    }
                }
            }
        }

        // Botón principal inferior para concluir el pedido y regresar a la pantalla de mesas
        Button(
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .height(65.dp)
                .fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = yellow),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mandar a cocina",
                    color = brown,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = brown,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Vista previa del resumen de orden.
 */
@Preview(showBackground = true)
@Composable
fun ResumenOrdenScreenPreview() {
    PompompurinCafeTheme {
        ResumenOrdenScreen()
    }
}