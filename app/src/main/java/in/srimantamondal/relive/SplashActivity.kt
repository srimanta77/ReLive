package `in`.srimantamondal.relive

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import `in`.srimantamondal.relive.ui.theme.ReLiveTheme
import `in`.srimantamondal.relive.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReLiveTheme {
                SplashScreen(
                    onAnimationFinished = {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        @Suppress("DEPRECATION")
                        this@SplashActivity.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1.05f else 0.8f,
        animationSpec = tween(durationMillis = 1500, easing = EaseInOut)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onAnimationFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B132B) // Dark navy background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(330.dp)
                    .scale(scaleAnim.value)
                    .background(
                        Color(0xFFFFC107).copy(alpha = glowAlpha),
                        shape = CircleShape
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.relive_logo),
                contentDescription = "ReLive Logo",
                modifier = Modifier
                    .size(280.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            )
        }
    }
}
