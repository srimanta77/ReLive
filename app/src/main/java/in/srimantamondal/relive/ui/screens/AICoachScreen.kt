package `in`.srimantamondal.relive.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.srimantamondal.relive.BuildConfig
import `in`.srimantamondal.relive.data.usage.AppUsageManager
import `in`.srimantamondal.relive.ui.UsageStatsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val OrangeAccent = Color(0xFFFFB74D)

@Composable
fun AICoachScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usageManager = remember { AppUsageManager(context) }

    var userMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var screenTimeToday by remember { mutableStateOf("") }
    var topApps by remember { mutableStateOf("") }

    data class ChatMessage(val text: String, val isUser: Boolean)
    var chatHistory by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    "Hi! I'm your AI Wellness Coach. I can help you with:\n\n" +
                            "• 📊 Screen time analysis\n" +
                            "• 🎯 Focus & productivity tips\n" +
                            "• 💧 Health & wellness advice\n" +
                            "• 😴 Sleep improvement\n" +
                            "• 🧘 Stress management\n\n" +
                            "Ask me anything about your digital wellbeing!",
                    false
                )
            )
        )
    }

    LaunchedEffect(Unit) {
        val hasPermission = UsageStatsHelper.hasUsageStatsPermission(context)
        if (hasPermission) {
            val total = usageManager.getTotalScreenTimeToday()
            screenTimeToday = usageManager.formatDuration(total)
            val apps = usageManager.getTodayUsageStats().take(3)
            topApps = apps.joinToString(", ") {
                "${it.appName} (${usageManager.formatDuration(it.totalTimeInForeground)})"
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    suspend fun sendToAI(message: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val systemPrompt = """
                    You are ReLive AI Coach — a professional digital wellness and health coach.
                    
                    User's current data:
                    - Screen time today: ${screenTimeToday.ifEmpty { "Unknown" }}
                    - Most used apps: ${topApps.ifEmpty { "Unknown" }}
                    
                    Your role:
                    - Analyze their digital habits and give personalized advice
                    - Suggest focus techniques, breaks, and productivity tips
                    - Give health, sleep, water, and wellness advice
                    - Be encouraging, supportive, and actionable
                    - Keep responses concise (max 150 words)
                    - Use emojis to make responses friendly
                    - Give specific, practical tips based on their data
                """.trimIndent()

                val url = URL("https://api.anthropic.com/v1/messages")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("x-api-key", BuildConfig.ANTHROPIC_API_KEY)
                connection.setRequestProperty("anthropic-version", "2023-06-01")
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val body = JSONObject().apply {
                    put("model", "claude-sonnet-4-6")
                    put("max_tokens", 300)
                    put("system", systemPrompt)
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", message)
                        })
                    })
                }.toString()

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(body)
                writer.flush()

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    json.getJSONArray("content")
                        .getJSONObject(0)
                        .getString("text")
                } else {
                    val error = connection.errorStream?.bufferedReader()?.readText()
                    "Error $responseCode: Please check your API key. 🔑"
                }
            } catch (e: Exception) {
                "I'm having trouble connecting. Please check your internet connection. 🔄"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PurpleAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        "AI Wellness Coach",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        if (screenTimeToday.isNotEmpty()) "Screen time: $screenTimeToday"
                        else "Powered by Claude AI",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (chatHistory.size <= 1) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                chatHistory[0].text,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Quick Questions",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                val quickQuestions = listOf(
                    "📊 Analyze my screen time",
                    "🎯 How to improve focus?",
                    "😴 Tips for better sleep",
                    "💧 How much water should I drink?",
                    "🧘 Stress relief techniques",
                    "📱 How to reduce phone addiction?"
                )

                items(quickQuestions.size) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = PurpleAccent.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            val question = quickQuestions[index]
                            chatHistory = chatHistory + ChatMessage(question, true)
                            isLoading = true
                            scope.launch {
                                val response = sendToAI(question)
                                chatHistory = chatHistory + ChatMessage(response, false)
                                isLoading = false
                            }
                        }
                    ) {
                        Text(
                            quickQuestions[index],
                            color = TextPrimary,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(chatHistory.size) { index ->
                    val message = chatHistory[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (message.isUser)
                            Arrangement.End else Arrangement.Start
                    ) {
                        if (!message.isUser) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PurpleAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = PurpleAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Card(
                            modifier = Modifier.widthIn(max = 280.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (message.isUser) PurpleAccent else CardBg
                            ),
                            shape = RoundedCornerShape(
                                topStart = if (message.isUser) 16.dp else 4.dp,
                                topEnd = if (message.isUser) 4.dp else 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        ) {
                            Text(
                                message.text,
                                modifier = Modifier.padding(12.dp),
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PurpleAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = PurpleAccent,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .rotate(rotation)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    "Thinking...",
                                    modifier = Modifier.padding(12.dp),
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }

        // Input area
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    placeholder = {
                        Text(
                            "Ask your AI Coach...",
                            color = TextSecondary.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                IconButton(
                    onClick = {
                        if (userMessage.isNotBlank() && !isLoading) {
                            val msg = userMessage.trim()
                            userMessage = ""
                            chatHistory = chatHistory + ChatMessage(msg, true)
                            isLoading = true
                            scope.launch {
                                val response = sendToAI(msg)
                                chatHistory = chatHistory + ChatMessage(response, false)
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (userMessage.isNotBlank()) PurpleAccent else CardBg
                        )
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (userMessage.isNotBlank()) Color.White else TextSecondary
                    )
                }
            }
        }
    }
}