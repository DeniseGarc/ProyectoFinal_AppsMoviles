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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.Comensal
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.model.Pago
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
    val auth = Firebase.auth

    var ordenActual by remember { mutableStateOf<Orden?>(null) }
    var comensalSeleccionado by remember { mutableStateOf<Comensal?>(null) }
    var montoPropina by remember { mutableDoubleStateOf(0.0) }
    var etiquetaPropina by remember { mutableStateOf("0%") }
    var showDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Pagado",
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(18.dp).padding(end = 4.dp)
                                            )
                                            Text(
                                                text = "Pagado",
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 16.sp
                                            )
                                        }
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
                                        Text(
                                            text = "${item.cantidad}x",
                                            color = brown,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = item.platillo.nombre,
                                            color = brown,
                                            modifier = Modifier.weight(1f),
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "$${String.format(Locale.US, "%.2f", item.subtotal)}",
                                            color = brown,
                                            fontSize = 14.sp
                                        )
                                    }
                                    HorizontalDivider(color = lightyellow2.copy(alpha = 0.5f), thickness = 1.dp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Seleccionar Propina:",
                                color = brown,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
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
                                        Text(
                                            text = displayTxt,
                                            color = brown,
                                            fontSize = if (displayTxt.length > 5) 10.sp else 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal", color = brown, fontSize = 16.sp)
                                    Text("$${String.format(Locale.US, "%.2f", subtotal)}", color = brown, fontSize = 16.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Propina", color = brown, fontSize = 14.sp)
                                    Text("$${String.format(Locale.US, "%.2f", montoPropina)}", color = brown, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text("Total a pagar", color = brown, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "$${String.format(Locale.US, "%.2f", totalConPropina)}",
                                        color = brown,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (isProcessing) {
                        CircularProgressIndicator(color = brown)
                    } else {
                        Button(
                            onClick = {
                                isProcessing = true
                                val subtotalPago = comensal.subtotal
                                val totalFinalPago = subtotalPago + montoPropina

                                val nuevoPago = Pago(
                                    ordenId = orden.id,
                                    comensalId = comensal.id,
                                    monto = subtotalPago,
                                    propina = montoPropina,
                                    total = totalFinalPago,
                                    mesero = auth.currentUser?.email ?: "Desconocido"
                                )

                                // 1. Registrar el pago independiente
                                FirebaseManager.registrarPago(nuevoPago, {
                                    // 2. Actualizar la orden localmente y en Firebase
                                    val comensalesActualizados = orden.comensales?.map {
                                        if (it.nombre == comensal.nombre) {
                                            it.copy(pagado = true, propina = montoPropina, total = totalFinalPago)
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
                                        isProcessing = false
                                        Toast.makeText(context, "Pago de ${comensal.nombre} registrado", Toast.LENGTH_SHORT).show()
                                        comensalSeleccionado = null
                                        montoPropina = 0.0
                                        etiquetaPropina = "0%"
                                    }, { error ->
                                        isProcessing = false
                                        Toast.makeText(context, "Error al actualizar orden: $error", Toast.LENGTH_SHORT).show()
                                    })
                                }, { error ->
                                    isProcessing = false
                                    Toast.makeText(context, "Error al registrar pago: $error", Toast.LENGTH_SHORT).show()
                                })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = brown),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(65.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Procesar pago", color = yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(comensal.nombre, color = yellow, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { if (!isProcessing) comensalSeleccionado = null }) {
                        Text("← Volver a la lista", color = brown, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brown)
                }
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