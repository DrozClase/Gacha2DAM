package com.example.gacha2dam.screens.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gacha2dam.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CollectionScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val user = auth.currentUser
    val userId = user?.uid
    val personajes = remember { mutableStateOf<List<Personaje>>(emptyList()) }
    val miColeccion = remember { mutableStateOf(false) } // Estado del toggle button
    val pjsObtenidos = remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    // Verificamos que userId no sea nulo
    if (userId != null) {
        // Cargar personajes de la colección "personajes"
        LaunchedEffect(Unit) {
            db.collection("personajes").get()
                .addOnSuccessListener { querySnapshot ->
                    val lista = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Personaje::class.java)?.copy(id = document.id)
                    }
                    personajes.value = lista
                }
        }

        // Cargar personajes obtenidos del usuario
        LaunchedEffect(Unit) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val userPjsObtenidos = documentSnapshot.get("pjsObtenidos") as? Map<String, Boolean>
                    pjsObtenidos.value = userPjsObtenidos ?: emptyMap()
                }
        }
    } else {
        // Si no hay userId (usuario no autenticado), puedes manejar este caso mostrando un mensaje o redirigiendo a otra pantalla
        Text("Por favor, inicia sesión para ver tu colección.")
    }

    Column {
        // Botón de estilo toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { miColeccion.value = !miColeccion.value },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (miColeccion.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = if (miColeccion.value) "Mi Colección" else "Colección Completa",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Mostrar personajes filtrados según el estado del botón toggle
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Esto hace que haya dos elementos por fila
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp) // Añadimos algo de padding para que no se vean pegados
        ) {
            val personajesFiltrados = if (miColeccion.value) {
                personajes.value.filter { pjsObtenidos.value[it.id] == true }
            } else {
                personajes.value
            }

            items(personajesFiltrados) { personaje ->
                PersonajeCard(personaje = personaje, navController)
            }
        }
    }
}

// Composable para mostrar un personaje con estilo de carta
@Composable
fun PersonajeCard(personaje: Personaje, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Padding entre tarjetas
            .clip(RoundedCornerShape(12.dp)) // Bordes más redondeados para el estilo de carta
            .background(MaterialTheme.colorScheme.surface)
            .shadow(8.dp, RoundedCornerShape(12.dp)) // Sombra para dar profundidad
            .clickable {
                // Navegar a la pantalla de detalles pasando el id del personaje
                navController.navigate("${Screens.PersonajeDetalleScreen.name}/${personaje.id}")
            }
    ) {
        // Imagen del personaje
        AsyncImage(
            model = personaje.urlImagen,
            contentDescription = personaje.nombrePJ,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)) // Borde redondeado de la imagen
                .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp)), // Borde alrededor de la imagen
            contentScale = ContentScale.Fit // Ajusta la imagen sin recortarla
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre del personaje debajo de la imagen con fondo sutil
        Text(
            text = personaje.nombrePJ,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(4.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface // Aseguramos que el texto sea visible
        )
    }
}


// Modelo de datos para un personaje
data class Personaje(
    val id: String = "",
    val nombrePJ: String = "",
    val urlImagen: String = ""
)

