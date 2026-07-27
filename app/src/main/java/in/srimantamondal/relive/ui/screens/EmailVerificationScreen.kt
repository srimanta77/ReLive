package `in`.srimantamondal.relive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val NavyBg = Color(0xFF0B132B)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val RedAccent = Color(0xFFFF5252)

/**
 * Blocks access until the signed-in user's email is verified. There is no
 * skip/bypass here on purpose — verification is what proves the email is
 * real and owned by whoever signed up.
 */
@Composable
fun EmailVerificationScreen(onVerified: () -> Unit, onLogout: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    var isChecking by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var resendCooldown by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000)
            resendCooldown -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.MarkEmailUnread,
            contentDescription = null,
            tint = PurpleAccent,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Verify your email",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "We sent a verification link to ${auth.currentUser?.email ?: "your email"}. " +
                    "Please click it, then tap Continue below.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                message,
                color = if (isError) RedAccent else GreenAccent,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                isChecking = true
                message = ""
                scope.launch {
                    try {
                        auth.currentUser?.reload()?.await()
                        if (auth.currentUser?.isEmailVerified == true) {
                            onVerified()
                        } else {
                            isError = true
                            message = "Not verified yet. Check your inbox (and spam folder)."
                        }
                    } catch (e: Exception) {
                        isError = true
                        message = e.message ?: "Something went wrong"
                    } finally {
                        isChecking = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
            shape = RoundedCornerShape(10.dp),
            enabled = !isChecking
        ) {
            if (isChecking) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("I've verified — Continue", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            enabled = !isResending && resendCooldown == 0,
            onClick = {
                isResending = true
                message = ""
                scope.launch {
                    try {
                        auth.currentUser?.sendEmailVerification()?.await()
                        isError = false
                        message = "Verification email sent again."
                        resendCooldown = 30
                    } catch (e: Exception) {
                        isError = true
                        message = e.message ?: "Couldn't resend right now"
                    } finally {
                        isResending = false
                    }
                }
            }
        ) {
            Text(
                if (resendCooldown > 0) "Resend in ${resendCooldown}s" else "Resend email",
                color = if (resendCooldown > 0) TextSecondary else PurpleAccent
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = {
            auth.signOut()
            onLogout()
        }) {
            Text("Use a different account", color = TextSecondary, fontSize = 13.sp)
        }
    }
}