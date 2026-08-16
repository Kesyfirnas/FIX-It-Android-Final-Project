package com.example.fixitapplication.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fixitapplication.R
import com.example.fixitapplication.ui.navigation.Screen
import com.example.fixitapplication.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigate: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val alpha = remember { Animatable(0f) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1000)
        viewModel.checkAutoLogin()
    }

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            val destination = if (uiState.role == "admin") Screen.AdminDashboard.route else Screen.Home.route
            onNavigate(destination)
            viewModel.resetState()
        } else if (uiState.isNotLoggedIn || uiState.error != null) {
            onNavigate(Screen.Login.route)
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "Logo FixIt",
            modifier = Modifier
                .size(200.dp)
                .alpha(alpha.value)
        )
    }
}
