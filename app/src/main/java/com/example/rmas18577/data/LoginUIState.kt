package com.example.rmasprojekat18723.data


data class LoginUIState(
    var email : String = "",
    var password : String = "",

    var emailError : String? = null,
    var passwordError : String? = null,
    var loginError: String? = null,
)