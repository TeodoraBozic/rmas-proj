package com.example.rmas18577.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.components.ClickableTextLogin
import com.example.rmas18577.components.Heading
import com.example.rmas18577.components.MyTextFieldComponent
import com.example.rmas18577.components.PasswordTextFieldComponent
import com.example.rmas18577.components.ButtonComponent
import com.example.rmas18577.components.NormalTextComponent
import com.example.rmasprojekat18723.data.LoginUIEvent
import com.example.rmasprojekat18723.data.LoginViewModel
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val loginState = loginViewModel.loginUIState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp) // Consistent padding

    ) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth() // Changed to fillMaxWidth for better alignment
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp) // Consistent padding
            ) {
                NormalTextComponent(value = "Zdravo")
                Heading(value = "Prijavi se")
                Spacer(modifier = Modifier.height(16.dp))

                MyTextFieldComponent(
                    labelValue = "Email",
                    Icons.Default.Email,
                    onTextChanged = {
                        loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                    },
                    errorStatus = loginState.emailError != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordTextFieldComponent(
                    labelValue = "Password",
                    icon = Icons.Default.Lock,
                    onTextSelected = { loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it)) },
                    errorStatus = loginState.passwordError,
                    errorMessage = "Lozinka mora imati najmanje 6 karaktera.",
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityToggle = {
                        isPasswordVisible = !isPasswordVisible
                        Log.d("LoginScreen", "Password visibility toggled: $isPasswordVisible")
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                ButtonComponent(
                    value = "Prijavi se",
                    onButtonClicked = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidationsPassed.value
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                ClickableTextLogin(tryingToLogin = false) {
                    Navigator.navigateTo(Screen.SignUpScreen)
                }
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
