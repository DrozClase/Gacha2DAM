package com.example.gacha2dam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gacha2dam.navigation.BottomNavigationBar
import com.example.gacha2dam.navigation.Navigation
import com.example.gacha2dam.navigation.Screens
import com.example.gacha2dam.ui.theme.Gacha2DAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Gacha2DAMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val currentBackStack by navController.currentBackStackEntryAsState()
                    val currentDestination = currentBackStack?.destination?.route

                    val showBottomBar = currentDestination in listOf(
                        Screens.PrincipalScreen.name,
                        Screens.CollectionScreen.name
                    )

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavigationBar(navController)
                            }
                        }
                    ) {innerPadding ->
                        // Aplicamos el padding a Navigation
                        Box(modifier = Modifier.padding(innerPadding)) {
                            Navigation(navController = navController)
                        }
                    }
                }
            }
        }
    }
}