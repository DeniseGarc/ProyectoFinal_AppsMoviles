package mx.edu.itson.pompompurincafe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
 * Actividad encargada de dar de alta a nuevos usuarios en el sistema.
 * Coordina la comunicación con Firebase Auth para el proceso de creación de credenciales.
 */
class RegisterActivity : ComponentActivity() {

    /** Instancia encargada de gestionar los servicios de autenticación y registros de Firebase. */
    private lateinit var auth: FirebaseAuth

    /**
     * Inicializa la pantalla de registro.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        setContent {
            PompompurinCafeTheme {
                RegisterScreen(
                    onRegister = { email, password, onResult ->
                        // Petición a Firebase para registrar un nuevo correo y contraseña.
                        auth.createUserWithEmailAndPassword(email, password)
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
 * Componente de interfaz de usuario que despliega el formulario de registro.
 * Maneja los estados locales de los campos, los mensajes de error y las reglas de validación.
 */
@Composable
fun RegisterScreen(
    onRegister: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    // Estados reactivos para capturar el texto ingresado y controlar el estado de carga.
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Registro",
                color = brown,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CustomFontFamily,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Entrada para el correo electrónico del nuevo usuario.
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

            // Entrada de seguridad para asignar la contraseña de la cuenta.
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

            Spacer(modifier = Modifier.height(16.dp))

            // Entrada de seguridad secundaria para validar la contraseña ingresada previamente.
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
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

            // Alternancia visual entre el indicador de progreso y el botón de acción principal.
            if (isLoading) {
                CircularProgressIndicator(color = brown)
            } else {
                Button(
                    onClick = {
                        // Capa de validación local (campos llenos, coincidencia y longitud de caracteres).
                        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                            if (password == confirmPassword) {
                                if (password.length >= 6) {
                                    isLoading = true
                                    onRegister(email, password) { success, errorMsg ->
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                            // Cierra la pantalla actual y regresa al flujo previo (Login).
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
                                    Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
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
                        text = "Registrarse",
                        color = yellow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CustomFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opción interactiva para cancelar el registro y retornar al inicio de sesión.
            TextButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = brown,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Vista previa del RegisterScreen.
 */
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    PompompurinCafeTheme {
        RegisterScreen(onRegister = { _, _, _ -> })
    }
}