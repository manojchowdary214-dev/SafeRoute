package com.example.saferoute.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saferoute.AuthManager
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {

    // Email input
    var emailState by remember { mutableStateOf("") }

    // Password input
    var passwordState by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Gradient background
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7E57C2), Color(0xFFB39DDB))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {

        // Centered card
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(24.dp)) // Card style
                .padding(32.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4A148C)
            )
            Spacer(Modifier.height(28.dp))

            // Email field
            OutlinedTextField(
                value = emailState,
                onValueChange = { emailState = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = passwordState,
                onValueChange = { passwordState = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(24.dp))

            // Sign Up button
            Button(
                onClick = {
                    AuthManager.registerWithEmail(emailState, passwordState) { success, message ->
                        scope.launch {
                            if (success) {
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            } else {
                                snackbarHostState.showSnackbar(message ?: "Registration failed")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Sign Up", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // Login redirect
            TextButton(onClick = { navController.navigate("login") }) {
                Text(
                    "Already have an account? Login",
                    color = Color(0xFF6A1B9A),
                    fontSize = 14.sp
                )
            }
        }

        // Snackbar host
        SnackbarHost(
            snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}