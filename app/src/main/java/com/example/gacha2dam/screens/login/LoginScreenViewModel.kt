package com.example.gacha2dam.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gacha2dam.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Iniciar sesión con email y contraseña
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message ?: "Error desconocido")
                    }
                }
        }
    }

    // Iniciar sesión con Google (implementar lógica de Google SignIn)
    fun loginWithGoogle(credential: AuthCredential, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""
                            val displayName = auth.currentUser?.displayName ?: ""

                            // Verificar si el usuario ya existe en Firestore
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // Usuario ya existe, continuar con el inicio de sesión
                                        onResult(true, null)
                                    } else {
                                        // Usuario no existe, crear en Firestore
                                        createUserInFirestore(userId, displayName) { success, error ->
                                            if (success) {
                                                onResult(true, null) // Usuario creado y logeado
                                            } else {
                                                onResult(false, error) // Error al crear usuario
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    onResult(false, exception.message ?: "Error al verificar usuario")
                                }
                        } else {
                            onResult(false, task.exception?.message ?: "Error desconocido")
                        }
                    }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Error desconocido")
            }
        }
    }


    fun register(username: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    createUserInFirestore(userId, username) { success, error ->
                        if (success) {
                            callback(true, null) // Usuario creado con éxito
                        } else {
                            callback(false, error) // Fallo al guardar en Firestore
                        }
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    private fun createUserInFirestore(userId: String, displayName: String?, callback: (Boolean, String?) -> Unit) {
        val db = FirebaseFirestore.getInstance() // Inicializamos la base de datos de Firebase

        db.collection("personajes").get()
            .addOnSuccessListener { querySnapshot ->
                // Crear un mapa con los IDs de los personajes y asignarles "false"
                val personajesMap = hashMapOf<String, Boolean>()
                for (document in querySnapshot.documents) {
                    personajesMap[document.id] = false
                }
        val user = User(
            userId = userId,
            displayName = displayName,
            pjsObtenidos = personajesMap // Inicializamos con un mapa vacío
        )

        // Guardamos el usuario en la colección "users"
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                callback(true, null) // Éxito
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message) // Error
            }
        }
    }
}
