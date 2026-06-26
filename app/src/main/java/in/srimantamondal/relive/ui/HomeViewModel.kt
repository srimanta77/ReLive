package `in`.srimantamondal.relive.ui

import android.app.Application
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

// DataStore delegate for Application
private val Application.dataStore by preferencesDataStore(name = "relive_prefs")

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore key for parent mode flag (not sensitive)
    private val PARENT_MODE_KEY = booleanPreferencesKey("parent_mode_enabled")

    // DB / DAO
    private val db: ReLiveDatabase = ReLiveDatabase.getInstance(application)
    private val dao: ReLiveDao = db.reliveDao()

    // Activities flow -> expose as StateFlow for Compose
    val activities: StateFlow<List<ActivityRecord>> =
        dao.getAllActivities()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Parent mode boolean stored in DataStore
    val parentMode: StateFlow<Boolean> = application.dataStore.data
        .map { prefs -> prefs[PARENT_MODE_KEY] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Password manager (EncryptedSharedPreferences)
    private val passwordManager = PasswordManager(application.applicationContext)

    // Cached boolean whether password exists; used for quick checks in UI
    private val _passwordExists = MutableStateFlow(passwordManager.hasPassword())
    val passwordExists: StateFlow<Boolean> = _passwordExists.asStateFlow()

    // UI events for snackbars/navigation
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
        viewModelScope.launch { _uiEvents.emit(UiEvent.Message("Clicked: ${activity.title}")) }
    }

    fun showTip(message: String) {
        viewModelScope.launch { _uiEvents.emit(UiEvent.Message(message)) }
    }

    // ---------------- Parent-mode & password ----------------

    /**
     * Synchronous helper: returns true if a parent password exists.
     */
    fun hasParentPassword(): Boolean = passwordManager.hasPassword()

    /**
     * Save a parent password (EncryptedSharedPreferences) and update cached state.
     */
    fun setParentPassword(plainPassword: String) {
        passwordManager.savePassword(plainPassword)
        _passwordExists.value = true
        viewModelScope.launch { _uiEvents.emit(UiEvent.Message("Parent password saved")) }
    }

    /**
     * Verify a plain password against stored hash (sync, uses EncryptedSharedPrefs).
     */
    fun verifyParentPassword(plainPassword: String): Boolean {
        return passwordManager.verify(plainPassword)
    }

    /**
     * Clear stored password and update cache.
     */
    fun clearParentPassword() {
        passwordManager.clearPassword()
        _passwordExists.value = false
        viewModelScope.launch { _uiEvents.emit(UiEvent.Message("Parent password cleared")) }
    }

    /**
     * Toggle parent mode flag stored in DataStore.
     */
    fun setParentMode(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[PARENT_MODE_KEY] = enabled
            }
            _uiEvents.emit(UiEvent.Message(if (enabled) "Parent mode enabled" else "Parent mode disabled"))
        }
    }

    // UI events sealed
    sealed class UiEvent {
        data class Message(val text: String) : UiEvent()
        data class Navigate(val route: String) : UiEvent()
        object Dismiss : UiEvent()
    }
}
