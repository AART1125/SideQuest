package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignupScreen(
    onLoginClicked: () -> Unit = {},
    onNavigateToMain: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val questGreen = Color(0xFF509A72)

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Toast messages
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Text(
            text = if (uiState.showOtpInput) "Verify your phone" else "Sign up",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
        )

        if (!uiState.showOtpInput) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create an account to get started",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!uiState.showOtpInput) {
            // Username input
            Text("Username", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::updateUsername,
                placeholder = { Text("Enter username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mobile number input
            Text("Mobile number", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.mobileNumber,
                onValueChange = viewModel::updateMobileNumber,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                enabled = !uiState.isLoading,
                placeholder = { Text("+63XXXXXXXXXX") }
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Send OTP button
            Button(
                onClick = { viewModel.signUp(context as androidx.activity.ComponentActivity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = questGreen),
                enabled = viewModel.canSignUp() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Send OTP", fontSize = 18.sp, color = Color.White)
                }
            }
        } else {
            // OTP verification
            Text(
                text = "Enter OTP sent to ${uiState.mobileNumber}",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.otpCode,
                onValueChange = viewModel::updateOtpCode,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !uiState.isLoading,
                placeholder = { Text("000000") },
                label = { Text("OTP Code") }
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.verifyOtp(onNavigateToMain) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = questGreen),
                enabled = viewModel.canVerifyOtp() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Create Account", fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resend OTP button
            TextButton(
                onClick = { viewModel.resendOtp(context as androidx.activity.ComponentActivity) },
                enabled = !uiState.isLoading
            ) {
                Text(
                    "Resend OTP",
                    color = questGreen,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Success message
        uiState.message?.let { message ->
            Text(
                text = message,
                color = questGreen,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
            )
        }

        // Error message
        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = questGreen,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
            )
        }

        // Login link
        if(!uiState.showOtpInput) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = Color.Gray)
                TextButton(onClick = onLoginClicked) {
                    Text(
                        text = "Login here",
                        color = questGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
        SignupScreen()
}