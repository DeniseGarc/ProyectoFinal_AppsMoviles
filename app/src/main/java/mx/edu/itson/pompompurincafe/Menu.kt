package mx.edu.itson.pompompurincafe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import mx.edu.itson.pompompurincafe.ui.theme.CustomFontFamily
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.lightyellow
import mx.edu.itson.pompompurincafe.ui.theme.lightyellow2
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow

class Menu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                MenuScreen()
            }
        }
    }
}

@Composable
fun PlatilloCard(imagen: Int, nombre: String, descripcion: String, precio: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, brown)
    ) {
        Column {

            // Contenedor de la Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                // Imagen del producto
                Image(
                    painter = painterResource(imagen),
                    contentDescription = "$nombre",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Información del platillo
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Nombre del producto
                    Text("$nombre",
                        fontWeight = FontWeight.SemiBold,
                        color = brown
                    )

                    // Precio del producto
                    Text("$precio",
                        fontWeight = FontWeight.SemiBold,
                        color = brown
                    )
                }

                // Descripción del producto
                Text("$descripcion",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = brown)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Contador de cantidad
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(lightyellow2, RoundedCornerShape(10.dp))
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        // Botón disminuir
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = lightyellow),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("-",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = brown
                            )
                        }

                        // Cantidad
                        Text("0",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = brown,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        // Botón aumentar
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = lightyellow),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = brown,
                            )
                        }
                    }

                    // Botón Agregar
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = brown),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text("Agregar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = CustomFontFamily,
                            color = yellow)
                    }
                }
            }
        }
    }
}


@Composable
fun Resumen(cantidadPlatillos: Int, total: Double, propietario: String){
    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total",
                    color = brown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(20.dp))

                // Cantidad de platillos ordenados
                Text("$cantidadPlatillos platillos",
                    fontSize = 16.sp,
                    color = brown
                )
            }

            // Total de la orden
            Text("$$total",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = brown
            )

            // Propietario de la orden
            Text("Orden de: $propietario",
                fontSize = 16.sp,
                color = brown
            )
        }

        Spacer(modifier = Modifier.weight(1f).height(1.dp))

        // Botón revisar orden
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(context, OrdenPersona::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = brown),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(60.dp)
                .padding(start= 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Revisar\nOrden",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = CustomFontFamily,
                    color = white)

                Spacer(modifier = Modifier.width(8.dp))

                Icon(Icons.Default.ArrowForward,
                    contentDescription = "Icono de flecha",
                    tint = white
                )
            }
        }
    }

}

@Composable
fun MenuScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de la pantalla
        Fondo()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            HeaderBanner()

            // Filtros (Categorías)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filtro de todos los platillos
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clickable(){/* Acción */ }
                        .background(brown, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = white)
                }

                // Filtros de categoria
                val categorias = listOf("Platillos", "Bebidas", "Postres")
                categorias.forEach { cat ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .clickable(){/* Acción */ }
                            .background(yellow, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cat, fontWeight = FontWeight.Bold, color = brown)
                    }
                }
            }

            // Título de los filtros
            Text(
                "Todos",
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                color = brown,
                fontSize = 20.sp
            )

            // Lista de platillos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp) // Espacio para el banner inferior
            ) {
                // platillos de ejemplo
                items(4) {
                    PlatilloCard(R.drawable.ordenes_pompompurin,"Nombre del platillo...","Descripción...","$000.00")
                }
            }
        }

        // Banner inferior con el resumen de la orden
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = yellow),
            shape = RoundedCornerShape(16.dp)
        ) {
            Resumen(5,142.50,"Mesa 12")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    PompompurinCafeTheme {
        MenuScreen()
    }
}
