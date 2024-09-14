package com.example.rmas18577.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmas18577.components.ButtonComponent
import com.example.rmas18577.components.ClickableTextLogin
import com.example.rmas18577.components.LoginOrRegisterButtons
import com.example.rmasprojekat18723.data.LoginUIEvent
import com.example.rmasprojekat18723.data.LoginViewModel
import com.example.rmasprojekat18723.data.SignupUIEvent
import com.example.rmasprojekat18723.data.SignupViewModel
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen

@Composable
fun HomePage(loginViewModel: LoginViewModel = viewModel(), signupViewModel: SignupViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Spacer(modifier = Modifier.height(40.dp))
        // Naslov aplikacije
        Text(
            text = "Run Together",
            style = TextStyle(
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Magenta
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Manji tekst ispod naslova
        Text(
            text = "Trčimo zajedno!",
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))

        LoginOrRegisterButtons(
            onLoginClick = {
                Navigator.navigateTo(Screen.LogInScreen)
            },
            onRegisterClick = {
                Navigator.navigateTo(Screen.SignUpScreen)
            }
        )



    }
}

@Composable
fun HomePageScreen(navController: NavController) {
    LoginOrRegisterButtons(
        onLoginClick = {
            navController.navigate(Screen.LogInScreen)
        },
        onRegisterClick = {
            navController.navigate(Screen.SignUpScreen)
        }
    )
}
@Preview
@Composable
fun DefaultPreviewOfHomePageScreen() {
    HomePage()
}