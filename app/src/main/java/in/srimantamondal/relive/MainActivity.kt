package `in`.srimantamondal.relive

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import `in`.srimantamondal.relive.ui.screens.AuthScreen
import `in`.srimantamondal.relive.ui.screens.EmailVerificationScreen
import `in`.srimantamondal.relive.ui.screens.HomeScreen
import `in`.srimantamondal.relive.ui.theme.ReLiveTheme

private enum class AuthState { LOGGED_OUT, NEEDS_VERIFICATION, LOGGED_IN }

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.parseColor("#0B132B")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            ReLiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0B132B)
                ) {
                    // Auth state: logged out -> needs verification -> logged in
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    var authState by remember {
                        mutableStateOf(
                            when {
                                currentUser == null -> AuthState.LOGGED_OUT
                                currentUser.isEmailVerified -> AuthState.LOGGED_IN
                                else -> AuthState.NEEDS_VERIFICATION
                            }
                        )
                    }

                    when (authState) {
                        AuthState.LOGGED_IN -> HomeScreen(
                            onLogout = { authState = AuthState.LOGGED_OUT }
                        )
                        AuthState.NEEDS_VERIFICATION -> EmailVerificationScreen(
                            onVerified = { authState = AuthState.LOGGED_IN },
                            onLogout = { authState = AuthState.LOGGED_OUT }
                        )
                        AuthState.LOGGED_OUT -> AuthScreen(
                            onAuthSuccess = { authState = AuthState.LOGGED_IN },
                            onNeedsVerification = { authState = AuthState.NEEDS_VERIFICATION }
                        )
                    }
                }
            }
        }
    }
}