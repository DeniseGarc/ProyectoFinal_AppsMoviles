package mx.edu.itson.pompompurincafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow

class OrdenMesa : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                ResumenOrdenScreen()
            }
        }
    }
}


@Composable
fun OrdenListaPlatillos(cantidad: Int, nombre: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, brown)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Cantidad del platillo
                Text(
                    text = "$cantidad",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = brown
                )
                Spacer(modifier = Modifier.width(16.dp))

                // Nombre del platillo
                Text(
                    text = "$nombre",
                    fontSize = 16.sp,
                    color = brown,
                    fontWeight = FontWeight.Medium
                )
            }

            // Botón para eliminar platillo de la orden
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = brown,
                modifier = Modifier.size(28.dp)
                    .clickable() { /* Acción */ }
            )
        }
    }
}

@Composable
fun InfoMesa(numMesa: Int, numPersonas: Int, total: Double){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Número de mesa
            Text(
                text = "Mesa $numMesa",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = brown
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Cantidad de personas
            Surface(
                color = yellow,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "$numPersonas personas",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = brown
                )
            }
        }

        // Total de la orden
        Column(horizontalAlignment = Alignment.End) {
            Text("Total", fontSize = 16.sp, color = brown)
            Text("$$total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = brown)
        }
    }
}

@Composable
fun ResumenOrdenScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        Fondo()

        Column(modifier = Modifier.fillMaxSize()) {
            HeaderBanner()

           // Info mesa
            InfoMesa(12,4,142.50)

            Row(
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Titulo Mesa editar
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(brown, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1",
                        color = white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Mesa",
                    color = brown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))

                // Icono Editar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(yellow, RoundedCornerShape(4.dp))
                        .clickable { /* Acción */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Edit, null, tint = brown, modifier = Modifier.size(18.dp))
                }
            }

            // Lista de platillos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp) // Espacio para el botón de abajo
            ) {
                items(10) {
                    OrdenListaPlatillos(cantidad = 1, nombre = "Nombre del platillo")
                }
            }
        }

        // Botón mandar a cocina
        Button(
            onClick = { /* Acción */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .height(65.dp)
                .fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = yellow),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mandar a cocina",
                    color = brown,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = brown,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdenScreenPreview() {
    PompompurinCafeTheme {
        ResumenOrdenScreen()
    }
}