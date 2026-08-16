package com.example.fixitapplication.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object ReportDamage : Screen("report_damage")
    object Profile : Screen("profile")
    object AdminDashboard : Screen("admin_dashboard")
}
