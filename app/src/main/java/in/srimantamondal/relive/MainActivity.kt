package `in`.srimantamondal.relive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import `in`.srimantamondal.relive.ui.screens.HomeScreen
import `in`.srimantamondal.relive.ui.theme.ReLiveTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReLiveTheme {
                //  Use Surface to match splash background and eliminate white flicker
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0B132B)  // Same dark navy background as splash
                ) {
                    HomeScreen()
                }
            }
        }
    }
}
