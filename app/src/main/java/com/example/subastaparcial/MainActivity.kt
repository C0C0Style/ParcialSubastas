package com.example.subastaparcial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.subastaparcial.ui.subastaslist.SubastaListScreen
import com.example.subastaparcial.ui.crearsubasta.CrearSubastaScreen
import com.example.subastaparcial.ui.subastadetalle.DetalleSubastaScreen
import com.example.subastaparcial.viewmodel.SubastaListViewModel
import com.example.subastaparcial.ui.theme.SubastaParcialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubastaParcialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: SubastaListViewModel = viewModel() // Instancia única

    NavHost(
        navController = navController,
        startDestination = "subastasList"
    ) {
        composable("subastasList") {
            SubastaListScreen(navController, viewModel) // ✅ lo pasas
        }

        composable("crearSubasta") {
            CrearSubastaScreen(navController)
        }

        composable("detalleSubasta") {
            val subasta = viewModel.subastaSeleccionada
            if (subasta != null) {
                DetalleSubastaScreen(subasta)
            } else {
                Text("Subasta no encontrada")
            }
        }
    }
}

