package com.example.policeplus.views

import PoliceRegistrationForm
import ToggleButtonItem
import UserRegistrationForm
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavController
import com.example.policeplus.LoginRequest
import com.example.policeplus.R
import com.example.policeplus.UserViewModel
import com.example.policeplus.ui.theme.PolicePlusBlue




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavController, userViewModel: UserViewModel) {
    var isPoliceLogin by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(110.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFF0F4F7))
                .padding(4.dp)
        ) {
            ToggleButtonItem("Police", isPoliceLogin) { isPoliceLogin = true }
            ToggleButtonItem("User", !isPoliceLogin) { isPoliceLogin = false }
        }

        Spacer(modifier = Modifier.height(34.dp))


        LoginScreenReal(navController, userViewModel, isPoliceLogin)

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreenReal(navController: NavController, userViewModel: UserViewModel, isPoliceLogin:Boolean) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        val textFieldModifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)

        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = null
                message = null
            },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            placeholder = {
                Text(
                    "Email",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF8391A1)
                )
            },
            modifier = textFieldModifier,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Black,
                unfocusedBorderColor = if (emailError != null) MaterialTheme.colorScheme.error else Color(0xFFE8ECF4),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = null
                message = null
            },
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            placeholder = {
                Text(
                    "Password",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF8391A1)
                )
            },
            modifier = textFieldModifier,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (showPassword)
                    painterResource(R.drawable.eyeopened)
                else painterResource(R.drawable.eyeclosed)

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(painter = image, contentDescription = if (showPassword) "Hide password" else "Show password")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Black,
                unfocusedBorderColor = if (passwordError != null) MaterialTheme.colorScheme.error else Color(0xFFE8ECF4),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Reset previous errors
                emailError = null
                passwordError = null
                message = null
                
                // Validate inputs
                var isValid = true
                
                if (email.isBlank()) {
                    emailError = "Email is required"
                    isValid = false
                } else if (!userViewModel.isValidEmail(email)) {
                    emailError = "Please enter a valid email address"
                    isValid = false
                }
                
                if (password.isBlank()) {
                    passwordError = "Password is required"
                    isValid = false
                }
                
                if (!isValid) return@Button
                
                isLoading = true
                val loginRequest = LoginRequest(email, password)
                
                val loginCallback = { success: Boolean, msg: String? ->
                    isLoading = false
                    if (!success) {
                        message = when {
                            msg?.contains("401") == true -> "Invalid email or password"
                            msg?.contains("timeout") == true -> "Connection timeout. Please check your internet"
                            msg?.contains("unable to resolve host") == true -> "No internet connection"
                            msg != null -> msg
                            else -> "Login failed. Please try again"
                        }
                    } else {
                        navController.navigate("home")
                    }
                }
                
                if (isPoliceLogin) {
                    userViewModel.loginUser(loginRequest, loginCallback)
                } else {
                    userViewModel.loginNormal(loginRequest, loginCallback)
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = PolicePlusBlue,
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Don't have an account? ")
            Text(
                text = "Register Now",
                color = Color(0xFF0077B6),
                modifier = Modifier.clickable {
                    navController.navigate("register")
                },
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        }

        message?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = if (it == "Login successful!") Color.Green else Color.Red
            )
        }
    }
}

