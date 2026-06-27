package `in`.srimantamondal.relive.ui

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.channels.BufferOverflow
import `in`.srimantamondal.relive.data.db.ReLiveDatabase
import `in`.srimantamondal.relive.data.db.ReLiveDao
import `in`.srimantamondal.relive.data.model.ActivityRecord
import `in`.srimantamondal.relive.security.PasswordManager
import `in`.srimantamondal.relive.parent.ParentModeService

private val Application.dataStore by preferencesDataStore(name = "relive_prefs")

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val parentModeKey = booleanPreferencesKey("parent_mode_enabled")

    private val db: ReLiveDatabase = ReLiveDatabase.getInstance(application)
    private val dao: ReLiveDao = db.reliveDao()

    val activities: StateFlow<List<ActivityRecord>> =
        dao.getAllActivities()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val parentMode: StateFlow<Boolean> = application.dataStore.data
        .map { prefs -> prefs[parentModeKey] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val passwordManager = PasswordManager(application.applicationContext)

    private val _passwordExists = MutableStateFlow(passwordManager.hasPassword())
    val passwordExists: StateFlow<Boolean> = _passwordExists.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    // ---------------- Activity operations ----------------

    fun addActivity(title: String, notes: String? = null) {
        val record = ActivityRecord(
            id = 0L,
            title = title,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            try {
                dao.insert(record)
                _uiEvents.emit(UiEvent.Message("Activity added"))
            } catch (t: Throwable) {
                _uiEvents.emit(UiEvent.Message("Failed to add activity: ${t.localizedMessage}"))
            }
        }
    }

    fun deleteActivity(record: ActivityRecord) {
        viewModelScope.launch {
            try {
                dao.delete(record)
                _uiEvents.emit(UiEvent.Message("Activity removed"))
            } catch (t: Throwable) {
                _uiEvents.emit(UiEvent.Message("Failed to remove: ${t.localizedMessage}"))
            }
        }
    }

    fun updateActivity(record: ActivityRecord) {
        viewModelScope.launch {
            try {
                dao.update(record)
                _uiEvents.emit(UiEvent.Message("Activity updated"))
            } catch (t: Throwable) {
                _uiEvents.emit(UiEvent.Message("Failed to update: ${t.localizedMessage}"))
            }
        }
    }

    fun onActivityClicked(activity: ActivityRecord) {
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.Message("Clicked: ${activity.title}"))
        }
    }

    fun showTip(message: String) {
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.Message(message))
        }
    }

    // ---------------- Parent Mode & Password ----------------

    fun hasParentPassword(): Boolean = passwordManager.hasPassword()

    fun setParentPassword(plainPassword: String) {
        passwordManager.savePassword(plainPassword)
        _passwordExists.value = true
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.Message("Parent password saved"))
        }
    }

    fun verifyParentPassword(plainPassword: String): Boolean {
        return passwordManager.verify(plainPassword)
    }

    fun clearParentPassword() {
        passwordManager.clearPassword()
        _passwordExists.value = false
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.Message("Parent password cleared"))
        }
    }

    fun setParentMode(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[parentModeKey] = enabled
            }

            val context = getApplication<Application>()
            val intent = Intent(context, ParentModeService::class.java).apply {
                action = if (enabled) ParentModeService.ACTION_START
                else ParentModeService.ACTION_STOP
            }

            if (enabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } else {
                context.stopService(intent)
            }

            _uiEvents.emit(
                UiEvent.Message(
                    if (enabled) "Parent mode enabled" else "Parent mode disabled"
                )
            )
        }
    }

    sealed class UiEvent {
        data class Message(val text: String) : UiEvent()
        data class Navigate(val route: String) : UiEvent()
        object Dismiss : UiEvent()
    }
}