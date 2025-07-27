package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mobicom.s18.toledo.aaronace.sidequest.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

data class AuthUiState(
    val mobileNumber: String = "+63",
    val password: String = "",
    val username: String = "",
    val confirmPassword: String = "",
    val otpCode: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val showOtpInput: Boolean = false,
    val verificationId: String? = null,
    val resendToken: PhoneAuthProvider.ForceResendingToken? = null,
    val message: String? = null,
    val isSignup: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(isLoggedIn = auth.currentUser != null)
    }

    // Update functions
    fun updateMobileNumber(number: String) {
        _uiState.value = _uiState.value.copy(mobileNumber = number, errorMessage = null)
    }

    fun updateOtpCode(code: String) {
        _uiState.value = _uiState.value.copy(otpCode = code, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, errorMessage = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            passwordVisible = !_uiState.value.passwordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            confirmPasswordVisible = !_uiState.value.confirmPasswordVisible
        )
    }

    // Send OTP
    fun sendOtp(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val phoneNumber = _uiState.value.mobileNumber

                /*if(phoneNumber.isBlank() || phoneNumber.length != 13) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enter a valid phone number."
                    )
                    return@launch
                }*/

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            // Auto-retrieval or instant verification
                            signInWithCredential(credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Verification failed"
                            )
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                showOtpInput = true,
                                verificationId = verificationId,
                                resendToken = token,
                                message = "OTP sent to $phoneNumber",
                                errorMessage = null
                            )
                        }
                    })
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to send OTP"
                )
            }
        }
    }

    // Verify OTP
    fun verifyOtp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val verificationId = _uiState.value.verificationId
                val otpCode = _uiState.value.otpCode

                if(verificationId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Verification ID is missing."
                    )
                    return@launch
                }

                if(otpCode.isBlank() || otpCode.length != 6) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enter a valid OTP."
                    )
                    return@launch
                }

                val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
                signInWithCredential(credential, onSuccess)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Invalid OTP."
                )
            }
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithCredential(credential).await()
                val userId = result.user?.uid ?: ""

                if(userId.isNotEmpty()) {
                    // Check if user exists in Firestore
                    val userDoc = firestore.collection("users").document(userId).get().await()
                    val isSignup = _uiState.value.isSignup

                    when {
                        // SIGNUP: User doesn't exist, create new account
                        !userDoc.exists() && isSignup -> {
                            if (_uiState.value.username.isNotBlank()) {
                                val userProfile = UserModel(
                                    id = userId,
                                    username = _uiState.value.username,
                                    phoneNumber = _uiState.value.mobileNumber
                                )
                                firestore.collection("users")
                                    .document(userId)
                                    .set(userProfile)
                                    .await()

                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    showOtpInput = false,
                                    message = "Account created successfully!",
                                )
                                onSuccess?.invoke()
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Username is required."
                                )
                            }
                        }

                        // LOGIN: User exists, just log in
                        userDoc.exists() && !isSignup -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                showOtpInput = false,
                                message = "Logged in successfully!"
                            )
                            onSuccess?.invoke()
                        }

                        // ERROR: Trying to sign up with phone number already linked to an account
                        userDoc.exists() && isSignup -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "An account with this phone number already exists. Please login instead."
                            )
                        }

                        // ERROR: Trying to log but account doesn't exist
                        !userDoc.exists() && !isSignup -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "No account found for this phone number. Please sign up."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Authentication failed."
                )
            }
        }
    }

    fun resendOtp(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val phoneNumber = _uiState.value.mobileNumber
                val resendToken = _uiState.value.resendToken

                val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            signInWithCredential(credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Resend failed"
                            )
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                verificationId = verificationId,
                                resendToken = token,
                                message = "OTP resent to $phoneNumber"
                            )
                        }
                    })

                if(resendToken != null) {
                    optionsBuilder.setForceResendingToken(resendToken)
                }

                PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to resend OTP"
                )
            }
        }
    }

    private suspend fun checkPhoneNumberExists(phoneNumber: String): Boolean {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    fun login(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSignup = false,
                isLoading = true,
                errorMessage = null
            )

            try {
                val phoneNumber = _uiState.value.mobileNumber

                // Phone number format validation
                if (phoneNumber.isBlank() || phoneNumber.length != 13) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enter a valid phone number."
                    )
                    return@launch
                }

                // Check if phone already in db before sending OTP
                val phoneExists = checkPhoneNumberExists(phoneNumber)

                if (!phoneExists) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No account found with this phone number. Please sign up."
                    )
                    return@launch
                }

                sendOtp(activity)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to verify phone number."
                )
            }
        }
    }

    fun signUp(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSignup = true,
                isLoading = true,
                errorMessage = null
            )

            try {
                // Username validation
                if (_uiState.value.username.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Username is required."
                    )
                    return@launch
                }

                val phoneNumber = _uiState.value.mobileNumber

                // Phone number format validation
                if (phoneNumber.isBlank() || phoneNumber.length != 13) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enter a valid phone number."
                    )
                    return@launch
                }

                // Check if phone already in db before sending OTP
                val phoneExists = checkPhoneNumberExists(phoneNumber)

                if (phoneExists) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "An account with this phone number already exists. Please login instead."
                    )
                    return@launch
                }

                sendOtp(activity)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to verify phone number."
                )
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            auth.signOut()
            _uiState.value = AuthUiState()
            onSuccess()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(message = null, errorMessage = null)
    }

    fun canLogin(): Boolean {
        return _uiState.value.mobileNumber.isNotBlank()
                //&& _uiState.value.password.isNotBlank()
    }

    fun canSignUp(): Boolean {
        return _uiState.value.username.isNotBlank() && _uiState.value.mobileNumber.isNotBlank()
    }

    fun canVerifyOtp(): Boolean {
        return _uiState.value.otpCode.length == 6
    }
}
