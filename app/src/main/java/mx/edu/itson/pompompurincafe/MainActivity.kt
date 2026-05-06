package mx.edu.itson.pompompurincafe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.ui.theme.CustomFontFamily
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow
import java.util.Locale

/**
 * Activity principal que muestra las órdenes activas.
 */
class MainActivity : ComponentActivity() {

    /**
     * Inicializa la pantalla principal.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                OrdenesScreen()
            }
        }
    }
}

/**
 * Composable que muestra el banner superior.
 */
@Composable
fun HeaderBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.header),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "POMPOMPURIN  café",
                color = yellow,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/**
 * Composable que representa una tarjeta de orden.
 */
@Composable
fun OrdenCard(orden: Orden, onDelete: () -> Unit) {
    val context = LocalContext.current

    val personasTexto = if (orden.tipoCuenta == "individual") {
        if (orden.numPersonas == 1) "1 persona" else "${orden.numPersonas ?: 0} personas"
    } else {
        "Toda la mesa"
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = brown),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${orden.mesa}",
                    color = white,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(yellow, RoundedCornerShape(8.dp))
                            .clickable {
                                val activity = if (orden.tipoCuenta == "mesa") PagarOrdenMesa::class.java else PagarOrdenPersona::class.java
                                val intent = Intent(context, activity)
                                intent.putExtra("tipoOrden", orden.tipoCuenta)
                                intent.putExtra("ordenId", orden.id)
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$", color = brown, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(yellow, RoundedCornerShape(8.dp))
                            .clickable {
                                val activity = if (orden.tipoCuenta == "mesa") OrdenMesa::class.java else OrdenPersona::class.java
                                val intent = Intent(context, activity)
                                intent.putExtra("tipoOrden", orden.tipoCuenta)
                                intent.putExtra("ordenId", orden.id)
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = brown,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFE57373), RoundedCornerShape(8.dp))
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = white,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = personasTexto, color = white, fontSize = 14.sp)
            Text(text = "Mesero: ${orden.mesero}", color = white.copy(alpha = 0.8f), fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text(text = "Subtotal", color = white, fontSize = 12.sp)
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", orden.subtotal)}",
                        color = white,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Total", color = yellow, fontSize = 12.sp)
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", orden.total)}",
                        color = yellow,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Composable que dibuja el fondo de la aplicación.
 */
@Composable
fun Fondo() {
    Image(
        painter = painterResource(id = R.drawable.fondo2_app),
        contentDescription = "fondo de rayas",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}

/**
 * Composable principal que muestra la lista de órdenes.
 */
@Composable
fun OrdenesScreen() {
    val context = LocalContext.current
    var listaOrdenes by remember { mutableStateOf<List<Orden>>(emptyList()) }
    var ordenAEliminar by remember { mutableStateOf<Orden?>(null) }

    LaunchedEffect(Unit) {
        FirebaseManager.suscribirseAOrdenes { ordenes ->
            listaOrdenes = ordenes.filter { !it.pagada }
        }
    }

    if (ordenAEliminar != null) {
        AlertDialog(
            onDismissRequest = { ordenAEliminar = null },
            title = { Text(text = "Confirmar eliminación") },
            text = { Text(text = "¿Estás seguro de que deseas borrar la orden de la mesa ${ordenAEliminar?.mesa}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        ordenAEliminar?.let { orden ->
                            FirebaseManager.eliminarOrden(
                                orden.id,
                                onSuccess = {
                                    Toast.makeText(context, "Orden eliminada", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        ordenAEliminar = null
                    }
                ) {
                    Text(text = "Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { ordenAEliminar = null }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Fondo()

        Image(
            painter = painterResource(id = R.drawable.ordenes2_pompompurin),
            contentDescription = "pompompurin fondo",
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HeaderBanner()

            Text(
                text = "Ordenes activas",
                color = brown,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(listaOrdenes) { orden ->
                    OrdenCard(orden, onDelete = { ordenAEliminar = orden })
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        Button(
            onClick = {
                val intent = Intent(context, NuevaOrden::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .height(60.dp)
                .fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = yellow),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = brown, modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nueva orden",
                    color = brown,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CustomFontFamily
                )
            }
        }
    }
}

/**
 * Vista previa de la pantalla de órdenes.
 */
@Preview(showBackground = true)
@Composable
fun OrdenesScreenPreview() {
    PompompurinCafeTheme {
        OrdenesScreen()
    }
}

/**
 * Vista previa del banner.
 */
@Preview(showBackground = true)
@Composable
fun HeaderBannerPreview() {
    HeaderBanner()
}
