package com.example.rmas18577.app


import MainPage
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.rmas18577.data.home.HomeViewModel
import com.example.rmas18577.screens.HomePage
import com.example.rmas18577.screens.LoginScreen
import com.example.rmas18577.screens.SignUpScreen
import com.example.rmasprojekat18723.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.screens.MapScreen
import com.example.rmas18577.screens.UsersInfo
import com.example.rmas18577.screens.ViewObjects
import com.example.rmasprojekat18723.data.SignupViewModel
import com.example.rmasprojekat18723.navigation.Navigator


@Composable
fun RunTogetherApp(
    homeViewModel: HomeViewModel = viewModel(),
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit
) {
    // Provera da li je korisnik ulogovan
    homeViewModel.checkForActiveSession()


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        // Ako je početni ekran `HomePage`, koristimo ga direktno
        val currentScreen by remember { Navigator.currentScreen }

        // Ako `currentScreen` nije postavljen, inicijalizuj na `HomePage`
        LaunchedEffect(currentScreen) {
            if (currentScreen == null) {
                Navigator.navigateTo(Screen.HomePage)
            }
        }

        // Prati promene u currentScreen
        Crossfade(targetState = currentScreen) { screen ->
            when (screen) {
                is Screen.SignUpScreen -> {
                    SignUpScreen()
                }
                is Screen.LogInScreen -> {
                    LoginScreen()
                }
                is Screen.UsersInfo-> {
                    UsersInfo(signUpViewModel = SignupViewModel())
                }
                is Screen.HomePage -> {
                    HomePage()
                }
                is Screen.MainPage -> {
                        MainPage(
                        mapClick = {
                            Navigator.navigateTo(Screen.MapScreen)
                        }
                    )
                }
                is Screen.ViewObjects -> {
                    ViewObjects()
                }
                is Screen.MapScreen -> {
                    MapScreen(
                        onSuccess = {}
                    )
                }


            }
        }


    }
}



