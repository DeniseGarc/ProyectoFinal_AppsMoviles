package mx.edu.itson.pompompurincafe

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.ui.theme.*

class PagarOrdenPersona : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                PagarOrdenPersonaScreen("Mesa 12")
            }
        }
    }
}

@Composable
fun PagarOrdenPersonaScreen(numeroMesa: String) {
    var personaSeleccionada by remember { mutableStateOf("Juan") }

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

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = white),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, brown)
            ) {
                Orden(
                    nombreSeleccionado = personaSeleccionada,
                    onPersonaCambiada = { personaSeleccionada = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BotonProcesarPago(personaSeleccionada)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Orden(nombreSeleccionado: String, onPersonaCambiada: (String) -> Unit) {
    Column(modifier = Modifier.padding(20.dp)) {

        Text(
            text = "Seleccionar cuenta",
            color = brown,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_bold))
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CuentaPersona("Juan", nombreSeleccionado == "Juan") { onPersonaCambiada("Juan") }
            CuentaPersona("Maria", nombreSeleccionado == "Maria") { onPersonaCambiada("Maria") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Platillos ordenados",
            color = brown,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_bold)),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(4) {
                ItemPlatillo("1", "Nombre del platillo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SeleccionPropina()

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text("Subtotal", color = brown, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
            Text("$$$$$$$$$$$", color = brown, fontSize = 22.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)))

            Spacer(modifier = Modifier.height(8.dp))

            Text("Total", color = brown, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
            Text("$$$$$$$$$$$", color = brown, fontSize = 30.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)))
        }
    }
}

@Composable
fun CuentaPersona(nombre: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
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
                text = "$$$$$",
                color = if (seleccionado) yellow else brown,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.fredoka_medium))
            )
        }
    }
}

@Composable
fun BotonProcesarPago(nombre: String) {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(containerColor = brown),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(0.75f).height(65.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Procesar pago", color = yellow, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.fredoka_bold)))
            Text("Cerrar cuenta de $nombre", color = yellow, fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.fredoka_medium)))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PagarOrdenPersonaPreview() {
    PompompurinCafeTheme {
        PagarOrdenPersonaScreen("Mesa 12")
    }
}