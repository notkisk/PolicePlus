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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.colors
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
import androidx.navigation.NavController
import com.example.policeplus.R
import com.example.policeplus.RegisterRequest
import com.example.policeplus.UserViewModel
import com.example.policeplus.ui.theme.PolicePlusBlue

@Composable
fun RegisterScreen(navController: NavController, userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var rank by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var badgeNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo), // Replace with your logo
            contentDescription = "Logo",
            modifier = Modifier
                .size(138.dp)
                .padding(bottom = 8.dp)
        )

        // TextFields with rounded background style
        val textFieldModifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Name", fontWeight = FontWeight.Medium, fontSize = 15.sp, color= Color(0xFF8391A1)) },
            modifier = textFieldModifier,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Black, unfocusedBorderColor = Color(0xFFE8ECF4)
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = badgeNumber,
            onValueChange = { badgeNumber = it },
            placeholder = { Text("Badge Number", fontWeight = FontWeight.Medium, fontSize = 15.sp, color= Color(0xFF8391A1)) },
            modifier = textFieldModifier,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Black, unfocusedBorderColor = Color(0xFFE8ECF4)
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", fontWeight = FontWeight.Medium, fontSize = 15.sp, color= Color(0xFF8391A1)) },
            modifier = textFieldModifier,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Black, unfocusedBorderColor = Color(0xFFE8ECF4)
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
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
                focusedTextColor = Color.Black , unfocusedBorderColor = Color(0xFFE8ECF4)
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = rank,
            onValueChange = { rank = it },
            placeholder = { Text("Rank", fontWeight = FontWeight.Medium, fontSize = 15.sp, color= Color(0xFF8391A1)) },
            modifier = textFieldModifier,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Black, unfocusedBorderColor = Color(0xFFE8ECF4)
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = department,
            onValueChange = { department = it },
            placeholder = { Text("Department", fontWeight = FontWeight.Medium, fontSize = 15.sp, color= Color(0xFF8391A1)) },
            modifier = textFieldModifier,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Black, unfocusedBorderColor = Color(0xFFE8ECF4)
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Go)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val registerRequest = RegisterRequest(
            email = email,
            password = password,
            name = name,
            rank = rank,
            department = department,
            badge_number = badgeNumber
        )

        Button(
            onClick = {
                isLoading = true
                userViewModel.registerUser(registerRequest) { success, msg ->
                    isLoading = false
                    message = msg
                    if (success) {
                        navController.navigate("login")
                    }
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
                Text("Register", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Already have an account? ")
            Text(
                text = "Login Now",
                color = Color(0xFF0077B6),
                modifier = Modifier.clickable {
                    navController.navigate("login")
                },
                fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline
            )
        }

        message?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = if (it == "Registration successful!") Color.Green else Color.Red
            )
        }
    }
}


