package com.example.saferoute.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saferoute.AuthManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val googleClient: GoogleSignInClient = AuthManager.getGoogleSignInClient(
        context,
        "1029139051952-7n20gvls34dvse1k8a7tiojamhshbkge.apps.googleusercontent.com"
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        AuthManager.handleGoogleSignInResult(result.data) { success, msg ->
            coroutineScope.launch {
                if (success) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    snackbarHostState.showSnackbar(msg ?: "Google Sign-In failed")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "Safe Route",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7E57C2)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome back! Let's keep you safe.",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    AuthManager.signInWithEmail(email.value, password.value) { success, msg ->
                        coroutineScope.launch {
                            if (success) {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                snackbarHostState.showSnackbar(msg ?: "Login failed")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Login", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text("  Or  ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    AuthManager.launchGoogleSignInWithPicker(googleClient) { intent ->
                        launcher.launch(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text("Continue with Google", color = Color.Black, fontSize = 16.sp)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
