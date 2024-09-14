package com.example.rmasprojekat18723.data



object Validator {

//Ime
    fun validateFirstName(fName: String): ValidationResult {
        return ValidationResult(
            (!fName.isNullOrEmpty() && fName.length >= 2)
        )

    }

    //Korisnicko ime
    fun validateUsername(fName: String): ValidationResult {
        return ValidationResult(
            (!fName.isNullOrEmpty() && fName.length >= 2)
        )

    }
//Prezime
    fun validateLastName(lName: String): ValidationResult {
        return ValidationResult(
            (!lName.isNullOrEmpty() && lName.length >= 2)
        )
    }


    fun validatePassword(password: String): ValidationResult {
        return ValidationResult(
            (!password.isNullOrEmpty() && password.length >= 4)
        )
    }

    fun validateEmail(email: String): ValidationResult {
        return ValidationResult(
            (!email.isNullOrEmpty() && email.length >= 4)
        )
    }

    fun validatePhoneNumber(phonenumber: String): ValidationResult {
        return ValidationResult(
            (!phonenumber.isNullOrEmpty() && phonenumber.length >= 8)
        )
    }

    fun validatePassword2(password2: String): ValidationResult {
        return ValidationResult(
            (!password2.isNullOrEmpty() && password2.length >= 4)
        )
    }

}

data class ValidationResult(
    val status: Boolean = false
)








