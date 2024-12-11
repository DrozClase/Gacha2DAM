package com.example.gacha2dam.screens.detalles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gacha2dam.model.Personaje
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PersonajeDetalleScreen(personajeId: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val personaje = remember { mutableStateOf<Personaje?>(null) }

    // Cargar los detalles del personaje desde Firestore
    LaunchedEffect(personajeId) {
        db.collection("personajes").document(personajeId).get()
            .addOnSuccessListener { documentSnapshot ->
                val personajeDetails = documentSnapshot.toObject(Personaje::class.java)
                personaje.value = personajeDetails
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (personaje.value == null) {
            // Indicador de carga
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Mostrar detalles del personaje
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Nombre del personaje (arriba, destacado)
                Text(
                    text = personaje.value?.nombrePJ ?: "",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Espaciado entre el nombre y la imagen
                Spacer(modifier = Modifier.height(32.dp)) // Espaciado más grande

                // Imagen del personaje (más grande, con borde y sombra)
                AsyncImage(
                    model = personaje.value?.urlImagen,
                    contentDescription = personaje.value?.nombrePJ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp) // Imagen más grande
                        .clip(RoundedCornerShape(16.dp)) // Bordes redondeados más suaves
                        .shadow(8.dp, RoundedCornerShape(16.dp)), // Sombra para destacar
                    contentScale = ContentScale.Fit // Escalar la imagen para que no se corte
                )

                // Espaciado entre imagen y descripción
                Spacer(modifier = Modifier.height(16.dp))

                // Rareza del personaje
                Text(
                    text = "Rareza: ${personaje.value?.rareza ?: "Desconocida"}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Descripción del personaje
                Text(
                    text = personaje.value?.descripcion ?: "Sin descripción disponible.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Botón para volver a la pantalla anterior (más prominente)
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart) // Movemos el icono más abajo
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape) // Fondo sutil
                .padding(10.dp) // Aumentamos el tamaño del ícono
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


