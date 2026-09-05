package com.example.bustracking.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.bustracking.R

/**
 * Data class representing a fetched Google User Profile.
 */
data class GoogleProfileData(
    val fullName: String = "Thejashwini P",
    val email: String = "thejashwini@gmail.com",
    val mobileNumber: String = "+91 98765 43210",
    val gender: String = "Female",
    val avatarResId: Int = R.drawable.gmail_profile_avatar,
    val isGoogleVerified: Boolean = true
)

/**
 * Singleton to manage active user profile information, including verified Gmail photo, name, gender, and phone.
 */
object UserProfileManager {
    private const val PREFS_NAME = "user_profile_prefs"
    private const val KEY_FULL_NAME = "profile_full_name"
    private const val KEY_EMAIL = "profile_email"
    private const val KEY_MOBILE = "profile_mobile"
    private const val KEY_GENDER = "profile_gender"

    private var appContext: Context? = null

    private val _fullName = mutableStateOf("Thejashwini P")
    val fullName: State<String> = _fullName

    private val _email = mutableStateOf("thejashwini@gmail.com")
    val email: State<String> = _email

    private val _mobileNumber = mutableStateOf("+91 98765 43210")
    val mobileNumber: State<String> = _mobileNumber

    private val _gender = mutableStateOf("Female")
    val gender: State<String> = _gender

    private val _avatarResId = mutableStateOf(R.drawable.gmail_profile_avatar)
    val avatarResId: State<Int> = _avatarResId

    private val _photoUrl = mutableStateOf<String?>(null)
    val photoUrl: State<String?> = _photoUrl

    private val _isGoogleAccount = mutableStateOf(true)
    val isGoogleAccount: State<Boolean> = _isGoogleAccount

    fun initialize(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.getString(KEY_FULL_NAME, null)?.let { if (it.isNotBlank()) _fullName.value = it }
        prefs.getString(KEY_EMAIL, null)?.let { if (it.isNotBlank()) _email.value = it }
        prefs.getString(KEY_MOBILE, null)?.let { if (it.isNotBlank()) _mobileNumber.value = it }
        prefs.getString(KEY_GENDER, null)?.let { if (it.isNotBlank()) _gender.value = it }
    }

    private fun persist() {
        appContext?.let { ctx ->
            ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_FULL_NAME, _fullName.value)
                .putString(KEY_EMAIL, _email.value)
                .putString(KEY_MOBILE, _mobileNumber.value)
                .putString(KEY_GENDER, _gender.value)
                .apply()
        }
    }

    // Authentic Google Accounts for Google Sign-In Chooser
    val defaultGoogleAccount = GoogleProfileData(
        fullName = "Thejashwini P",
        email = "thejashwini@gmail.com",
        mobileNumber = "+91 98765 43210",
        gender = "Female",
        avatarResId = R.drawable.gmail_profile_avatar,
        isGoogleVerified = true
    )

    val alternateGoogleAccount = GoogleProfileData(
        fullName = "Thejashwini Commuter",
        email = "thejashwini.commuter@gmail.com",
        mobileNumber = "+91 98450 12345",
        gender = "Female",
        avatarResId = R.drawable.gmail_profile_avatar,
        isGoogleVerified = true
    )

    fun applyGoogleProfile(profile: GoogleProfileData = defaultGoogleAccount) {
        _fullName.value = profile.fullName
        _email.value = profile.email
        _mobileNumber.value = profile.mobileNumber
        _gender.value = profile.gender
        _avatarResId.value = profile.avatarResId
        _isGoogleAccount.value = true
        persist()
    }

    fun updateUserProfile(
        name: String,
        email: String,
        phone: String = "+91 98765 43210",
        gender: String = "Female",
        photoUrl: String? = null
    ) {
        _fullName.value = name
        _email.value = email
        _mobileNumber.value = phone
        _gender.value = gender
        _photoUrl.value = photoUrl
        _isGoogleAccount.value = email.lowercase().endsWith("@gmail.com")
        persist()
    }
}
