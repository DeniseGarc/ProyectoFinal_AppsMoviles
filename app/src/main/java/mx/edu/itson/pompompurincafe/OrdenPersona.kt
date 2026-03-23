package mx.edu.itson.pompompurincafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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

class OrdenPersona : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PompompurinCafeTheme {
                    OrdenPersonaScreenPreview()
            }
        }
    }
}

@Composable
fun OrdenPersonaListaPlatillos(cantidad: Int, nombre: String) {
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
fun PersonaHeader(numero: Int, nombre: String) {
    Row(
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cuadro marrón con número
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(brown, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("$numero",
                color = white,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))

        // Nombre de la persona
        Text("$nombre",
            color = brown,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp)

        Spacer(modifier = Modifier.width(8.dp))

        // Icono Editar con fondo amarillo
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(yellow, RoundedCornerShape(8.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Edit,
                null,
                tint = brown,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


@Composable
fun ResumenOrdenPersonaScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        Fondo()

        Column(modifier = Modifier.fillMaxSize()) {
            HeaderBanner()

            // Info mesa
            InfoMesa(12,4,142.50)

            // Agregar cliente formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(containerColor = brown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Titulo
                    Text(
                        text = "Agregar persona",
                        color = yellow,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )

                    // Subtitulo
                    Text(
                        text = "Nombre del cliente:",
                        color = yellow,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Campo de texto blanco
                        BasicTextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier
                                .weight(1f)
                                .height(35.dp)
                                .background(white, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Botón circular amarillo con el "+"
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .background(yellow, CircleShape)
                                .clickable { /* Acción */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, "añadir", tint = white)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp) // Espacio para el botón de abajo
            ){

                items(3) {
                    PersonaHeader(1, "Juan")
                    OrdenPersonaListaPlatillos(1, "Nombre del platillo")
                    OrdenPersonaListaPlatillos(1, "Nombre del platillo")
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
fun OrdenPersonaScreenPreview() {
    PompompurinCafeTheme {
        ResumenOrdenPersonaScreen()
    }
}