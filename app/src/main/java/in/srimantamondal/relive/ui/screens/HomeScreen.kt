package `in`.srimantamondal.relive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.srimantamondal.relive.data.model.ActivityRecord
import `in`.srimantamondal.relive.ui.HomeViewModel
import `in`.srimantamondal.relive.ui.UsageStatsHelper
import `in`.srimantamondal.relive.ui.theme.ReLiveTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

// Brand Colors
private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val DividerColor = Color(0xFF2A2A3E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val activities by viewModel.activities.collectAsState(initial = emptyList())
    val parentMode by viewModel.parentMode.collectAsState(initial = false)
    val passwordExists by viewModel.passwordExists.collectAsState()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showSetPasswordDialog by remember { mutableStateOf(false) }
    var showEnterPasswordDialog by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showUsageStatsDialog by remember { mutableStateOf(false) }
    var tempPassword by remember { mutableStateOf("") }
    var tempPasswordConfirm by remember { mutableStateOf("") }
    var desiredParentMode by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is HomeViewModel.UiEvent.Message ->
                    snackbarHostState.showSnackbar(event.text)
                else -> {}
            }
        }
    }

    if (showSettingsScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyBg)
        ) {
            ParentModeSettingsScreen(
                viewModel = viewModel,
                onBack = { showSettingsScreen = false }
            )
            TextButton(
                onClick = { showSettingsScreen = false },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 8.dp)
            ) {
                Text("← Back", color = PurpleAccent)
            }
        }
        return
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "ReLive",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = { showSettingsScreen = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = PurpleAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NavyBg
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddSheet = true },
                    containerColor = PurpleAccent
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1C2541)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleAccent,
                        selectedTextColor = PurpleAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = Color(0xFF2A2A3E)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Commit") },
                    label = { Text("Commit") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleAccent,
                        selectedTextColor = PurpleAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = Color(0xFF2A2A3E)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Spa, contentDescription = "Wellness") },
                    label = { Text("Wellness") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleAccent,
                        selectedTextColor = PurpleAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = Color(0xFF2A2A3E)
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyBg)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeTab(
                    activities = activities,
                    parentMode = parentMode,
                    onActivityClicked = { activity -> viewModel.onActivityClicked(activity) },
                    onToggleParentMode = { enabled ->
                        desiredParentMode = enabled
                        if (enabled) {
                            if (!viewModel.hasParentPassword()) {
                                tempPassword = ""
                                tempPasswordConfirm = ""
                                showSetPasswordDialog = true
                            } else {
                                if (!UsageStatsHelper.hasUsageStatsPermission(context)) {
                                    showUsageStatsDialog = true
                                } else {
                                    viewModel.setParentMode(true)
                                }
                            }
                        } else {
                            tempPassword = ""
                            showEnterPasswordDialog = true
                        }
                    },
                    onAddActivity = { title, notes ->
                        viewModel.addActivity(title, notes)
                    }
                )
                1 -> CommitTabScreen()
                2 -> WellnessTabScreen()
            }
        }

        // Usage Stats Permission Dialog
        if (showUsageStatsDialog) {
            AlertDialog(
                onDismissRequest = { showUsageStatsDialog = false },
                containerColor = CardBg,
                title = {
                    Text("Permission Required", color = TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        "ReLive needs Usage Access permission to monitor app usage and protect your device.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showUsageStatsDialog = false
                        UsageStatsHelper.openUsageAccessSettings(context)
                        viewModel.setParentMode(true)
                    }) {
                        Text("Grant Permission", color = PurpleAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showUsageStatsDialog = false
                        viewModel.setParentMode(true)
                    }) {
                        Text("Skip", color = TextSecondary)
                    }
                }
            )
        }

        // Add Activity Bottom Sheet
        if (showAddSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddSheet = false },
                containerColor = CardBg
            ) {
                AddActivityBottomSheet(
                    onCancel = { showAddSheet = false },
                    onSave = { title, notes ->
                        viewModel.addActivity(title.trim().ifEmpty { "Untitled" }, notes)
                        showAddSheet = false
                        scope.launch { snackbarHostState.showSnackbar("Activity saved") }
                    }
                )
            }
        }

        // Set Password Dialog
        if (showSetPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showSetPasswordDialog = false },
                containerColor = CardBg,
                title = {
                    Text("Set Parent Password", color = TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempPassword,
                            onValueChange = { tempPassword = it },
                            label = { Text("Password", color = TextSecondary) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleAccent,
                                unfocusedBorderColor = DividerColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tempPasswordConfirm,
                            onValueChange = { tempPasswordConfirm = it },
                            label = { Text("Confirm Password", color = TextSecondary) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleAccent,
                                unfocusedBorderColor = DividerColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (tempPassword.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Password cannot be empty") }
                            return@TextButton
                        }
                        if (tempPassword != tempPasswordConfirm) {
                            scope.launch { snackbarHostState.showSnackbar("Passwords do not match") }
                            return@TextButton
                        }
                        viewModel.setParentPassword(tempPassword)
                        viewModel.setParentMode(true)
                        showSetPasswordDialog = false
                        desiredParentMode = null
                        tempPassword = ""
                        tempPasswordConfirm = ""
                    }) {
                        Text("Set & Enable", color = PurpleAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showSetPasswordDialog = false
                        desiredParentMode = null
                    }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // Enter Password Dialog
        if (showEnterPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showEnterPasswordDialog = false },
                containerColor = CardBg,
                title = {
                    Text("Enter Parent Password", color = TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    OutlinedTextField(
                        value = tempPassword,
                        onValueChange = { tempPassword = it },
                        label = { Text("Password", color = TextSecondary) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleAccent,
                            unfocusedBorderColor = DividerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (viewModel.verifyParentPassword(tempPassword)) {
                            viewModel.setParentMode(false)
                            showEnterPasswordDialog = false
                            desiredParentMode = null
                            tempPassword = ""
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Incorrect password") }
                        }
                    }) {
                        Text("Unlock", color = PurpleAccent)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showEnterPasswordDialog = false
                        desiredParentMode = null
                        tempPassword = ""
                    }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun CommitTabScreen() {
    var selectedCommitTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
    ) {
        TabRow(
            selectedTabIndex = selectedCommitTab,
            containerColor = Color(0xFF1C2541),
            contentColor = PurpleAccent
        ) {
            Tab(
                selected = selectedCommitTab == 0,
                onClick = { selectedCommitTab = 0 },
                text = {
                    Text(
                        "Focus",
                        color = if (selectedCommitTab == 0) PurpleAccent else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            )
            Tab(
                selected = selectedCommitTab == 1,
                onClick = { selectedCommitTab = 1 },
                text = {
                    Text(
                        "Study",
                        color = if (selectedCommitTab == 1) PurpleAccent else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            )
            Tab(
                selected = selectedCommitTab == 2,
                onClick = { selectedCommitTab = 2 },
                text = {
                    Text(
                        "Habits",
                        color = if (selectedCommitTab == 2) PurpleAccent else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            )
        }

        when (selectedCommitTab) {
            0 -> FocusModeScreen()
            1 -> StudyModeScreen()
            2 -> HabitTrackerScreen()
        }
    }
}

@Composable
fun WellnessTabScreen() {
    var selectedWellnessTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedWellnessTab,
            containerColor = Color(0xFF1C2541),
            contentColor = PurpleAccent,
            edgePadding = 0.dp
        ) {
            Tab(
                selected = selectedWellnessTab == 0,
                onClick = { selectedWellnessTab = 0 },
                text = {
                    Text(
                        "Dashboard",
                        color = if (selectedWellnessTab == 0) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
            Tab(
                selected = selectedWellnessTab == 1,
                onClick = { selectedWellnessTab = 1 },
                text = {
                    Text(
                        "Daily",
                        color = if (selectedWellnessTab == 1) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
            Tab(
                selected = selectedWellnessTab == 2,
                onClick = { selectedWellnessTab = 2 },
                text = {
                    Text(
                        "Weekly",
                        color = if (selectedWellnessTab == 2) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
            Tab(
                selected = selectedWellnessTab == 3,
                onClick = { selectedWellnessTab = 3 },
                text = {
                    Text(
                        "Limits",
                        color = if (selectedWellnessTab == 3) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
            Tab(
                selected = selectedWellnessTab == 4,
                onClick = { selectedWellnessTab = 4 },
                text = {
                    Text(
                        "Water",
                        color = if (selectedWellnessTab == 4) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
            Tab(
                selected = selectedWellnessTab == 5,
                onClick = { selectedWellnessTab = 5 },
                text = {
                    Text(
                        "Sleep",
                        color = if (selectedWellnessTab == 5) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
            Tab(
                selected = selectedWellnessTab == 6,
                onClick = { selectedWellnessTab = 6 },
                text = {
                    Text(
                        "Mood",
                        color = if (selectedWellnessTab == 6) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
            Tab(
                selected = selectedWellnessTab == 7,
                onClick = { selectedWellnessTab = 7 },
                text = {
                    Text(
                        "BMI",
                        color = if (selectedWellnessTab == 7) PurpleAccent else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            )
        }

        when (selectedWellnessTab) {
            0 -> UsageDashboardScreen()
            1 -> DailyReportScreen()
            2 -> WeeklyReportScreen()
            3 -> ScreenTimeLimitScreen()
            4 -> WaterReminderScreen()
            5 -> SleepTrackerScreen()
            6 -> MoodTrackerScreen()
            7 -> BMICalculatorScreen()
        }
    }
}

@Composable
fun HomeTab(
    activities: List<ActivityRecord>,
    parentMode: Boolean,
    onActivityClicked: (ActivityRecord) -> Unit,
    onToggleParentMode: (Boolean) -> Unit,
    onAddActivity: (title: String, notes: String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Welcome to ReLive",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Your Digital Wellbeing Assistant",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Parent Mode",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Switch(
                    checked = parentMode,
                    onCheckedChange = onToggleParentMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PurpleAccent,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DividerColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Activities",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = { onAddActivity("Untitled", null) }) {
                Text("+ Add", color = PurpleAccent)
            }
        }

        if (activities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No activities yet — tap + to add",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(activities) { activity ->
                    ActivityItem(activity = activity, onClick = { onActivityClicked(activity) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ActivityItem(activity: ActivityRecord, onClick: () -> Unit) {
    val dateText = remember(activity.timestamp) {
        try {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(Date(activity.timestamp))
        } catch (e: Exception) { "" }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                activity.title.ifEmpty { "Untitled" },
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!activity.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    activity.notes,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                dateText,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityBottomSheet(
    onCancel: () -> Unit,
    onSave: (title: String, notes: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(16.dp)
    ) {
        Text(
            "Add Activity",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurpleAccent,
                unfocusedBorderColor = DividerColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)", color = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurpleAccent,
                unfocusedBorderColor = DividerColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel", color = TextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onSave(title.trim(), notes.trim().ifEmpty { null }) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
            ) {
                Text("Save", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    ReLiveTheme {
        HomeTab(
            activities = listOf(
                ActivityRecord(
                    id = 1L,
                    title = "Walk",
                    notes = "Morning walk",
                    timestamp = System.currentTimeMillis()
                )
            ),
            parentMode = false,
            onActivityClicked = {},
            onToggleParentMode = {},
            onAddActivity = { _, _ -> }
        )
    }
}