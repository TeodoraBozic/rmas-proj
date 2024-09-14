package com.example.rmas18577.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.components.ClickableTextLogin
import com.example.rmas18577.components.Heading
import com.example.rmas18577.components.NormalTextComponent
import com.example.rmas18577.components.ButtonComponent
import com.example.rmas18577.components.MyTextFieldComponent
import com.example.rmas18577.components.PasswordTextFieldComponent
import com.example.rmasprojekat18723.data.LoginUIEvent
import com.example.rmasprojekat18723.data.LoginViewModel
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val loginState = loginViewModel.loginUIState.value
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                NormalTextComponent(value = "Zdravo")
                Heading(value = "Prijavi se")
                Spacer(modifier = Modifier.height(20.dp))


                MyTextFieldComponent(
                    labelValue = "Email",
                    Icons.Default.Email,
                    onTextChanged = {
                        loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                    },
                    errorStatus = loginState.emailError != null// Pristupamo direktno emailError iz loginState
                )


                // U funkciji SignUpScreen
                PasswordTextFieldComponent(
                    labelValue = "Password",
                    icon = Icons.Default.Lock,
                    onTextSelected = { loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it)) },
                    errorStatus = loginState.passwordError,
                    errorMessage = "Lozinka mora imati najmanje 6 karaktera.",
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityToggle = {
                        isPasswordVisible = !isPasswordVisible
                        Log.d("SignUpScreen", "Password visibility toggled: $isPasswordVisible")
                    }
                )




                Spacer(modifier = Modifier.height(40.dp))

                ButtonComponent(
                    value = "Prijavi se",
                    onButtonClicked = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidationsPassed.value
                )

                Spacer(modifier = Modifier.height(20.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                ClickableTextLogin(tryingToLogin = false, onTextSelected = {
                    Navigator.navigateTo(Screen.SignUpScreen)
                })
            }
        }

        if (loginViewModel.loginInProgress.value) {
            CircularProgressIndicator()
        }
    }

    BackHandler {
        Navigator.navigateTo(Screen.HomePage)
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
