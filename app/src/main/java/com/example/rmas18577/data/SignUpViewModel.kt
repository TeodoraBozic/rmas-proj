package com.example.rmasprojekat18723.data

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rmas18577.screens.uploadImageToFirebaseStorage
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
                Log.d(TAG, "Register button clicked")
                if (allValidationsPassed.value) {
                    signUp()
                } else {
                    Log.d(TAG, "Validations not passed")
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
//BITNOOOOOOOOOOOOOOO
    private fun createUserInFirebase(email: String, password: String) {
        signUpInProgress.value = true

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                signUpInProgress.value = false
                if (task.isSuccessful) {
                    Log.d(TAG, "User registration successful")
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid ?: return@addOnCompleteListener

                    // Dodajte podatke o korisniku u Firestore
                    val userData = hashMapOf(
                        "username" to registrationUIState.value.username,
                        "firstname" to registrationUIState.value.firstName,
                        "lastname" to registrationUIState.value.lastName,
                        "phoneNumber" to registrationUIState.value.phonenumber,
                        "email" to email,

                    )

                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d(TAG, "Uso sam u addOnSuccessListener funckiju")

                            Log.d(TAG, "Image URI: ${registrationUIState.value.imageUri}")

                            registrationUIState.value.imageUri?.let { imageUri ->
                                uploadImageToFirebaseStorage(imageUri, userId)
                                Navigator.navigateTo(Screen.LogInScreen)
                            } ?: run {
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Failed to save user data: ${exception.message}")
                        }

                } else {
                    Log.d(TAG, "Registration failed: ${task.exception?.message}")


                }
            }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("profile_images/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")

        imagesRef.putFile(imageUri)
            .addOnSuccessListener {
                imagesRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Ažurirajte URL slike u Firestore
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .update("photoUrl", downloadUrl.toString())
                        .addOnSuccessListener {
                            Log.d(TAG, "Image URL updated successfully")
                            Navigator.navigateTo(Screen.LogInScreen)
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Failed to update image URL: ${exception.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to upload image", e)
            }
    }


}

