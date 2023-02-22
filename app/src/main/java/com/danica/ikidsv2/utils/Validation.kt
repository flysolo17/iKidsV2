package com.danica.ikidsv2.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

class Validation {
    fun validateEmail(textInputLayout: TextInputLayout): Boolean {
        val email = textInputLayout.editText!!.text.toString()
        return if (email.isEmpty()) {
            textInputLayout.error = "Invalid Email"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayout.error = "Invalid Email"
            false
        } else {
            textInputLayout.error = ""
            true
        }
    }

    fun validatePassword(textInputLayout: TextInputLayout): Boolean {
        val password = textInputLayout.editText!!.text.toString()
        return if (password.isEmpty()) {
            textInputLayout.error = "This field is required!"
            false
        } else if (password.length < 8) {
            textInputLayout.error = "Password must be at least six (8) characters long!"
            false
        } else {
            textInputLayout.error = ""
            true
        }
    }
    companion object {

    }
}