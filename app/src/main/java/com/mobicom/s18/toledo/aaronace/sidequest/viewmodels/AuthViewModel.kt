package com.mobicom.s18.toledo.aaronace.sidequest.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val _mobileNumber = mutableStateOf("+63")
    val mobileNumber: State<String> = _mobileNumber

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _passwordVisible = mutableStateOf(false)
    val passwordVisible: State<Boolean> = _passwordVisible

    private val _confirmPasswordVisible = mutableStateOf(false)
    val confirmPasswordVisible: State<Boolean> = _confirmPasswordVisible

    fun updateMobileNumber(number: String) {
        _mobileNumber.value = number
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _confirmPasswordVisible.value = !_confirmPasswordVisible.value
    }

    fun canLogin(): Boolean {
        return _mobileNumber.value.isNotBlank() && _password.value.isNotBlank()
    }

    fun canSignUp(): Boolean {
        return _username.value.isNotBlank() &&
                _mobileNumber.value.isNotBlank() &&
                _password.value.isNotBlank() &&
                _password.value == _confirmPassword.value
    }
}