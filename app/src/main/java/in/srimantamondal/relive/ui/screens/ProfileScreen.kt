package `in`.srimantamondal.relive.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val RedAccent = Color(0xFFFF5252)

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            // Profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(PurpleAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (user?.displayName?.firstOrNull() ?: user?.email?.firstOrNull()
                            ?: "U").toString().uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        user?.displayName ?: "ReLive User",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        user?.email ?: "No email",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Verified badge
                    if (user?.isEmailVerified == true) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = GreenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Email Verified",
                                color = GreenAccent,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            // Account info
            Text(
                "Account Info",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Name",
                        value = user?.displayName ?: "Not set"
                    )
                    Divider(color = Color(0xFF2A2A3E))
                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = user?.email ?: "Not set"
                    )
                    Divider(color = Color(0xFF2A2A3E))
                    ProfileInfoRow(
                        icon = Icons.Default.AccessTime,
                        label = "Member since",
                        value = user?.metadata?.creationTimestamp?.let {
                            val date = java.util.Date(it)
                            java.text.SimpleDateFormat(
                                "MMM yyyy",
                                java.util.Locale.getDefault()
                            ).format(date)
                        } ?: "Unknown"
                    )
                }
            }
        }

        item {
            Text(
                "App Settings",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    ProfileActionRow(
                        icon = Icons.Default.Notifications,
                        label = "Notifications",
                        iconTint = PurpleAccent,
                        onClick = {}
                    )
                    Divider(color = Color(0xFF2A2A3E))
                    ProfileActionRow(
                        icon = Icons.Default.Security,
                        label = "Privacy & Security",
                        iconTint = PurpleAccent,
                        onClick = {}
                    )
                    Divider(color = Color(0xFF2A2A3E))
                    ProfileActionRow(
                        icon = Icons.Default.Info,
                        label = "About ReLive",
                        iconTint = PurpleAccent,
                        onClick = {}
                    )
                }
            }
        }

        item {
            // Logout button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedAccent.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = RedAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Logout",
                    color = RedAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = CardBg,
            title = {
                Text("Logout", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to logout?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    auth.signOut()
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Logout", color = RedAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 11.sp)
            Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProfileActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = TextPrimary, fontSize = 14.sp)
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}