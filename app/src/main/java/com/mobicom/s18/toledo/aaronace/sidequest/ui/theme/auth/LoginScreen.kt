package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun LoginScreen(
    onNavigateToMain: () -> Unit = {},
    onSignupClicked:() -> Unit = {},
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
            text = if (uiState.showOtpInput) "Enter OTP" else "Login to your account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        )
        Spacer(modifier = Modifier.height(32.dp))

        if(!uiState.showOtpInput) {
            // Mobile Number Input
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
            /*Spacer(modifier = Modifier.height(16.dp))

            // Password
            Text("Password", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::updatePassword,
                placeholder = { Text("Enter password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true
            )*/

            Spacer(modifier = Modifier.height(40.dp))

            // Send OTP button
            Button(
                onClick = { viewModel.login(context as androidx.activity.ComponentActivity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = questGreen),
                enabled = viewModel.canLogin() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Send OTP", fontSize = 18.sp, color = Color.White)
                }
            }
        } else {
            // OTP Input
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
                placeholder = { Text("XXXXXX") },
                label = { Text("OTP Code") }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Verify OTP button
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
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Verify OTP", fontSize = 18.sp, color = Color.White)
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

        // Sign up link
        if(!uiState.showOtpInput) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = Color.Gray)
                TextButton(onClick = onSignupClicked) {
                    Text(
                        text = "Sign up here",
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
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToMain = {}
    )
}
