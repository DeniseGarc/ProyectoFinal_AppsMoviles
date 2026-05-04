package mx.edu.itson.pompompurincafe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.ui.theme.CustomFontFamily
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow
import java.util.Locale

class MainActivity : ComponentActivity() {
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

@Composable
fun OrdenCard(orden: Orden) {
    val context = LocalContext.current
    
    // Mostramos el número de personas registrado en la base de datos
    val personasTexto = if (orden.tipo == "PERSONA") {
        if (orden.numPersonas == 1) "1 persona" else "${orden.numPersonas} personas"
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
                                val activity = if (orden.tipo == "MESA") PagarOrdenMesa::class.java else PagarOrdenPersona::class.java
                                val intent = Intent(context, activity)
                                intent.putExtra("tipoOrden", orden.tipo)
                                intent.putExtra("ordenId", orden.id)
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$", color = brown, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(yellow, RoundedCornerShape(8.dp))
                            .clickable {
                                val activity = if (orden.tipo == "MESA") OrdenMesa::class.java else OrdenPersona::class.java
                                val intent = Intent(context, activity)
                                intent.putExtra("tipoOrden", orden.tipo)
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Mostramos el texto basado en la propiedad numPersonas de la orden
            Text(text = personasTexto, color = white, fontSize = 16.sp)
            Text(
                text = "$${String.format(Locale.US, "%.2f", orden.total)}",
                color = yellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Fondo(){
    Image(
        painter = painterResource(id = R.drawable.fondo2_app),
        contentDescription = "fondo de rayas",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun OrdenesScreen() {
    val context = LocalContext.current
    var listaOrdenes by remember { mutableStateOf<List<Orden>>(emptyList()) }

    LaunchedEffect(Unit) {
        FirebaseManager.suscribirseAOrdenes { ordenes ->
            listaOrdenes = ordenes.filter { !it.pagada }
        }
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
                    OrdenCard(orden)
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
                Text(text = "Nueva orden", color = brown, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = CustomFontFamily)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdenesScreenPreview() {
    PompompurinCafeTheme {
        OrdenesScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderBannerPreview() {
    HeaderBanner()
}
