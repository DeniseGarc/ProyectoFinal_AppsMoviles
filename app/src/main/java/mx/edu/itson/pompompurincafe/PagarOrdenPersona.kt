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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.ui.theme.*
import java.util.Locale

class PagarOrdenPersona : ComponentActivity() {
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

@Composable
fun PagarOrdenPersonaScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val ordenId = activity?.intent?.getStringExtra("ordenId") ?: ""

    var ordenActual by remember { mutableStateOf<Orden?>(null) }
    var personaSeleccionada by remember { mutableStateOf("") }
    
    var montoPropina by remember { mutableDoubleStateOf(0.0) }
    var etiquetaPropina by remember { mutableStateOf("0%") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(ordenId) {
        if (ordenId.isNotEmpty()) {
            FirebaseManager.obtenerOrden(ordenId) { orden ->
                ordenActual = orden
                if (orden != null && orden.productos.isNotEmpty() && personaSeleccionada.isEmpty()) {
                    personaSeleccionada = orden.productos.first().cliente
                }
            }
        }
    }

    val productosPorCliente = ordenActual?.productos?.groupBy { it.cliente } ?: emptyMap()
    val itemsDelCliente = productosPorCliente[personaSeleccionada] ?: emptyList()
    val subtotalCliente = itemsDelCliente.sumOf { it.subtotal }

    if (showDialog) {
        CustomTipDialogPersona(
            subtotal = subtotalCliente,
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
                    fontFamily = FontFamily(Font(R.font.fredoka_bold))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.8f)
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, brown)
                ) {
                    OrdenContenido(
                        orden = orden,
                        nombreSeleccionado = personaSeleccionada,
                        onPersonaCambiada = { 
                            personaSeleccionada = it
                            montoPropina = 0.0
                            etiquetaPropina = "0%"
                        },
                        montoPropina = montoPropina,
                        etiquetaPropina = etiquetaPropina,
                        onPropinaSeleccionada = { monto, label ->
                            montoPropina = monto
                            etiquetaPropina = label
                        },
                        onCustomTipClick = { showDialog = true }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                BotonProcesarPagoFinal(orden)

                Spacer(modifier = Modifier.height(24.dp))
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brown)
                }
            }
        }
    }
}

@Composable
fun CustomTipDialogPersona(
    subtotal: Double,
    onConfirm: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf("") }
    var isPercentage by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Propina Personalizada", color = brown, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isPercentage,
                        onClick = { isPercentage = true },
                        colors = RadioButtonDefaults.colors(selectedColor = brown)
                    )
                    Text("Porcentual (%)", color = brown)
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = !isPercentage,
                        onClick = { isPercentage = false },
                        colors = RadioButtonDefaults.colors(selectedColor = brown)
                    )
                    Text("Fijo ($)", color = brown)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) value = it },
                    label = { Text(if (isPercentage) "Porcentaje" else "Monto", color = brown) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brown,
                        unfocusedBorderColor = brown,
                        cursorColor = brown
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val valDouble = value.toDoubleOrNull() ?: 0.0
                    val finalTip = if (isPercentage) subtotal * (valDouble / 100.0) else valDouble
                    val label = if (isPercentage) "${valDouble}%" else "$${valDouble}"
                    onConfirm(finalTip, label)
                },
                colors = ButtonDefaults.buttonColors(containerColor = brown)
            ) {
                Text("Confirmar", color = yellow)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = brown)
            }
        },
        containerColor = white
    )
}

@Composable
fun OrdenContenido(
    orden: Orden,
    nombreSeleccionado: String,
    onPersonaCambiada: (String) -> Unit,
    montoPropina: Double,
    etiquetaPropina: String,
    onPropinaSeleccionada: (Double, String) -> Unit,
    onCustomTipClick: () -> Unit
) {
    val productosPorCliente = orden.productos.groupBy { it.cliente }
    val itemsDelCliente = productosPorCliente[nombreSeleccionado] ?: emptyList()
    val subtotalCliente = itemsDelCliente.sumOf { it.subtotal }
    val propina = montoPropina
    val totalFinal = subtotalCliente + propina

    Column(modifier = Modifier.padding(20.dp)) {
        Text(
            text = "Seleccionar cuenta",
            color = brown,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_bold))
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Lista de personas con cuenta
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            productosPorCliente.keys.forEach { nombre ->
                val items = productosPorCliente[nombre] ?: emptyList()
                val totalPersona = items.sumOf { it.subtotal }
                CuentaPersonaItem(
                    nombre = nombre.ifEmpty { "General" },
                    cantidad = totalPersona,
                    seleccionado = nombreSeleccionado == nombre
                ) { onPersonaCambiada(nombre) }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Platillos de ${nombreSeleccionado.ifEmpty { "General" }}",
            color = brown,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_bold)),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(itemsDelCliente) { item ->
                ItemPlatilloRow("${item.cantidad}x", item.platillo.nombre, item.subtotal)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SeleccionPropinaRow(
            subtotal = subtotalCliente,
            etiquetaActual = etiquetaPropina,
            onPropinaSeleccionada = onPropinaSeleccionada,
            onCustomClick = onCustomTipClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().background(lightyellow.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = brown, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
                Text("$${String.format(Locale.US, "%.2f", subtotalCliente)}", color = brown, fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Propina", color = brown, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
                Text("$${String.format(Locale.US, "%.2f", propina)}", color = brown, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("Total a pagar", color = brown, fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)))
                Text("$${String.format(Locale.US, "%.2f", totalFinal)}", color = brown, fontSize = 28.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)))
            }
        }
    }
}

@Composable
fun CuentaPersonaItem(nombre: String, cantidad: Double, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (seleccionado) brown else lightyellow2)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (seleccionado) yellow else brown,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = nombre,
                color = if (seleccionado) yellow else brown,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.fredoka_medium))
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${String.format(Locale.US, "%.2f", cantidad)}",
                color = if (seleccionado) yellow else brown,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.fredoka_bold))
            )
        }
    }
}

@Composable
fun ItemPlatilloRow(cantidad: String, nombre: String, precio: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(cantidad, color = brown, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp))
        Text(nombre, color = brown, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text("$${String.format(Locale.US, "%.2f", precio)}", color = brown, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
    HorizontalDivider(color = lightyellow2, thickness = 1.dp)
}

@Composable
fun SeleccionPropinaRow(
    subtotal: Double,
    etiquetaActual: String,
    onPropinaSeleccionada: (Double, String) -> Unit,
    onCustomClick: () -> Unit
) {
    val opciones = listOf("0%", "5%", "10%", "Personalizado")

    Column {
        Text("Propina:", color = brown, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            opciones.forEach { txt ->
                val seleccionado = (etiquetaActual == txt) || (txt == "Personalizado" && etiquetaActual !in listOf("0%", "5%", "10%"))
                val displayTxt = if (txt == "Personalizado" && seleccionado && etiquetaActual != "Personalizado") etiquetaActual else txt
                
                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (seleccionado) yellow else lightyellow)
                        .clickable { 
                            when (txt) {
                                "0%" -> onPropinaSeleccionada(0.0, "0%")
                                "5%" -> onPropinaSeleccionada(subtotal * 0.05, "5%")
                                "10%" -> onPropinaSeleccionada(subtotal * 0.1, "10%")
                                "Personalizado" -> onCustomClick()
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
    }
}

@Composable
fun BotonProcesarPagoFinal(orden: Orden) {
    val context = LocalContext.current

    Button(
        onClick = {
            val ordenPagada = orden.copy(pagada = true)
            FirebaseManager.guardarOrden(ordenPagada, {
                Toast.makeText(context, "Mesa ${orden.mesa} pagada exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }, {
                Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
            })
        },
        colors = ButtonDefaults.buttonColors(containerColor = brown),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(0.8f).height(65.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Finalizar Pago", color = yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Cerrar cuenta Mesa ${orden.mesa}", color = yellow, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PagarOrdenPersonaScreenPreview() {
    PompompurinCafeTheme {
        PagarOrdenPersonaScreen()
    }
}
