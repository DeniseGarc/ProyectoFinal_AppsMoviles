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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

class OrdenPersona : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                ResumenOrdenPersonaScreen()
            }
        }
    }
}

@Composable
fun OrdenPersonaListaPlatillos(item: ItemOrden, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, brown)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${item.cantidad}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = brown)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = item.platillo.nombre, fontSize = 16.sp, color = brown, modifier = Modifier.width(200.dp))
            }
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = brown,
                modifier = Modifier.size(28.dp).clickable { onRemove() }
            )
        }
    }
}

@Composable
fun PersonaHeader(numero: Int, nombre: String, ordenId: String, tipoOrden: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(brown, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("$numero", color = white, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(nombre, color = brown, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.size(32.dp).background(yellow, RoundedCornerShape(8.dp))
                .clickable {
                    val intent = Intent(context, Menu::class.java)
                    intent.putExtra("tipoOrden", tipoOrden)
                    intent.putExtra("ordenId", ordenId)
                    intent.putExtra("clienteNombre", nombre) // Para saber a quién agregarle platillos
                    context.startActivity(intent)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Edit, null, tint = brown, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun ResumenOrdenPersonaScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val ordenId = activity?.intent?.getStringExtra("ordenId") ?: ""
    val tipoOrden = activity?.intent?.getStringExtra("tipoOrden") ?: "PERSONA"

    var nombreCliente by remember { mutableStateOf("") }
    var ordenActual by remember { mutableStateOf<Orden?>(null) }

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

                // Formulario agregar persona
                Card(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    colors = CardDefaults.cardColors(containerColor = brown),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Agregar persona", color = yellow, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        Text("Nombre del cliente:", color = yellow, fontSize = 12.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = nombreCliente,
                                onValueChange = { nombreCliente = it },
                                modifier = Modifier.weight(1f).height(35.dp).background(white, RoundedCornerShape(4.dp)),
                                textStyle = TextStyle(color = brown, fontSize = 16.sp, textAlign = TextAlign.Center),
                                decorationBox = { innerTextField ->
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        if (nombreCliente.isEmpty()) {
                                            Text("Ingresa el nombre", color = Color.Gray, fontSize = 14.sp)
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier.size(35.dp).background(yellow, CircleShape).clickable {
                                    val nombreTrim = nombreCliente.trim()
                                    if (nombreTrim.isNotEmpty()) {
                                        // Validación para no agregar duplicados
                                        val existe = ordenActual?.productos?.any { it.cliente.equals(nombreTrim, ignoreCase = true) } ?: false
                                        
                                        if (existe) {
                                            Toast.makeText(context, "Esta persona ya está registrada en la mesa", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Abrir menú para esta persona específica
                                            val intent = Intent(context, Menu::class.java)
                                            intent.putExtra("tipoOrden", tipoOrden)
                                            intent.putExtra("ordenId", ordenId)
                                            intent.putExtra("clienteNombre", nombreTrim)
                                            context.startActivity(intent)
                                            nombreCliente = ""
                                        }
                                    } else {
                                        Toast.makeText(context, "Escribe un nombre", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, "añadir", tint = white)
                            }
                        }
                    }
                }

                val productosPorCliente = orden.productos.groupBy { it.cliente }

                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 100.dp)) {
                    productosPorCliente.forEach { (cliente, items) ->
                        item {
                            PersonaHeader(
                                numero = productosPorCliente.keys.indexOf(cliente) + 1,
                                nombre = cliente.ifEmpty { "General" },
                                ordenId = ordenId,
                                tipoOrden = tipoOrden
                            )
                        }
                        items(items) { itemOrden ->
                            OrdenPersonaListaPlatillos(itemOrden) {
                                val nuevaLista = orden.productos.toMutableList()
                                nuevaLista.remove(itemOrden)
                                FirebaseManager.guardarOrden(orden.copy(productos = nuevaLista), {}, {})
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { context.startActivity(Intent(context, MainActivity::class.java)) },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp).height(65.dp).fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = yellow),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Mandar a cocina", color = brown, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.ArrowForward, null, tint = brown, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResumenOrdenPersonaScreenPreview() {
    PompompurinCafeTheme {
        ResumenOrdenPersonaScreen()
    }
}
