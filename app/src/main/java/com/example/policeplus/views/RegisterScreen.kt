import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.policeplus.RegisterRequest
import com.example.policeplus.UserViewModel

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = rank, onValueChange = { rank = it }, label = { Text("Rank") })
        OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Department") })
        OutlinedTextField(value = badgeNumber, onValueChange = { badgeNumber = it }, label = { Text("Badge Number") })

        Spacer(modifier = Modifier.height(16.dp))
        val registerRequest  = RegisterRequest(

            email = email,
            password = password,
            name = name,
            rank = rank,
            department = department,
            badge_number = badgeNumber,
        )


        Button(
            onClick = {
                isLoading = true
                val registerRequest = RegisterRequest(email, password, name, rank, department, badgeNumber)

                userViewModel.registerUser(registerRequest) { success, msg ->
                    isLoading = false
                    message = msg
                    if (success) {
                        navController.navigate("login") // Navigate to login on success
                    }
                }
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Register")
            }
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }


        message?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = if (it == "Registration successful!") Color.Green else Color.Red)
        }
    }
}
