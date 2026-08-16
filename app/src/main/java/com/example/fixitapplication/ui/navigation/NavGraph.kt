package com.example.fixitapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fixitapplication.ui.screens.AdminDashboardScreen
import com.example.fixitapplication.ui.screens.ForgotPasswordScreen
import com.example.fixitapplication.ui.screens.HomeScreen
import com.example.fixitapplication.ui.screens.LoginScreen
import com.example.fixitapplication.ui.screens.ProfileScreen
import com.example.fixitapplication.ui.screens.RegisterScreen
import com.example.fixitapplication.ui.screens.ReportDamageScreen
import com.example.fixitapplication.ui.screens.SplashScreen

@Composable
fun FixItNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = if (role == "admin") Screen.AdminDashboard.route else Screen.Home.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToReport = {
                    navController.navigate(Screen.ReportDamage.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.ReportDamage.route) {
            ReportDamageScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
