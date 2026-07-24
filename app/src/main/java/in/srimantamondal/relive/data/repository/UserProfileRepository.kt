package `in`.srimantamondal.relive.data.repository

import `in`.srimantamondal.relive.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun docFor(uid: String) =
        firestore.collection("users").document(uid)

    /** One-time fetch, used right after login/signup to check if a profile already exists. */
    suspend fun getProfile(uid: String): UserProfile? {
        val snapshot = docFor(uid).get().await()
        return snapshot.toObject(UserProfile::class.java)
    }

    /**
     * Live listener — fires immediately with cached/local data, then again whenever
     * this doc changes on ANY device signed into the same account. This is what
     * gives us cross-device sync on the Profile screen.
     */
    fun observeProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val registration = docFor(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(UserProfile::class.java))
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveProfile(uid: String, name: String, email: String, photoBase64: String?) {
        val profile = UserProfile(
            uid = uid,
            name = name,
            email = email,
            photoBase64 = photoBase64,
            updatedAt = System.currentTimeMillis()
        )
        docFor(uid).set(profile).await()
    }
}