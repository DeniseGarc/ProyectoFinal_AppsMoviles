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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import mx.edu.itson.pompompurincafe.data.FirebaseManager
import mx.edu.itson.pompompurincafe.model.Orden
import mx.edu.itson.pompompurincafe.ui.theme.*

/**
 * Actividad encargada de registrar una nueva comanda en la cafetería.
 * Permite al mesero elegir el número de mesa y especificar la modalidad de cobro de la cuenta.
 */
class NuevaOrden : ComponentActivity() {

    /**
     * Inicializa la pantalla de nueva orden.
     */
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

/**
 * Componente principal que estructura la pantalla de creación de órdenes.
 * Carga el fondo general de la aplicación y conecta los elementos visuales con la base de datos.
 */
@Composable
fun NuevaOrdenScreen() {

    // Estados locales para rastrear la mesa seleccionada y la opción de cuenta elegida
    var tipoSeleccionado by remember { mutableIntStateOf(0) }
    var mesaSeleccionada by remember { mutableIntStateOf(0) }
    var ordenesActivas by remember { mutableStateOf<List<Orden>>(emptyList()) }

    // Trae las órdenes vigentes desde Firebase para saber qué mesas están ocupadas actualmente
    LaunchedEffect(Unit) {
        FirebaseManager.suscribirseAOrdenes { ordenes ->
            ordenesActivas = ordenes.filter { !it.pagada }
        }
    }

    // Filtra las mesas con cuentas abiertas para deshabilitar su selección
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

            // Formulario principal con la cuadrícula de mesas y botones de tipo de orden
            NuevaOrdenCard(
                seleccionado = tipoSeleccionado,
                onSeleccionCambiada = { tipoSeleccionado = it },
                mesaSeleccionada = mesaSeleccionada,
                onMesaChange = { mesaSeleccionada = it },
                mesasOcupadas = mesasOcupadas
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de confirmación para guardar el registro final en Firebase
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

/**
 * Contenedor visual que incluye los controles de selección de la comanda.
 * Muestra las mesas en formato de cuadrícula interactiva y las opciones de tipo de cuenta.
 */
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

            // Cuadrícula que distribuye las 15 mesas del establecimiento
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items((1..15).toList()) { numero ->
                    val estaOcupada = mesasOcupadas.contains(numero)
                    val esEstaSeleccionada = mesaSeleccionada == numero

                    // Asigna el color del botón según la disponibilidad y el clic del usuario
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

            // Selector horizontal para definir si la cuenta es general o dividida
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(white)
            ) {
                // Opción para cobrar todos los productos juntos
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

                // Opción para separar el consumo individualmente
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

/**
 * Botón que procesa la creación de la comanda.
 * Valida los datos requeridos, extrae el mesero autenticado y crea el documento en Firebase.
 */
@Composable
fun BotonNuevaOrden(tipo: Int, mesa: Int, mesasOcupadas: Set<Int>) {
    val context = LocalContext.current
    val auth = Firebase.auth

    Button(
        onClick = {
            // Verifica que se haya elegido una mesa antes de continuar
            if (mesa != 0) {
                if (mesasOcupadas.contains(mesa)) {
                    Toast.makeText(context, "La mesa $mesa ya no está disponible", Toast.LENGTH_SHORT).show()
                } else {
                    // Mapea la selección interna con las etiquetas de la base de datos
                    val etiqueta = if (tipo == 0) "mesa" else "individual"
                    val usuarioActual = auth.currentUser?.email ?: "Desconocido"

                    // Crea el modelo con la información básica inicial
                    val nuevaOrden = Orden(
                        mesa = mesa,
                        tipoCuenta = etiqueta,
                        mesero = usuarioActual
                    )

                    // Guarda la orden en la base de datos y redirige al flujo que corresponda
                    FirebaseManager.guardarOrden(
                        nuevaOrden,
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
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(60.dp)
    ) {
        Text(
            text = "Tomar orden",
            color = yellow,
            fontSize = 18.sp,
            fontFamily = CustomFontFamily
        )
    }
}

/**
 * Componente decorativo que ubica la ilustración de la marca en la base de la pantalla.
 */
@Composable
fun FooterPompompurin() {
    Image(
        painter = painterResource(id = R.drawable.nuevaorden_pompompurin),
        contentDescription = "Pompompurin",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

/**
 * Vista previa de la pantalla de nueva orden.
 */
@Preview(showBackground = true)
@Composable
fun NuevaOrdenScreenPreview() {
    PompompurinCafeTheme {
        NuevaOrdenScreen()
    }
}