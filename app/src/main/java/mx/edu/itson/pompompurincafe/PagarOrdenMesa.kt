package mx.edu.itson.pompompurincafe

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.ui.theme.*

class PagarOrdenMesa : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                PagarOrdenMesaScreen("Mesa 12")
            }
        }
    }
}

@Composable
fun PagarOrdenMesaScreen(numeroMesa: String) {
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

            Text(
                text = numeroMesa,
                color = brown,
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.fredoka_bold))
            )

            Spacer(modifier = Modifier.height(5 .dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = white),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, brown)
            ) {
                Orden()
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonProcesarPago()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Orden() {
    Column(modifier = Modifier.padding(20.dp)) {
        Text(
            text = "Platillos ordenados",
            color = brown,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_bold)),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(1) {
                ItemPlatillo("2", "Mango Soda with Icecream", 84.38)
                ItemPlatillo("1", "Fluffy Souffle Omelette Rice", 212.64)
                ItemPlatillo("1", "Pompompurin´s Beef Strog...", 156.39)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SeleccionPropina()

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(text = "Subtotal", color = brown, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
            Text(text = "$537.79", color = brown, fontSize = 22.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text(text = "Total", color = brown, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
            Text(text = "$537.79", color = brown, fontSize = 30.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
        }
    }
}

@Composable
fun ItemPlatillo(cantidad: String, nombre: String, precio: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = lightyellow2,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(cantidad, color = brown, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)), modifier = Modifier.padding(end = 12.dp))
            Text(nombre, color = brown, fontSize = 15.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)) )
            Spacer(modifier = Modifier.weight(1f))
            Text("$$precio", color = brown, fontSize = 15.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
        }
    }
}

@Composable
fun SeleccionPropina() {
    var seleccionado by remember { mutableStateOf("") }
    val opciones = listOf("10%", "15%", "20%", "otro")

    Column {
        Text(
            text = "Porcentaje de propina",
            color = brown,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_medium  ))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            opciones.forEach { opcion ->
                val porcentaje = seleccionado == opcion

                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(35.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (porcentaje) lightyellow else yellow)
                        .clickable {
                            seleccionado = if (porcentaje) "" else opcion
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opcion,
                        color = brown,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.fredoka_medium))
                    )
                }
            }
        }
    }
}

@Composable
fun BotonProcesarPago() {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(containerColor = brown),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(0.75f).height(65.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Procesar pago", color = yellow, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)))
            Text("Cerrar cuenta Mesa 10", color = yellow, fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PagarOrdenPreview() {
    PompompurinCafeTheme {
        PagarOrdenMesaScreen("Mesa 10")
    }
}