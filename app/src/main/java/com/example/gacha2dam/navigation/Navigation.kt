package com.example.gacha2dam.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gacha2dam.screens.collection.CollectionScreen
import com.example.gacha2dam.screens.login.LoginScreen
import com.example.gacha2dam.screens.principal.PrincipalScreen
import com.example.gacha2dam.screens.register.RegisterScreen
import com.example.gacha2dam.screens.splash.SplashScreen
import com.example.gacha2dam.screens.detalles.PersonajeDetalleScreen


@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name){
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(Screens.PrincipalScreen.name) {
            PrincipalScreen(navController = navController)
        }
        composable(Screens.RegisterScreen.name) {
            RegisterScreen(navController = navController)
        }
        composable(Screens.CollectionScreen.name) {
            CollectionScreen(navController = navController)
        }
        composable("${Screens.PersonajeDetalleScreen.name}/{personajeId}") { backStackEntry ->
            val personajeId = backStackEntry.arguments?.getString("personajeId") ?: ""
            PersonajeDetalleScreen(personajeId = personajeId, navController = navController)
        }
    }
}
