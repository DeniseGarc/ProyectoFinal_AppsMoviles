package mx.edu.itson.pompompurincafe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.Comensal
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.ui.theme.*
import java.util.Locale

/**
 * Activity encargada de mostrar la pantalla para pagar una orden por persona.
 */
class PagarOrdenPersona : ComponentActivity() {

    /**
     * Inicializa la actividad y carga el contenido Compose.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                PagarOrdenPersonaScreen()
            }
        }
    }
}

/**
 * Composable principal que maneja toda la lógica y UI para el pago por comensal.
 */
@Composable
fun PagarOrdenPersonaScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val ordenId = activity?.intent?.getStringExtra("ordenId") ?: ""

    var ordenActual by remember { mutableStateOf<Orden?>(null) }
    var comensalSeleccionado by remember { mutableStateOf<Comensal?>(null) }
    var montoPropina by remember { mutableDoubleStateOf(0.0) }
    var etiquetaPropina by remember { mutableStateOf("0%") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(ordenId) {
        if (ordenId.isNotEmpty()) {
            FirebaseManager.obtenerOrden(ordenId) {
                ordenActual = it
            }
        }
    }

    if (showDialog) {
        CustomTipDialog(
            subtotal = comensalSeleccionado?.subtotal ?: ordenActual?.total ?: 0.0,
            onConfirm = { monto, label ->
                montoPropina = monto
                etiquetaPropina = label
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.fondo_app),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderBanner()

            Spacer(modifier = Modifier.height(16.dp))

            ordenActual?.let { orden ->
                Text(
                    text = "Mesa ${orden.mesa}",
                    color = brown,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.fredoka_bold))
                )

                Spacer(modifier = Modifier.height(8.dp))

                val comensales = orden.comensales ?: emptyList()

                if (comensalSeleccionado == null) {
                    Text(
                        text = "Selecciona quién va a pagar:",
                        color = brown,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(comensales) { comensal ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable(enabled = !comensal.pagado) {
                                        comensalSeleccionado = comensal
                                        montoPropina = 0.0
                                        etiquetaPropina = "0%"
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (comensal.pagado) lightyellow2 else white
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (comensal.pagado) lightyellow else brown)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = comensal.nombre.ifEmpty { "General" },
                                            color = brown,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "Subtotal: $${String.format(Locale.US, "%.2f", comensal.subtotal)}",
                                            color = brown,
                                            fontSize = 14.sp
                                        )
                                    }
                                    if (comensal.pagado) {
                                        Text(
                                            text = "✓ Pagado",
                                            color = lightyellow,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    } else {
                                        Text(
                                            text = "Pagar →",
                                            color = brown,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val todosListos = comensales.all { it.pagado }
                    if (todosListos) {
                        Button(
                            onClick = {
                                val ordenPagada = orden.copy(pagada = true)
                                FirebaseManager.guardarOrden(ordenPagada, {
                                    Toast.makeText(context, "Mesa ${orden.mesa} liquidada", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                }, { error ->
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = brown),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(65.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            Text("Cerrar mesa", color = yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                } else {
                    val comensal = comensalSeleccionado!!
                    val productosComensal = orden.productos.filter { it.cliente == comensal.nombre }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.75f)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, brown)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Cuenta de: ${comensal.nombre.ifEmpty { "General" }}",
                                color = brown,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(productosComensal) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${item.cantidad}x", color = brown, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(item.platillo.nombre, modifier = Modifier.weight(1f), color = brown)
                                        Text("$${String.format(Locale.US, "%.2f", item.subtotal)}", color = brown)
                                    }
                                    HorizontalDivider(color = lightyellow2.copy(alpha = 0.5f))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Seleccionar Propina:", color = brown, fontWeight = FontWeight.Bold)

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val opciones = listOf("0%", "5%", "10%", "Personalizado")
                                opciones.forEach { txt ->
                                    val seleccionado = (etiquetaPropina == txt) ||
                                            (txt == "Personalizado" && etiquetaPropina !in listOf("0%", "5%", "10%"))
                                    val displayTxt = if (txt == "Personalizado" && seleccionado && etiquetaPropina != "Personalizado") etiquetaPropina else txt

                                    Box(
                                        modifier = Modifier
                                            .width(75.dp)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (seleccionado) yellow else lightyellow)
                                            .clickable {
                                                val sub = comensal.subtotal
                                                when (txt) {
                                                    "0%" -> { montoPropina = 0.0; etiquetaPropina = "0%" }
                                                    "5%" -> { montoPropina = sub * 0.05; etiquetaPropina = "5%" }
                                                    "10%" -> { montoPropina = sub * 0.1; etiquetaPropina = "10%" }
                                                    "Personalizado" -> showDialog = true
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(displayTxt, color = brown, textAlign = TextAlign.Center)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val subtotal = comensal.subtotal
                            val totalConPropina = subtotal + montoPropina

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(lightyellow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text("Total a pagar: $${String.format(Locale.US, "%.2f", totalConPropina)}",
                                    color = brown,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            val comensalesActualizados = orden.comensales?.map {
                                if (it.nombre == comensal.nombre) {
                                    it.copy(pagado = true, propina = montoPropina, total = it.subtotal + montoPropina)
                                } else it
                            }?.toMutableList()

                            val productosActualizados = orden.productos.map {
                                if (it.cliente == comensal.nombre) it.copy(pagado = true) else it
                            }.toMutableList()

                            val ordenActualizada = orden.copy(
                                comensales = comensalesActualizados,
                                productos = productosActualizados
                            )

                            FirebaseManager.guardarOrden(ordenActualizada, {
                                Toast.makeText(context, "${comensal.nombre} ha pagado", Toast.LENGTH_SHORT).show()
                                comensalSeleccionado = null
                                montoPropina = 0.0
                                etiquetaPropina = "0%"
                            }, { error ->
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = brown)
                    ) {
                        Text("Procesar pago", color = yellow)
                    }

                    TextButton(onClick = { comensalSeleccionado = null }) {
                        Text("← Volver a la lista", color = brown)
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = brown)
            }
        }
    }
}

/**
 * Preview del composable principal para diseño en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun PagarOrdenPersonaScreenPreview() {
    PompompurinCafeTheme {
        PagarOrdenPersonaScreen()
    }
}