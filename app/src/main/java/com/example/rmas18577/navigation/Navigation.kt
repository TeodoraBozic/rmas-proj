package com.example.rmasprojekat18723.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {

    object SignUpScreen : Screen()
    object LogInScreen : Screen()
    object HomePage : Screen()
    object MainPage: Screen()
}


object Navigator {

    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.HomePage)

    fun navigateTo(screen : Screen){
        currentScreen.value = screen
    }


}