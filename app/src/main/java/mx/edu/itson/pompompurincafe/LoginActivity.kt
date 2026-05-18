package mx.edu.itson.pompompurincafe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import mx.edu.itson.pompompurincafe.ui.theme.CustomFontFamily
import mx.edu.itson.pompompurincafe.ui.theme.PompompurinCafeTheme
import mx.edu.itson.pompompurincafe.ui.theme.brown
import mx.edu.itson.pompompurincafe.ui.theme.white
import mx.edu.itson.pompompurincafe.ui.theme.yellow

/**
 * Actividad encargada de gestionar el flujo de autenticación de usuarios.
 * Inicializa los servicios de Firebase Auth y renderiza la interfaz gráfica de inicio de sesión.
 */
class LoginActivity : ComponentActivity() {

    // Instancia encargada del manejo de sesiones y autenticación con Firebase. */
    private lateinit var auth: FirebaseAuth

    /**
     * Inicializa la pantalla de login.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        setContent {
            PompompurinCafeTheme {
                LoginScreen(
                    onLogin = { email, password, onResult ->
                        // Petición asíncrona a Firebase Auth para verificar el usuario.
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                onResult(task.isSuccessful, task.exception?.message)
                            }
                    }
                )
            }
        }
    }
}

/**
 * Componente de interfaz de usuario para el formulario de acceso.
 * Administra el estado de los campos de texto, las alertas visuales y la redirección de pantallas.
 */
@Composable
fun LoginScreen(
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    val context = LocalContext.current

    // Estados para almacenar y validar las entradas de la interfaz.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Fondo()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elementos gráficos de la cabecera e identidad de la marca.
            Image(
                painter = painterResource(id = R.drawable.ordenes2_pompompurin),
                contentDescription = "Logo Pompompurin",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "POMPOMPURIN café",
                color = brown,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CustomFontFamily,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¡El lugar más dulce para tus antojos!",
                color = brown,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Campo de captura para el identificador de usuario (correo).
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(0.85f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = brown,
                    unfocusedBorderColor = brown,
                    focusedLabelColor = brown,
                    unfocusedLabelColor = brown,
                    focusedContainerColor = white,
                    unfocusedContainerColor = white
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de captura cifrado para la credencial de seguridad (contraseña).
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(0.85f),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = brown,
                    unfocusedBorderColor = brown,
                    focusedLabelColor = brown,
                    unfocusedLabelColor = brown,
                    focusedContainerColor = white,
                    unfocusedContainerColor = white
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Control de flujo visual para alternar entre el indicador de carga y el botón de acción.
            if (isLoading) {
                CircularProgressIndicator(color = brown)
            } else {
                Button(
                    onClick = {
                        // Validación local de campos obligatorios previo al envío de datos.
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            onLogin(email, password) { success, errorMsg ->
                                isLoading = false
                                if (success) {
                                    // Finaliza la actividad actual y navega al menú principal si el Login es exitoso.
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                    (context as? ComponentActivity)?.finish()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error: ${errorMsg ?: "Error desconocido"}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brown),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Ingresar",
                        color = yellow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enlace interactivo para redirigir al flujo de registro de nuevos usuarios.
            Text(
                text = "¿No tienes una cuenta? Regístrate aquí",
                color = brown,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    val intent = Intent(context, RegisterActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

/**
 * Vista previa del LoginScreen.
 */
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PompompurinCafeTheme {
        LoginScreen(onLogin = { _, _, _ -> })
    }
}