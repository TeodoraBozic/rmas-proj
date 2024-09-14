package com.example.rmasprojekat18723.data

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignupViewModel : ViewModel() {

    private val TAG = SignupViewModel::class.simpleName

    var registrationUIState = mutableStateOf(RegistrationUIState())
    var allValidationsPassed = mutableStateOf(false)
    var signUpInProgress = mutableStateOf(false)
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    fun onEvent(event: SignupUIEvent) {
        when (event) {
            is SignupUIEvent.ImageSelected -> {
                registrationUIState.value = registrationUIState.value.copy(imageUri = event.imageUri)
                event.imageUri?.let { uploadImageToFirebaseStorage(it) }
            }
            is SignupUIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(firstName = event.firstName)
                printState()
            }
            is SignupUIEvent.LastNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(lastName = event.lastName)
                printState()
            }
            is SignupUIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(email = event.email)
                printState()
            }
            is SignupUIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(password = event.password)
                printState()
                validateDataWithRules()
            }
            is SignupUIEvent.Password2Changed -> {
                registrationUIState.value = registrationUIState.value.copy(password2 = event.password2)
                printState()
                validateDataWithRules()
            }
            is SignupUIEvent.UserNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(username = event.username)
            }
            is SignupUIEvent.PhoneNumberChanged -> {
                registrationUIState.value = registrationUIState.value.copy(phonenumber = event.phonenumber)
            }
            is SignupUIEvent.RegisterButtonClicked -> {
                //ako su svi uslovi ispunjeni onda se poziva signUp()
                if (allValidationsPassed.value) {
                    signUp()
                }
            }
        }
    }

    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(registrationUIState.value.firstName)
        val lNameResult = Validator.validateLastName(registrationUIState.value.lastName)
        val emailResult = Validator.validateEmail(registrationUIState.value.email)
        val passwordResult = Validator.validatePassword(registrationUIState.value.password)
        val password2Result = Validator.validatePassword2(registrationUIState.value.password2)
        val phonenumberResult = Validator.validatePhoneNumber(registrationUIState.value.phonenumber)
//proverava da li su ove 2 sifre iste!!
        val passwordsMatch = registrationUIState.value.password == registrationUIState.value.password2

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            password2Error = password2Result.status,
            phonenumberError = phonenumberResult.status,
            passwordMatchError = !passwordsMatch
        )

        allValidationsPassed.value = fNameResult.status &&
                lNameResult.status &&
                emailResult.status &&
                passwordResult.status &&
                password2Result.status &&
                phonenumberResult.status &&
                passwordsMatch
    }

    private fun signUp() {
        //ovo prolazi
        Log.d(TAG, "Inside_signUp")
        printState()
        createUserInFirebase(
            //cuvace se korisnik novokreirani na osnovu email-a i password-a
            email = registrationUIState.value.email,
            password = registrationUIState.value.password,


        )
        Log.d(TAG, "Zapmti email i pass")
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, registrationUIState.value.toString())
    }

    private fun createUserInFirebase(email: String, password: String) {
        signUpInProgress.value = true

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "insideCompleteListener")
                Log.d(TAG, "isSuccessful")
                signUpInProgress.value = false
                if (task.isSuccessful) {
                    // Prikazi poruku o uspehu
                    Log.d(TAG, "Registracija uspešna")
                    Navigator.navigateTo(Screen.LogInScreen)
                } else {

                    Log.d(TAG, "Registracija nije uspela: ${task.exception?.message}")

                }
            }
    }
    private fun uploadImageToFirebaseStorage(imageUri: Uri?) {
        imageUri?.let {
            val storageRef = FirebaseStorage.getInstance().reference
            val imagesRef = storageRef.child("profile_images/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")

            imagesRef.putFile(imageUri)
                .addOnSuccessListener {
                    Log.d("SignUpViewModel", "Image uploaded successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SignUpViewModel", "Failed to upload image", e)
                }
        }
    }

}

