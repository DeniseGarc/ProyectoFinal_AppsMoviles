package mx.edu.itson.pompompurincafe

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow

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
        // Header
        Image(
            painter = painterResource(id = R.drawable.header),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Titulo
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
fun OrdenCard(numero: String, personas: String, precio: String) {
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
            // Fila superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Número de mesa
                Text(
                    text = numero,
                    color = white,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    // Botón de pago
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(yellow, RoundedCornerShape(8.dp))
                            .clickable(){ /* Acción */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$", color = brown, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón de edición
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(yellow, RoundedCornerShape(8.dp))
                            .clickable(){ /* Acción */ },
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

            // Cantidad de personas
            Text(text = "$personas personas", color = white, fontSize = 16.sp)

            // Total de la mesa
            Text(
                text = "$$precio",
                color = yellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
fun OrdenesScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de la pantalla
        Image(
            painter = painterResource(id = R.drawable.fondo2_app),
            contentDescription = "fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            HeaderBanner()

            // titulo
            Text(
                text = "Ordenes activas",
                color = brown,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            // Ordenes
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(9) { index ->
                    // Datos de ejemplo
                    val numeros = listOf("10", "2", "6", "11", "12","13","14","3","9")
                    OrdenCard(
                        numero = numeros[index],
                        personas = "4",
                        precio = "142.50"
                    )
                }
            }

            // Espacio para que el botón no tape la última fila
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Botón "Nueva orden"
        Button(
            onClick = { /* Acción */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .height(60.dp)
                .fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = yellow),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add,
                    contentDescription = null,
                    tint = brown,
                    modifier = Modifier.size(30.dp))

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Nueva orden",
                    color = brown,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
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
