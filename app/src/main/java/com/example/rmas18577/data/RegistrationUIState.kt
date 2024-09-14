package com.example.rmasprojekat18723.data

import android.net.Uri

data class RegistrationUIState(
    var username : String = "",
    var firstName :String = "",
    var lastName  :String = "",
    var phonenumber: String = " ",
    var email  :String = "",
    var password  :String = "",
    var password2 :String =" ",
    val imageUri: Uri? = null, // Dodajte ovo,


    var usernameError:Boolean =false,
    var firstNameError :Boolean = false,
    var lastNameError : Boolean = false,
    var phonenumberError : Boolean = false,
    var emailError :Boolean = false,
    var passwordError : Boolean = false,
    var password2Error:Boolean=false,
    val passwordMatchError: Boolean = false



)
