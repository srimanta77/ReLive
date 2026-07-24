package `in`.srimantamondal.relive.data.model

/**
 * User profile document stored in Firestore at users/{uid}.
 * photoBase64 holds a compressed JPEG encoded as Base64 (kept small,
 * so no Firebase Storage / Blaze plan is required).
 */
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoBase64: String? = null,
    val updatedAt: Long = 0L
)