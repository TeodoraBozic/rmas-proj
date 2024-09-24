package com.example.rmas18577.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.R
import com.example.rmas18577.components.ButtonComponent
import com.example.rmas18577.components.ClickableTextLogin
import com.example.rmas18577.components.Heading
import com.example.rmas18577.components.MyTextFieldComponent
import com.example.rmas18577.components.NormalTextComponent
import com.example.rmas18577.components.PasswordTextFieldComponent
import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmasprojekat18723.data.SignupUIEvent
import com.example.rmasprojekat18723.data.SignupViewModel
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


// Funkcija za čuvanje Bitmap slike u fajl
fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
    val filename = "IMG_${System.currentTimeMillis()}.png"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(storageDir, filename)

    return try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
        }
        Uri.fromFile(file)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

// Funkcija za upload slike na Firebase
fun uploadImageToFirebaseStorage(imageUri: Uri?) {
    imageUri?.let {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("profile_images/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")

        imagesRef.putFile(imageUri)
            .addOnSuccessListener {
                Log.d("SignUpScreen", "Image uploaded successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SignUpScreen", "Failed to upload image", e)
            }
    }
}


@Composable
fun SignUpScreen(signupViewModel: SignupViewModel = viewModel()) {
    val context = LocalContext.current
    val selectedImageUri by signupViewModel.selectedImageUri.observeAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPassword2Visible by remember { mutableStateOf(false) }
    val registrationUIState = signupViewModel.registrationUIState.value
    val allValidationsPassed = signupViewModel.allValidationsPassed.value

    // Launchers for selecting and taking pictures
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { signupViewModel.onEvent(SignupUIEvent.ImageSelected(uri)) } }
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            bitmap?.let {
                val imageUri = saveBitmapToFile(context, it)
                imageUri?.let { uri ->
                    signupViewModel.onEvent(SignupUIEvent.ImageSelected(uri))
                    uploadImageToFirebaseStorage(uri)
                }
            }
        }
    )

    // Scrollable Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()) // Add scroll state here
    ) {
        // Image selection buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { pickImageLauncher.launch("image/*") }) {
                Text("Izaberi sliku")
            }
            Button(onClick = { takePictureLauncher.launch() }) {
                Text("Slikaj")
            }
        }

        // Image preview
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = "Profilna slika",
                modifier = Modifier
                    .size(150.dp)
                    .padding(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Increased space here for better separation

        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth() // Changed to fillMaxWidth for better alignment
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                NormalTextComponent(value = "Kreiraj nalog")

                Spacer(modifier = Modifier.height(8.dp)) // Increased space for better separation

                // Username
                MyTextFieldComponent(
                    labelValue = "Username",
                    Icons.Default.Person,
                    onTextChanged = { signupViewModel.onEvent(SignupUIEvent.UserNameChanged(it)) },
                    errorStatus = registrationUIState.usernameError
                )
                MyTextFieldComponent(
                    labelValue = "Ime",
                    Icons.Default.Person,
                    onTextChanged = { signupViewModel.onEvent(SignupUIEvent.FirstNameChanged(it)) },
                    errorStatus = registrationUIState.firstNameError
                )
                MyTextFieldComponent(
                    labelValue = "Prezime",
                    Icons.Default.Person,
                    onTextChanged = { signupViewModel.onEvent(SignupUIEvent.LastNameChanged(it)) },
                    errorStatus = registrationUIState.lastNameError
                )
                MyTextFieldComponent(
                    labelValue = "Email",
                    Icons.Default.Email,
                    onTextChanged = { signupViewModel.onEvent(SignupUIEvent.EmailChanged(it)) },
                    errorStatus = registrationUIState.emailError
                )
                MyTextFieldComponent(
                    labelValue = "Broj telefona",
                    Icons.Default.Phone,
                    onTextChanged = { signupViewModel.onEvent(SignupUIEvent.PhoneNumberChanged(it)) },
                    errorStatus = registrationUIState.phonenumberError
                )

                Spacer(modifier = Modifier.height(16.dp)) // Increased space for better separation

                // Password
                PasswordTextFieldComponent(
                    labelValue = "Password",
                    icon = Icons.Default.Lock,
                    onTextSelected = { signupViewModel.onEvent(SignupUIEvent.PasswordChanged(it)) },
                    errorStatus = registrationUIState.passwordError.toString(),
                    errorMessage = "Lozinka mora imati najmanje 6 karaktera.",
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                )

                Spacer(modifier = Modifier.height(8.dp)) // Increased space for better separation

                // Confirm Password
                PasswordTextFieldComponent(
                    labelValue = "Potvrdi lozinku",
                    icon = Icons.Default.Lock,
                    onTextSelected = { signupViewModel.onEvent(SignupUIEvent.Password2Changed(it)) },
                    errorStatus = registrationUIState.passwordMatchError.toString(),
                    errorMessage = "Lozinke se moraju poklapati",
                    isPasswordVisible = isPassword2Visible,
                    onVisibilityToggle = { isPassword2Visible = !isPassword2Visible }
                )

                Spacer(modifier = Modifier.height(16.dp)) // Increased space for better separation

                // Register Button
                ButtonComponent(
                    value = "Registruj se",
                    onButtonClicked = { signupViewModel.onEvent(SignupUIEvent.RegisterButtonClicked) },
                    isEnabled = allValidationsPassed
                )

                Spacer(modifier = Modifier.height(16.dp)) // Increased space for better separation

                // Login Text
                ClickableTextLogin(tryingToLogin = true) { selectedText ->
                    if (selectedText == "Prijavi se") {
                        Navigator.navigateTo(Screen.LogInScreen)
                    }
                }
            }
        }


        SystemBackButtonHandler {
            Navigator.navigateTo(Screen.HomePage)
        }
    }
}


