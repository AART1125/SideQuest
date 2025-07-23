package com.mobicom.s18.toledo.aaronace.sidequest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobicom.s18.toledo.aaronace.sidequest.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val questGreen = Color(0xFF509A72)

    val mobileNumber by viewModel.mobileNumber
    val password by viewModel.password
    val passwordVisible by viewModel.passwordVisible

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Text(
            text = "Login to your account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        )
        Spacer(modifier = Modifier.height(32.dp))

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
        Spacer(modifier = Modifier.height(40.dp))

        // Login button
        Button(
            onClick = {
                if (viewModel.canLogin()) {
                    onNavigateToMain()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = questGreen),
            enabled = viewModel.canLogin()
        ) {
            Text("Login", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToMain = {}
    )
}
