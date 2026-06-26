package `in`.srimantamondal.relive.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

/**
 * Simple password manager using EncryptedSharedPreferences.
 * Stores a SHA-256 hash of the password (not the raw password).
 */
class PasswordManager(private val context: Context) {

    private val prefsName = "relive_secure_prefs"
    private val keyPasswordHash = "parent_mode_password_hash"

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            prefsName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun hash(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hasPassword(): Boolean {
        return prefs.contains(keyPasswordHash)
    }

    fun savePassword(password: String) {
        val h = hash(password)
        prefs.edit().putString(keyPasswordHash, h).apply()
    }

    fun clearPassword() {
        prefs.edit().remove(keyPasswordHash).apply()
    }

    fun verify(password: String): Boolean {
        val stored = prefs.getString(keyPasswordHash, null) ?: return false
        return stored == hash(password)
    }
}
