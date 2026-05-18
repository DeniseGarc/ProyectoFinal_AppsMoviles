package mx.edu.itson.pompompurincafe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.DataSource
import mx.edu.itson.pompompurincafe.model.ItemOrden
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.model.Platillo
import mx.edu.itson.pompompurincafe.ui.theme.CustomFontFamily
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.lightyellow
import mx.edu.itson.pompompurincafe.ui.theme.lightyellow2
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow
import java.util.Locale

/**
 * Actividad que muestra el catálogo de alimentos y bebidas disponibles en la cafetería.
 * Permite a los meseros seleccionar productos y asignarlos a una orden específica.
 */
class Menu : ComponentActivity() {

    /**
     * Inicializa la pantalla de menú.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                MenuScreen()
            }
        }
    }
}

/**
 * Barra inferior que muestra el total acumulado de la orden actual.
 * Incluye el botón para avanzar a la pantalla de revisión de la comanda.
 */
@Composable
fun Resumen(cantidadPlatillos: Int, total: Double, propietario: String, ordenId: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Obtiene el tipo de orden (por mesa o por persona) enviado desde la pantalla anterior
    val tipoOrden = activity?.intent?.getStringExtra("tipoOrden")

    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total", color = brown, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(20.dp))
                Text("$cantidadPlatillos platillos", fontSize = 16.sp, color = brown)
            }
            Text("$${String.format(Locale.US, "%.2f", total)}", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = brown)
            Text("Orden de: $propietario", fontSize = 16.sp, color = brown)
        }
        Spacer(modifier = Modifier.weight(1f))

        // Redirige a la pantalla de revisión correspondiente según el tipo de cuenta
        Button(
            onClick = {
                val intent = if (tipoOrden == "mesa") Intent(context, OrdenMesa::class.java) else Intent(context, OrdenPersona::class.java)
                intent.putExtra("tipoOrden", tipoOrden)
                intent.putExtra("ordenId", ordenId)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = brown),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(60.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Revisar\nOrden", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, fontFamily = CustomFontFamily, color = white)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = white)
            }
        }
    }
}

/**
 * Tarjeta individual para cada platillo o bebida del menú.
 * Contiene la foto, precio, descripción y los controles para modificar la cantidad.
 */
@Composable
fun PlatilloCard(platillo: Platillo, ordenId: String, clienteNombre: String, mesa: Int) {
    val context = LocalContext.current

    // Estado local para controlar cuántas piezas de este platillo se van a agregar
    var cantidad by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, brown)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                Image(
                    painter = painterResource(platillo.imagen),
                    contentDescription = platillo.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(platillo.nombre, fontWeight = FontWeight.SemiBold, color = brown, modifier = Modifier.weight(1f))
                    Text("$${platillo.precio}", fontWeight = FontWeight.SemiBold, color = brown)
                }
                Text(platillo.descripcion, fontSize = 12.sp, color = brown)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Controles para aumentar o disminuir la cantidad del producto
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(lightyellow2, RoundedCornerShape(10.dp))
                            .padding(4.dp)
                    ) {
                        Button(
                            onClick = { if (cantidad > 0) cantidad-- },
                            colors = ButtonDefaults.buttonColors(containerColor = lightyellow),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("-", fontSize = 20.sp, color = brown)
                        }
                        Text("$cantidad", modifier = Modifier.padding(horizontal = 16.dp), color = brown)
                        Button(
                            onClick = { cantidad++ },
                            colors = ButtonDefaults.buttonColors(containerColor = lightyellow),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+", fontSize = 20.sp, color = brown)
                        }
                    }

                    // Envía el producto seleccionado a la orden en Firebase
                    Button(
                        onClick = {
                            if (ordenId.isNotEmpty() && cantidad > 0) {
                                FirebaseManager.agregarProductoAOrden(
                                    ordenId,
                                    ItemOrden.desde(platillo, cantidad, clienteNombre)
                                )

                                // Personaliza la alerta dependiendo de si la orden es de alguien o de la mesa entera
                                val mensaje = if (clienteNombre.isNotEmpty()) {
                                    "¡Agregado para $clienteNombre! \uD83C\uDF6E"
                                } else {
                                    "¡Agregado a la mesa $mesa! \uD83C\uDF6E"
                                }
                                
                                cantidad = 0
                                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                            } else if (ordenId.isEmpty()) {
                                Toast.makeText(context, "Error: No hay orden activa", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = brown)
                    ) {
                        Text("Agregar", color = yellow)
                    }
                }
            }
        }
    }
}

/**
 * Estructura principal de la interfaz del menú.
 * Junta el banner, la lista de platillos y la sección del resumen inferior.
 */
@Composable
fun MenuScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    // Recupera los datos de la orden que fueron enviados desde la pantalla anterior
    val ordenId = activity?.intent?.getStringExtra("ordenId") ?: ""
    val clienteNombre = activity?.intent?.getStringExtra("clienteNombre") ?: ""

    // Estado para guardar y actualizar la información de la orden activa
    var ordenActual by remember { mutableStateOf<Orden?>(null) }

    // Trae los datos actualizados de la orden desde Firebase cada vez que cambia el ID
    LaunchedEffect(ordenId) {
        if (ordenId.isNotEmpty()) {
            FirebaseManager.obtenerOrden(ordenId) { ordenActual = it }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Fondo()

        Column(modifier = Modifier.fillMaxSize()) {
            HeaderBanner()

            // Despliega la lista completa de platillos en un contenedor vertical optimizado
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(DataSource.menuCompleto) {
                    PlatilloCard(it, ordenId, clienteNombre, ordenActual?.mesa ?: 0)
                }
            }

            // Si hay una orden activa, muestra la tarjeta de resumen en la parte inferior
            ordenActual?.let { orden ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = yellow),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    Resumen(
                        cantidadPlatillos = orden.productos.size,
                        total = orden.total,
                        propietario = clienteNombre.ifEmpty { "Mesa ${orden.mesa}" },
                        ordenId = ordenId
                    )
                }
            }
        }
    }
}

/**
 * Vista previa del menú.
 */
@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    PompompurinCafeTheme {
        MenuScreen()
    }
}