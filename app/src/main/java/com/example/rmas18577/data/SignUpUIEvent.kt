package com.example.rmasprojekat18723.data

import android.net.Uri

sealed class SignupUIEvent{

    data class UserNameChanged(val username: String): SignupUIEvent()
    data class FirstNameChanged(val firstName:String) : SignupUIEvent()
    data class LastNameChanged(val lastName:String) : SignupUIEvent()
    data class EmailChanged(val email:String): SignupUIEvent()
    data class PasswordChanged(val password: String) : SignupUIEvent()
    data class Password2Changed(val password2: String) : SignupUIEvent()
    data class PhoneNumberChanged(val phonenumber:String): SignupUIEvent()
    data class ImageSelected(val imageUri: Uri?) : SignupUIEvent() // Dodajte ovo



    object RegisterButtonClicked : SignupUIEvent()
}
