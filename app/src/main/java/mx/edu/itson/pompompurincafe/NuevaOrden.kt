package mx.edu.itson.pompompurincafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    // estado de la selección
    var tipoSeleccionado by remember { mutableIntStateOf(0) }

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

            Spacer(modifier = Modifier.height(30.dp))

            NuevaOrdenCard(
                seleccionado = tipoSeleccionado,
                onSeleccionCambiada = { tipoSeleccionado = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            BotonNuevaOrden(tipo = tipoSeleccionado)

            Spacer(modifier = Modifier.weight(1f))

            FooterPompompurin()
        }
    }
}

@Composable
fun NuevaOrdenCard(seleccionado: Int, onSeleccionCambiada: (Int) -> Unit) {
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
                fontFamily = FontFamily(Font(R.font.fredoka_bold))
            )

            Text(
                text = "Número de mesa:",
                color = brown,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                fontFamily = FontFamily(Font(R.font.fredoka_medium))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(white, RoundedCornerShape(8.dp))
            )

            Text(
                text = "Número de personas:",
                color = brown,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                fontFamily = FontFamily(Font(R.font.fredoka_medium))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(white, RoundedCornerShape(8.dp))
            )

            Text(
                text = "Tipo de orden:",
                color = brown,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                fontFamily = FontFamily(Font(R.font.fredoka_medium))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
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
                        color = if (seleccionado == 0) yellow else brown,
                        fontFamily = FontFamily(Font(R.font.fredoka_medium))
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
                        color = if (seleccionado == 1) yellow else brown,
                        fontFamily = FontFamily(Font(R.font.fredoka_medium))
                    )
                }
            }
        }
    }
}

@Composable
fun BotonNuevaOrden(tipo: Int) {
    Button(
        onClick = {
            if (tipo == 0) {
                // flujo de pantallas
            } else {
                // flujo de pantallas
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
            fontFamily = FontFamily(Font(R.font.fredoka_bold))
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevaOrdenPreview() {
    PompompurinCafeTheme {
        NuevaOrdenScreen()
    }
}