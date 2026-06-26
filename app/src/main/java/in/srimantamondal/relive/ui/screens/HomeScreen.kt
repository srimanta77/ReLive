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
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.srimantamondal.relive.ui.HomeViewModel
import `in`.srimantamondal.relive.ui.theme.ReLiveTheme
import `in`.srimantamondal.relive.data.model.ActivityRecord
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    // collect flows from VM into Compose state
    val activities by viewModel.activities.collectAsState(initial = emptyList())
    val parentMode by viewModel.parentMode.collectAsState(initial = false)
    val passwordExists by viewModel.passwordExists.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // bottom sheet / dialog states
    var showAddSheet by remember { mutableStateOf(false) }
    var showSetPasswordDialog by remember { mutableStateOf(false) }
    var showEnterPasswordDialog by remember { mutableStateOf(false) }

    // temp state for password entry
    var tempPassword by remember { mutableStateOf("") }
    var tempPasswordConfirm by remember { mutableStateOf("") }

    // desiredParentMode tracks the UI toggle target while dialogs operate
    var desiredParentMode by remember { mutableStateOf<Boolean?>(null) }

    // Collect simple UI messages from ViewModel
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is HomeViewModel.UiEvent.Message -> {
                    snackbarHostState.showSnackbar(event.text)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("ReLive", style = MaterialTheme.typography.titleLarge) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        bottomBar = {
            var selectedTab by remember { mutableStateOf(0) }
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Commit") },
                    label = { Text("Commit") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Spa, contentDescription = "Wellness") },
                    label = { Text("Wellness") }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            HomeTab(
                activities = activities,
                parentMode = parentMode,
                onActivityClicked = { activity -> viewModel.onActivityClicked(activity) },
                onToggleParentMode = { enabled ->
                    // when user toggles the UI switch:
                    desiredParentMode = enabled
                    if (enabled) {
                        // enabling: if no password exists -> ask to set password first
                        if (!viewModel.hasParentPassword()) {
                            tempPassword = ""
                            tempPasswordConfirm = ""
                            showSetPasswordDialog = true
                        } else {
                            viewModel.setParentMode(true)
                        }
                    } else {
                        // disabling -> ask for password verification
                        tempPassword = ""
                        showEnterPasswordDialog = true
                    }
                },
                onAddActivity = { title, notes ->
                    viewModel.addActivity(title, notes)
                    scope.launch { snackbarHostState.showSnackbar("Activity added") }
                }
            )
        }

        // Add Activity bottom sheet
        if (showAddSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddSheet = false }
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

        // Set Password Dialog (shown when enabling parent mode first time)
        if (showSetPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showSetPasswordDialog = false },
                title = { Text("Set Parent Mode Password") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempPassword,
                            onValueChange = { tempPassword = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tempPasswordConfirm,
                            onValueChange = { tempPasswordConfirm = it },
                            label = { Text("Confirm Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // validate
                        if (tempPassword.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Password cannot be empty") }
                            return@TextButton
                        }
                        if (tempPassword != tempPasswordConfirm) {
                            scope.launch { snackbarHostState.showSnackbar("Passwords do not match") }
                            return@TextButton
                        }
                        // save password and enable parent mode
                        viewModel.setParentPassword(tempPassword)
                        viewModel.setParentMode(true)
                        showSetPasswordDialog = false
                        desiredParentMode = null
                        tempPassword = ""
                        tempPasswordConfirm = ""
                        scope.launch { snackbarHostState.showSnackbar("Parent Mode enabled") }
                    }) {
                        Text("Set & Enable")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        // cancel -> do nothing (leave switch unchanged)
                        showSetPasswordDialog = false
                        desiredParentMode = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Enter Password Dialog (shown when disabling parent mode)
        if (showEnterPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showEnterPasswordDialog = false },
                title = { Text("Enter Parent Password") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempPassword,
                            onValueChange = { tempPassword = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val ok = viewModel.verifyParentPassword(tempPassword)
                        if (ok) {
                            viewModel.setParentMode(false)
                            showEnterPasswordDialog = false
                            desiredParentMode = null
                            tempPassword = ""
                            scope.launch { snackbarHostState.showSnackbar("Parent Mode disabled") }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Incorrect password") }
                        }
                    }) {
                        Text("Unlock")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        // Cancel -> keep parent mode ON (do not change)
                        showEnterPasswordDialog = false
                        desiredParentMode = null
                        tempPassword = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    activities: List<ActivityRecord>,
    parentMode: Boolean,
    onActivityClicked: (ActivityRecord) -> Unit,
    onToggleParentMode: (Boolean) -> Unit,
    onAddActivity: (title: String, notes: String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(12.dp))
        Text("Welcome to ReLive", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Your Digital Wellbeing Assistant", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))

        // Parent mode toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Parent Mode", style = MaterialTheme.typography.bodyLarge)
             Switch(checked = parentMode, onCheckedChange = onToggleParentMode)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Add button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { onAddActivity("Untitled", null) }) {
                Text("Add activity")
            }
        }

        // Activities list
        if (activities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No activities yet — tap + to add", style = MaterialTheme.typography.bodyMedium)
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
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            formatter.format(Date(activity.timestamp))
        } catch (e: Exception) {
            ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
        ) {
            Text(activity.title.ifEmpty { "Untitled" }, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(activity.notes ?: "", style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Recorded:", style = MaterialTheme.typography.bodySmall)
                Text(dateText, style = MaterialTheme.typography.bodySmall)
            }
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
    val canSave = title.isNotBlank()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
        Text("Add Activity", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onSave(title.trim(), notes.trim().ifEmpty { null }) }, enabled = canSave) {
                Text("Save")
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
                ActivityRecord(id = 1L, title = "Walk", notes = "Morning walk", timestamp = System.currentTimeMillis())
            ),
            parentMode = false,
            onActivityClicked = {},
            onToggleParentMode = {},
            onAddActivity = { _, _ -> }
        )
    }
}
