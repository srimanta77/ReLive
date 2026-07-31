package `in`.srimantamondal.relive.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import `in`.srimantamondal.relive.BuildConfig
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import `in`.srimantamondal.relive.data.model.UserProfile
import `in`.srimantamondal.relive.data.repository.UserProfileRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

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
    val repository = remember { UserProfileRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Firestore-synced profile (name + photo). Falls back to FirebaseAuth's
    // own displayName/email until the first snapshot arrives.
    var syncedProfile by remember { mutableStateOf<UserProfile?>(null) }
    val photoBitmap = remember(syncedProfile?.photoBase64) {
        syncedProfile?.photoBase64?.let { b64 ->
            runCatching {
                val bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()
        }
    }
    val displayName = syncedProfile?.name?.takeIf { it.isNotBlank() }
        ?: user?.displayName ?: "ReLive User"

    // Live listener -> updates on THIS device whenever the doc changes on
    // any device signed into the same account (cross-device sync).
    LaunchedEffect(user?.uid) {
        val uid = user?.uid ?: return@LaunchedEffect
        repository.observeProfile(uid).collect { profile ->
            syncedProfile = profile
        }
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
                        if (photoBitmap != null) {
                            Image(
                                bitmap = photoBitmap.asImageBitmap(),
                                contentDescription = "Profile photo",
                                modifier = Modifier.size(80.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                (displayName.firstOrNull() ?: user?.email?.firstOrNull()
                                ?: "U").toString().uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit profile",
                                tint = PurpleAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
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
                        value = displayName
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
                        onClick = {
                            val intent = if (android.os.Build.VERSION.SDK_INT >= 26) {
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                            } else {
                                // Pre-Android 8: no per-app notification settings screen exists,
                                // fall back to the general app details page.
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            }
                            context.startActivity(intent)
                        }
                    )
                    Divider(color = Color(0xFF2A2A3E))
                    ProfileActionRow(
                        icon = Icons.Default.Security,
                        label = "Privacy & Security",
                        iconTint = PurpleAccent,
                        onClick = { showPrivacyDialog = true }
                    )
                    Divider(color = Color(0xFF2A2A3E))
                    ProfileActionRow(
                        icon = Icons.Default.Info,
                        label = "About ReLive",
                        iconTint = PurpleAccent,
                        onClick = { showAboutDialog = true }
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

    // Edit profile dialog (name + photo)
    if (showEditDialog && user != null) {
        EditProfileDialog(
            initialName = displayName,
            initialBitmap = photoBitmap,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newBitmap ->
                scope.launch {
                    val photoBase64 = newBitmap?.let { bitmapToBase64(it) }
                        ?: syncedProfile?.photoBase64
                    // Keep FirebaseAuth's own displayName in sync too, since
                    // other parts of the app (and Firebase console) read it.
                    val profileUpdates = com.google.firebase.auth
                        .UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()
                    runCatching { user.updateProfile(profileUpdates).await() }
                    runCatching {
                        repository.saveProfile(
                            uid = user.uid,
                            name = newName,
                            email = user.email ?: "",
                            photoBase64 = photoBase64
                        )
                    }
                    showEditDialog = false
                }
            }
        )
    }

    // About dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = CardBg,
            title = { Text("About ReLive", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "ReLive",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Your Digital Wellbeing Assistant",
                        color = PurpleAccent,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Version ${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "ReLive helps you understand and manage how you use your phone. " +
                                "It combines screen-time tracking, parental controls, focus and " +
                                "study tools, and simple wellness tracking (water, sleep, mood, " +
                                "BMI) with an AI coach — all in one place.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("What's inside", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf(
                        "Screen time dashboard, daily & weekly reports",
                        "Parent Mode with encrypted password protection",
                        "Focus Mode & Study Mode (Pomodoro-style timers)",
                        "Water, sleep, mood & BMI tracking",
                        "AI Coach for guidance",
                        "Cloud-synced profile across devices"
                    ).forEach {
                        Text("•  $it", color = TextSecondary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "Developed by Srimanta Mondal",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "github.com/srimanta77/ReLive",
                        color = PurpleAccent,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "© 2026 Srimanta Mondal. All rights reserved.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = PurpleAccent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Privacy & Security dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = CardBg,
            title = { Text("Privacy & Security", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "What we collect",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf(
                        "Account info: name, email, and profile photo (via Firebase Authentication)",
                        "App usage data: screen time and app-open counts, read only with your explicit Usage Access permission",
                        "Wellness entries you add yourself: water, sleep, mood, BMI, habits, focus/study sessions"
                    ).forEach {
                        Text("•  $it", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "How it's stored",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf(
                        "Profile (name, email, photo) is stored in Cloud Firestore, secured by rules so only you can read or write your own document",
                        "Screen time, habits, and wellness entries are stored locally on your device (Room database)",
                        "Parent Mode password is stored using Android's EncryptedSharedPreferences, not in plain text"
                    ).forEach {
                        Text("•  $it", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "What we don't do",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf(
                        "We don't sell or share your personal data with third parties",
                        "We don't read the content of other apps — only aggregate usage time via Android's official Usage Access API",
                        "You can revoke Usage Access anytime from your device Settings"
                    ).forEach {
                        Text("•  $it", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Your controls",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "You can edit or delete your profile info anytime from this screen, " +
                                "and sign out to stop sync on this device.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = PurpleAccent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    initialName: String,
    initialBitmap: Bitmap?,
    onDismiss: () -> Unit,
    onSave: (String, Bitmap?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var pickedBitmap by remember { mutableStateOf(initialBitmap) }
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }.getOrNull()?.let { pickedBitmap = it }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor = CardBg,
        title = { Text("Edit Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PurpleAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (pickedBitmap != null) {
                        Image(
                            bitmap = pickedBitmap!!.asImageBitmap(),
                            contentDescription = "Selected photo",
                            modifier = Modifier.size(72.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PurpleAccent)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {
                    photoPicker.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
                    Text("Change Photo", color = PurpleAccent)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && !isSaving,
                onClick = {
                    isSaving = true
                    onSave(name.trim(), pickedBitmap)
                }
            ) {
                Text("Save", color = PurpleAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isSaving) onDismiss() }) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/** Downscales + compresses so the Base64 string stays well under Firestore's 1MB doc limit. */
private fun bitmapToBase64(bitmap: Bitmap): String {
    val maxDimension = 300
    val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
    val scaled = if (scale < 1f) {
        Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt().coerceAtLeast(1),
            (bitmap.height * scale).toInt().coerceAtLeast(1),
            true
        )
    } else bitmap
    val stream = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.DEFAULT)
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
            .clickable(onClick = onClick)
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