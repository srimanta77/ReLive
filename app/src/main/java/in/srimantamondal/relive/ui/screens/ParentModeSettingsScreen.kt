package `in`.srimantamondal.relive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.srimantamondal.relive.ui.HomeViewModel

@Composable
fun ParentModeSettingsScreen(
    viewModel: HomeViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val parentMode by viewModel.parentMode.collectAsState()
    val passwordExists by viewModel.passwordExists.collectAsState()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var statusIsError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = Color(0xFF7C4DFF),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Parent Mode Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Divider(color = Color(0xFF2A2A3E))

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (parentMode) Color(0xFF1B5E20) else Color(0xFF1A1A2E)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (parentMode) Color(0xFF69F0AE) else Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (parentMode) "Parent Mode is ON" else "Parent Mode is OFF",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (parentMode) "Device is being protected"
                        else "Enable from Home screen",
                        fontSize = 12.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }
        }

        // Change Password Section
        if (passwordExists) {
            Text(
                text = "Change Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF7C4DFF)
            )

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7C4DFF),
                    unfocusedBorderColor = Color(0xFF3A3A5C),
                    focusedLabelColor = Color(0xFF7C4DFF),
                    cursorColor = Color(0xFF7C4DFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(if (passwordExists) "New Password" else "Set Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C4DFF),
                unfocusedBorderColor = Color(0xFF3A3A5C),
                focusedLabelColor = Color(0xFF7C4DFF),
                cursorColor = Color(0xFF7C4DFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C4DFF),
                unfocusedBorderColor = Color(0xFF3A3A5C),
                focusedLabelColor = Color(0xFF7C4DFF),
                cursorColor = Color(0xFF7C4DFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Status message
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = if (statusIsError) Color(0xFFFF5252) else Color(0xFF69F0AE),
                fontSize = 13.sp
            )
        }

        // Save Button
        Button(
            onClick = {
                if (newPassword != confirmPassword) {
                    statusMessage = "Passwords do not match!"
                    statusIsError = true
                    return@Button
                }
                if (newPassword.length < 4) {
                    statusMessage = "Password must be at least 4 characters!"
                    statusIsError = true
                    return@Button
                }
                if (passwordExists) {
                    if (!viewModel.verifyParentPassword(currentPassword)) {
                        statusMessage = "Current password is incorrect!"
                        statusIsError = true
                        return@Button
                    }
                }
                viewModel.setParentPassword(newPassword)
                statusMessage = "Password saved successfully!"
                statusIsError = false
                currentPassword = ""
                newPassword = ""
                confirmPassword = ""
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C4DFF)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = if (passwordExists) "Change Password" else "Set Password",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}