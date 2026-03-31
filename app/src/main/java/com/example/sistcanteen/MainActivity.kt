package com.example.sistcanteen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sistcanteen.models.UserRole
import com.example.sistcanteen.screens.AdminScreen
import com.example.sistcanteen.screens.LoginScreen
import com.example.sistcanteen.screens.StaffScreen
import com.example.sistcanteen.screens.StudentScreen
import com.example.sistcanteen.ui.theme.SISTCANTEENTheme
import com.example.sistcanteen.viewmodel.CanteenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SISTCANTEENTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: CanteenViewModel = viewModel()
    val currentUser = viewModel.currentUser.value

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel = viewModel) {
                val role = viewModel.currentUser.value?.role
                when (role) {
                    UserRole.STUDENT -> navController.navigate("student") {
                        popUpTo("login") { inclusive = true }
                    }
                    UserRole.STAFF -> navController.navigate("staff") {
                        popUpTo("login") { inclusive = true }
                    }
                    UserRole.ADMIN -> navController.navigate("admin") {
                        popUpTo("login") { inclusive = true }
                    }
                    null -> {}
                }
            }
        }
        composable("student") {
            StudentScreen(viewModel = viewModel) {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("student") { inclusive = true }
                }
            }
        }
        composable("staff") {
            StaffScreen(viewModel = viewModel) {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("staff") { inclusive = true }
                }
            }
        }
        composable("admin") {
            AdminScreen(viewModel = viewModel) {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("admin") { inclusive = true }
                }
            }
        }
    }
}
