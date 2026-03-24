package mx.edu.itson.pompompurincafe

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    var mesa by remember { mutableStateOf("") }
    var numPersonas by remember { mutableStateOf("") }

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

            // Usamos TextField con diseño personalizado
            TextField(
                value = mesa,
                onValueChange = { mesa = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = white,
                    unfocusedContainerColor = white,
                    disabledContainerColor = white,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = brown,
                    focusedTextColor = brown,
                    unfocusedTextColor = brown
                ),
                singleLine = true
            )

            Text(
                text = "Número de personas:",
                color = brown,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                fontFamily = FontFamily(Font(R.font.fredoka_medium))
            )

            TextField(
                value = numPersonas,
                onValueChange = { numPersonas = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = white,
                    unfocusedContainerColor = white,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = brown,
                    focusedTextColor = brown,
                    unfocusedTextColor = brown
                ),
                singleLine = true
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
                    .height(60.dp)
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
    val context = LocalContext.current

    Button(
        onClick = {
            val pantalla = if (tipo == 0) Menu::class.java else OrdenPersona::class.java
            val intent = Intent(context, pantalla)

            val etiqueta = if (tipo == 0) "MESA" else "PERSONA"
            intent.putExtra("tipoOrden", etiqueta)

            context.startActivity(intent)
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