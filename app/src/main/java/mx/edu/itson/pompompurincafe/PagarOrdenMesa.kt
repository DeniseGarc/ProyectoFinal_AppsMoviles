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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.model.Pago
import mx.edu.itson.pompompurincafe.ui.theme.*
import java.util.Locale

/**
 * Activity que maneja el pago de una orden por mesa completa.
 */
class PagarOrdenMesa : ComponentActivity() {

    /**
     * Inicializa la pantalla de pago de la mesa.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                PagarOrdenMesaScreen()
            }
        }
    }
}

/**
 * Composable principal para mostrar el flujo de pago de una mesa.
 */
@Composable
fun PagarOrdenMesaScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val ordenId = activity?.intent?.getStringExtra("ordenId") ?: ""
    val auth = Firebase.auth

    var ordenActual by remember { mutableStateOf<Orden?>(null) }
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
            subtotal = ordenActual?.total ?: 0.0,
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.75f)
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, brown)
                ) {
                    ResumenPagoContenido(
                        orden = orden,
                        montoPropina = montoPropina,
                        etiquetaPropina = etiquetaPropina,
                        onPropinaChange = { monto, label ->
                            montoPropina = monto
                            etiquetaPropina = label
                        },
                        onCustomClick = { showDialog = true }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isProcessing) {
                    CircularProgressIndicator(color = brown)
                } else {
                    Button(
                        onClick = {
                            isProcessing = true
                            val subtotal = orden.subtotal
                            val totalFinal = subtotal + montoPropina
                            
                            val nuevoPago = Pago(
                                ordenId = orden.id,
                                monto = subtotal,
                                propina = montoPropina,
                                total = totalFinal,
                                mesero = auth.currentUser?.email ?: "Desconocido"
                            )

                            // 1. Registrar el pago en su propio nodo
                            FirebaseManager.registrarPago(nuevoPago, {
                                // 2. Actualizar el estado de la orden
                                val ordenPagada = orden.copy(
                                    pagada = true,
                                    propina = montoPropina,
                                    total = totalFinal
                                )
                                FirebaseManager.guardarOrden(ordenPagada, {
                                    isProcessing = false
                                    Toast.makeText(context, "Pago de mesa ${orden.mesa} registrado", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
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
                            Text("Mesa ${orden.mesa}", color = yellow, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brown)
                }
            }
        }
    }
}

/**
 * Diálogo para ingresar una propina personalizada.
 */
@Composable
fun CustomTipDialog(
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
                    RadioButton(selected = isPercentage, onClick = { isPercentage = true }, colors = RadioButtonDefaults.colors(selectedColor = brown))
                    Text("Porcentual (%)", color = brown)
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = !isPercentage, onClick = { isPercentage = false }, colors = RadioButtonDefaults.colors(selectedColor = brown))
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

/**
 * Composable que muestra el resumen de pago con propina y total.
 */
@Composable
fun ResumenPagoContenido(
    orden: Orden,
    montoPropina: Double,
    etiquetaPropina: String,
    onPropinaChange: (Double, String) -> Unit,
    onCustomClick: () -> Unit
) {
    val subtotal = orden.subtotal
    val propina = montoPropina
    val totalConPropina = subtotal + propina

    Column(modifier = Modifier.padding(20.dp)) {
        Text(
            text = "Resumen de Cuenta",
            color = brown,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(orden.productos) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${item.cantidad}x", color = brown, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.platillo.nombre, color = brown, modifier = Modifier.weight(1f))
                    Text("$${String.format(Locale.US, "%.2f", item.subtotal)}", color = brown)
                }
                HorizontalDivider(color = lightyellow2.copy(alpha = 0.5f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Seleccionar Propina:", color = brown, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            val opciones = listOf("0%", "5%", "10%", "Personalizado")
            opciones.forEach { txt ->
                val seleccionado = (etiquetaPropina == txt) || (txt == "Personalizado" && etiquetaPropina !in listOf("0%", "5%", "10%"))
                val displayTxt = if (txt == "Personalizado" && seleccionado && etiquetaPropina != "Personalizado") etiquetaPropina else txt

                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (seleccionado) yellow else lightyellow)
                        .clickable {
                            when (txt) {
                                "0%" -> onPropinaChange(0.0, "0%")
                                "5%" -> onPropinaChange(subtotal * 0.05, "5%")
                                "10%" -> onPropinaChange(subtotal * 0.1, "10%")
                                "Personalizado" -> onCustomClick()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(displayTxt, color = brown, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(lightyellow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = brown)
                Text("$${String.format(Locale.US, "%.2f", subtotal)}", color = brown)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Propina", color = brown)
                Text("$${String.format(Locale.US, "%.2f", propina)}", color = brown)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total a pagar", color = brown, fontWeight = FontWeight.Bold)
                Text("$${String.format(Locale.US, "%.2f", totalConPropina)}", color = brown, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

/**
 * Vista previa de la pantalla de pago de mesa.
 */
@Preview(showBackground = true)
@Composable
fun PagarOrdenMesaScreenPreview() {
    PompompurinCafeTheme {
        PagarOrdenMesaScreen()
    }
}