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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.ui.theme.*

class NuevaOrden : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                NuevaOrdenScreen()
            }
        }
    }
}

@Composable
fun NuevaOrdenScreen() {
    var tipoSeleccionado by remember { mutableIntStateOf(0) }
    var mesaSeleccionada by remember { mutableIntStateOf(0) }
    var ordenesActivas by remember { mutableStateOf<List<Orden>>(emptyList()) }

    // Obtenemos las órdenes de Firebase para validar disponibilidad
    LaunchedEffect(Unit) {
        FirebaseManager.suscribirseAOrdenes { ordenes ->
            ordenesActivas = ordenes.filter { !it.pagada }
        }
    }

    val mesasOcupadas = ordenesActivas.map { it.mesa }.toSet()

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

            Spacer(modifier = Modifier.height(20.dp))

            NuevaOrdenCard(
                seleccionado = tipoSeleccionado,
                onSeleccionCambiada = { tipoSeleccionado = it },
                mesaSeleccionada = mesaSeleccionada,
                onMesaChange = { mesaSeleccionada = it },
                mesasOcupadas = mesasOcupadas
            )

            Spacer(modifier = Modifier.height(24.dp))

            BotonNuevaOrden(
                tipo = tipoSeleccionado,
                mesa = mesaSeleccionada,
                mesasOcupadas = mesasOcupadas
            )

            Spacer(modifier = Modifier.weight(1f))

            FooterPompompurin()
        }
    }
}

@Composable
fun NuevaOrdenCard(
    seleccionado: Int, 
    onSeleccionCambiada: (Int) -> Unit,
    mesaSeleccionada: Int,
    onMesaChange: (Int) -> Unit,
    mesasOcupadas: Set<Int>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = yellow),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Nueva orden",
                color = brown,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Selecciona una mesa disponible:",
                color = brown,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                fontWeight = FontWeight.SemiBold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items((1..15).toList()) { numero ->
                    val estaOcupada = mesasOcupadas.contains(numero)
                    val esEstaSeleccionada = mesaSeleccionada == numero
                    
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    estaOcupada -> Color.LightGray
                                    esEstaSeleccionada -> brown
                                    else -> white
                                }
                            )
                            .clickable(enabled = !estaOcupada) { onMesaChange(numero) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$numero",
                            color = if (esEstaSeleccionada || estaOcupada) white else brown,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "Tipo de orden:",
                color = brown,
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp),
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(white)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (seleccionado == 0) brown else white)
                        .clickable { onSeleccionCambiada(0) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Toda la mesa",
                        color = if (seleccionado == 0) yellow else brown
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (seleccionado == 1) brown else white)
                        .clickable { onSeleccionCambiada(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Por persona",
                        color = if (seleccionado == 1) yellow else brown
                    )
                }
            }
        }
    }
}

@Composable
fun BotonNuevaOrden(tipo: Int, mesa: Int, mesasOcupadas: Set<Int>) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (mesa != 0) {
                // Validación final de disponibilidad por si se ocupó mientras llenaba el resto
                if (mesasOcupadas.contains(mesa)) {
                    Toast.makeText(context, "La mesa $mesa ya no está disponible", Toast.LENGTH_SHORT).show()
                } else {
                    val etiqueta = if (tipo == 0) "MESA" else "PERSONA"
                    val nuevaOrden = Orden(
                        mesa = mesa,
                        tipo = etiqueta
                    )

                    FirebaseManager.guardarOrden(nuevaOrden, 
                        onSuccess = {
                            val pantalla = if (tipo == 0) Menu::class.java else OrdenPersona::class.java
                            val intent = Intent(context, pantalla)
                            intent.putExtra("tipoOrden", etiqueta)
                            intent.putExtra("ordenId", nuevaOrden.id)
                            context.startActivity(intent)
                        }, 
                        onError = {
                            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            } else {
                Toast.makeText(context, "Por favor selecciona una mesa", Toast.LENGTH_SHORT).show()
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = brown),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(0.5f).height(60.dp)
    ) {
        Text(
            text = "Tomar orden",
            color = yellow,
            fontSize = 18.sp,
            fontFamily = CustomFontFamily
        )
    }
}

@Composable
fun FooterPompompurin() {
    Image(
        painter = painterResource(id = R.drawable.nuevaorden_pompompurin),
        contentDescription = "Pompompurin",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

@Preview(showBackground = true)
@Composable
fun NuevaOrdenScreenPreview() {
    PompompurinCafeTheme {
        NuevaOrdenScreen()
    }
}
