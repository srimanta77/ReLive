package `in`.srimantamondal.relive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val OrangeAccent = Color(0xFFFFB74D)
private val RedAccent = Color(0xFFFF5252)
private val BlueAccent = Color(0xFF40C4FF)

@Composable
fun BMICalculatorScreen() {
    var heightCm by remember { mutableStateOf(170f) }
    var weightKg by remember { mutableStateOf(70f) }

    val bmi = weightKg / ((heightCm / 100f) * (heightCm / 100f))
    val bmiCategory = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25f -> "Normal"
        bmi < 30f -> "Overweight"
        else -> "Obese"
    }
    val bmiColor = when {
        bmi < 18.5f -> BlueAccent
        bmi < 25f -> GreenAccent
        bmi < 30f -> OrangeAccent
        else -> RedAccent
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "BMI Calculator",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Track your Body Mass Index",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        item {
            // BMI Result Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        String.format("%.1f", bmi),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = bmiColor
                    )
                    Text(
                        bmiCategory,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = bmiColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // BMI Scale
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "Under\nweight" to BlueAccent,
                            "Normal" to GreenAccent,
                            "Over\nweight" to OrangeAccent,
                            "Obese" to RedAccent
                        ).forEach { (label, color) ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(
                                            color = color,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    label,
                                    color = color,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Height slider
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Height",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "${heightCm.toInt()} cm",
                            color = PurpleAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Slider(
                        value = heightCm,
                        onValueChange = { heightCm = it },
                        valueRange = 100f..220f,
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleAccent,
                            activeTrackColor = PurpleAccent,
                            inactiveTrackColor = Color(0xFF2A2A3E)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("100 cm", color = TextSecondary, fontSize = 11.sp)
                        Text("220 cm", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            // Weight slider
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Weight",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "${weightKg.toInt()} kg",
                            color = PurpleAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Slider(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        valueRange = 30f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleAccent,
                            activeTrackColor = PurpleAccent,
                            inactiveTrackColor = Color(0xFF2A2A3E)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("30 kg", color = TextSecondary, fontSize = 11.sp)
                        Text("200 kg", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            // BMI Ranges info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1628)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "BMI Ranges",
                        color = PurpleAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        Triple("Below 18.5", "Underweight", BlueAccent),
                        Triple("18.5 – 24.9", "Normal weight", GreenAccent),
                        Triple("25.0 – 29.9", "Overweight", OrangeAccent),
                        Triple("30.0 and above", "Obese", RedAccent)
                    ).forEach { (range, label, color) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(range, color = TextSecondary, fontSize = 12.sp)
                            Text(
                                label,
                                color = color,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}