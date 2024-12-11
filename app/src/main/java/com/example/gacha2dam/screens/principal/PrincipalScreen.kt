package com.example.gacha2dam.screens.principal

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gacha2dam.model.Personaje
import com.example.gacha2dam.navigation.Screens
import com.example.gacha2dam.screens.login.LoginScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PrincipalScreen(navController: NavController, viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Estados para el usuario
    val userName = remember { mutableStateOf("Cargando...") }
    val userProgress = remember { mutableStateOf(0f) }

    // Estados para el pop-up
    val showPopup = remember { mutableStateOf(false) }
    val personajeObtenido = remember { mutableStateOf<Personaje?>(null) }

    // Obtener el usuario actual
    val user = auth.currentUser
    val userId = user?.uid

    // Efecto lateral para cargar el nombre del usuario desde Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            // Obtener el nombre del usuario
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("displayName") ?: "Usuario"
                    userName.value = name
                }
                .addOnFailureListener {
                    userName.value = "Error al cargar"
                }

            // Obtener el progreso de los personajes obtenidos
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val pjsObtenidos = document.get("pjsObtenidos") as? Map<String, Boolean> ?: emptyMap()

                    // Contar cuántos personajes han sido obtenidos (true)
                    val totalPersonajes = pjsObtenidos.size
                    val personajesObtenidos = pjsObtenidos.values.count { it }

                    // Calcular el porcentaje
                    if (totalPersonajes > 0) {
                        userProgress.value = personajesObtenidos.toFloat() / totalPersonajes
                    }
                }
        } else {
            userName.value = "Usuario no autenticado"
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Botón de cerrar sesión en la esquina superior derecha
        IconButton(
            onClick = {
                auth.signOut()
                navController.navigate(Screens.LoginScreen.name) {
                    popUpTo(Screens.PrincipalScreen.name) { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                tint = MaterialTheme.colorScheme.onError
            )
        }

        // Texto clicable para ir al perfil en la esquina superior izquierda
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), // Aumenta la legibilidad
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), // Color más brillante y menos opaco
            modifier = Modifier
                .align(Alignment.TopStart)
                .clickable {
                    // Navegar a la pantalla de perfil o mostrar un pequeño recuadro con los datos (no implementado)
                }
                .padding(8.dp) // Agrega espacio alrededor del texto para mejorar la interacción
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(4.dp)) // Fondo suave para mejor visibilidad
                .padding(horizontal = 16.dp, vertical = 8.dp), // Ajuste del área tocable
            textDecoration = TextDecoration.Underline
        )
        Text(
            text = "Hola, ${userName.value}!",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-150).dp)
                .animateContentSize()
        )
        Text(
            text = "Tu porcentaje de obtencion de los personajes es:",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp)
                .animateContentSize()
        )
        //Barra de progreso
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = userProgress.value,
                strokeWidth = 8.dp,
                modifier = Modifier
                    .size(150.dp)
                    .shadow(8.dp, shape = CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
            )
            Text(
                text = "${(userProgress.value * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Botón "Obtener compañeros" debajo de la barra de progreso
        Button(
            onClick = {
                // Generar rareza basada en probabilidades
                val randomValue = (1..100).random()
                val rareza = when {
                    randomValue == 1 -> "ur"
                    randomValue <= 11 -> "sr"
                    else -> "r"
                }

                // Consultar Firestore para obtener personajes de la rareza seleccionada
                db.collection("personajes")
                    .whereEqualTo("rareza", rareza)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val personajes = querySnapshot.documents.mapNotNull { document ->
                            document.toObject(Personaje::class.java)?.copy(idPJ = document.id)
                        }

                        if (personajes.isNotEmpty()) {
                            // Seleccionar un personaje al azar
                            val personajeSeleccionado = personajes.random()
                            personajeObtenido.value = personajeSeleccionado

                            // Actualizar el contenedor `pjsObtenidos` del usuario
                            if (userId != null) {
                                db.collection("users").document(userId)
                                    .update("pjsObtenidos.${personajeSeleccionado.idPJ}", true)
                                    .addOnSuccessListener {
                                        showPopup.value = true // Mostrar el pop-up al usuario
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("Firestore", "Error actualizando pjsObtenidos: ${exception.message}")
                                    }
                            }
                        } else {
                            Log.e("Firestore", "No se encontraron personajes de rareza $rareza")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error obteniendo personajes: ${exception.message}")
                    }
            },
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 130.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(50))
                .shadow(8.dp, RoundedCornerShape(50)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Obtener compañeros",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        // Pop-up para mostrar el personaje obtenido
        if (showPopup.value) {
            AlertDialog(
                onDismissRequest = { showPopup.value = false },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                if (userId != null) {
                                    db.collection("users").document(userId).get()
                                        .addOnSuccessListener { document ->
                                            val pjsObtenidos = document.get("pjsObtenidos") as? Map<String, Boolean> ?: emptyMap()

                                            // Contar cuántos personajes han sido obtenidos (true)
                                            val totalPersonajes = pjsObtenidos.size
                                            val personajesObtenidos = pjsObtenidos.values.count { it }

                                            // Calcular el porcentaje
                                            if (totalPersonajes > 0) {
                                                userProgress.value = personajesObtenidos.toFloat() / totalPersonajes
                                            }
                                        }
                                }
                                showPopup.value = false
                            },
                            modifier = Modifier.fillMaxWidth(0.5f), // Reducir el tamaño del botón
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Cerrar", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
                title = {
                    Text(
                        text = "¡Personaje Obtenido!",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    personajeObtenido.value?.let { personaje ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Nombre del personaje
                            personaje.nombrePJ?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.headlineMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            // Espaciado entre elementos
                            Spacer(modifier = Modifier.height(16.dp))

                            // Imagen del personaje
                            AsyncImage(
                                model = personaje.urlImagen,
                                contentDescription = personaje.nombrePJ,
                                modifier = Modifier
                                    .size(150.dp)
                                    // Animación al mostrar la imagen
                                    .graphicsLayer {
                                        alpha = 1f
                                        scaleX = 1.2f
                                        scaleY = 1.2f
                                    }
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            // Espaciado entre la imagen y el texto
                            Spacer(modifier = Modifier.height(16.dp))
                            // Rareza del personaje
                            Text(
                                text = "Rareza: ${personaje.rareza}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            )
        }

    }
}

