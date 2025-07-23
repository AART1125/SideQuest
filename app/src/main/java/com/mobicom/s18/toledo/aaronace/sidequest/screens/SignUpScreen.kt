package com.mobicom.s18.toledo.aaronace.sidequest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobicom.s18.toledo.aaronace.sidequest.viewmodels.AuthViewModel

@Composable
fun SignupScreen(
    onLoginClicked: () -> Unit = {},
    onSignUpClicked: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val questGreen = Color(0xFF509A72)

    val username by viewModel.username
    val mobileNumber by viewModel.mobileNumber
    val password by viewModel.password
    val confirmPassword by viewModel.confirmPassword
    val passwordVisible by viewModel.passwordVisible
    val confirmPasswordVisible by viewModel.confirmPasswordVisible

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Text(
            text = "Sign up",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create an account to get started",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Username
        Text("Username", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = username,
            onValueChange = viewModel::updateUsername,
            placeholder = { Text("Enter username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Mobile number
        Text("Mobile number", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = mobileNumber,
            onValueChange = viewModel::updateMobileNumber,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

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
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        Text("Confirm password", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = viewModel::updateConfirmPassword,
            placeholder = { Text("Confirm password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (confirmPasswordVisible) "Hide password" else "Show password"
                IconButton(onClick = viewModel::toggleConfirmPasswordVisibility) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Sign Up button
        Button(
            onClick = {
                if (viewModel.canSignUp()) {
                    onSignUpClicked()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = questGreen)
        ) {
            Text("Sign up", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account? ", color = Color.Gray)
            ClickableText(
                text = AnnotatedString("Login here"),
                onClick = { onLoginClicked() },
                style = TextStyle(
                    color = questGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
        SignupScreen()
}