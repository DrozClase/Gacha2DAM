package com.example.gacha2dam.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gacha2dam.navigation.Screens
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
        navController: NavController,
        viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

        // Estados para los campos de texto
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }

        // Estado para mostrar errores
        val token = "197002281160-a8hlpqqedtlr2lffed3ie9ismsh5k2op.apps.googleusercontent.com"
        val context = LocalContext.current
        val loginError = remember { mutableStateOf<String?>(null) }
        val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()

        ) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                        val account = task.getResult(ApiException::class.java)
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        viewModel.loginWithGoogle(credential) { success, error ->
                                if (success) {
                                        navController.navigate(Screens.PrincipalScreen.name)
                                } else {
                                        loginError.value = error
                                }
                        }
                } catch (ex: Exception) {
                        Log.d("LoginScreen", "GoogleSignIn falló")
                }
        }

        // Fondo
        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .background(
                                Brush.verticalGradient(
                                        colors = listOf(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                MaterialTheme.colorScheme.secondaryContainer
                                        )
                                )
                        ),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Encabezado
                        Text(
                                text = "Bienvenido a Gacha2DAM",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                ),
                                textAlign = TextAlign.Center
                        )

                        Text(
                                text = "Inicia sesión para continuar",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                ),
                                textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de correo
                        OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Correo electrónico") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                        errorIndicatorColor = MaterialTheme.colorScheme.error
                                )
                        )

                        // Campo de contraseña
                        OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Contraseña") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                        errorIndicatorColor = MaterialTheme.colorScheme.error
                                )
                        )

                        // Botón de iniciar sesión
                        Button(
                                onClick = {
                                        when {
                                                email.isBlank() -> loginError.value = "Por favor, ingresa tu correo electrónico."
                                                password.isBlank() -> loginError.value = "Por favor, ingresa tu contraseña."
                                                else -> {
                                                        viewModel.login(email, password) { success, error ->
                                                                if (success) {
                                                                        navController.navigate(Screens.PrincipalScreen.name)
                                                                } else {
                                                                        loginError.value = error ?: "Error desconocido"
                                                                }
                                                        }
                                                }
                                        }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                        ) {
                                Text("Iniciar sesión")
                        }

                        // Botón de iniciar sesión con Google
                        OutlinedButton(
                                onClick = {
                                        val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(token)
                                                .requestEmail()
                                                .build()
                                        val googleSignInClient = GoogleSignIn.getClient(context, opciones)
                                        launcher.launch(googleSignInClient.signInIntent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.secondary
                                )
                        ) {
                                Text("Iniciar sesión con Google")
                        }

                        // Enlaces de texto
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Text(
                                        text = "¿Olvidaste tu contraseña?",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable { },
                                        textDecoration = TextDecoration.Underline
                                )
                                Text(
                                        text = "Registrarse",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.clickable {
                                                navController.navigate(Screens.RegisterScreen.name)
                                        },
                                        textDecoration = TextDecoration.Underline
                                )
                        }

                        // Mensaje de error
                        loginError.value?.let { errorMessage ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text = errorMessage,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                }
        }
}




