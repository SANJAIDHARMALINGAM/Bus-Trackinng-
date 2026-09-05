package com.example.bustracking.data

import java.util.regex.Pattern

/**
 * Interface for Backend Authentication Service.
 */
sealed class AuthResult {
    data class Success(val userId: String, val displayName: String, val token: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Strict email validator utility.
 * Only accepts valid Gmail addresses (e.g., username@gmail.com).
 */
object EmailValidator {
    // Standard Gmail regex: letters, numbers, dots, and + signs allowed before @gmail.com
    private val GMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.+_-]{3,}@gmail\\.com$", Pattern.CASE_INSENSITIVE)

    fun isValidGmail(email: String): Boolean {
        val trimmed = email.trim()
        return GMAIL_PATTERN.matcher(trimmed).matches()
    }
}

interface AuthService {
    suspend fun signInWithEmail(identifier: String, password: String): AuthResult
    suspend fun signUpWithDetails(
        fullName: String,
        gender: String,
        mobileNumber: String,
        email: String,
        password: String
    ): AuthResult
    suspend fun signUpWithEmail(identifier: String, password: String): AuthResult
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signInWithPhone(phoneNumber: String): AuthResult
    suspend fun resetPassword(identifier: String): AuthResult
}

/**
 * Default Front-end Mock implementation for seamless UI testing without crashing.
 * Enforces strict Gmail verification.
 */
class MockAuthService : AuthService {
    override suspend fun signInWithEmail(identifier: String, password: String): AuthResult {
        val trimmed = identifier.trim()
        if (!EmailValidator.isValidGmail(trimmed)) {
            return AuthResult.Error("Please enter a valid verified Gmail account (e.g. name@gmail.com).")
        }
        if (password.length < 4) {
            return AuthResult.Error("Password must be at least 4 characters.")
        }
        return AuthResult.Success(
            userId = "user_101",
            displayName = trimmed.substringBefore("@"),
            token = "jwt_mock_token_abc123"
        )
    }

    override suspend fun signUpWithDetails(
        fullName: String,
        gender: String,
        mobileNumber: String,
        email: String,
        password: String
    ): AuthResult {
        val trimmedEmail = email.trim()
        if (fullName.isBlank()) {
            return AuthResult.Error("Please enter your full name.")
        }
        if (mobileNumber.length < 10) {
            return AuthResult.Error("Please enter a valid 10-digit mobile number.")
        }
        if (!EmailValidator.isValidGmail(trimmedEmail)) {
            return AuthResult.Error("Please enter a valid verified Gmail address (@gmail.com).")
        }
        if (password.length < 4) {
            return AuthResult.Error("Password must be at least 4 characters.")
        }

        return AuthResult.Success(
            userId = "user_new_${System.currentTimeMillis()}",
            displayName = fullName,
            token = "jwt_mock_signup_token"
        )
    }

    override suspend fun signUpWithEmail(identifier: String, password: String): AuthResult {
        val trimmed = identifier.trim()
        if (!EmailValidator.isValidGmail(trimmed)) {
            return AuthResult.Error("Please enter a valid verified Gmail address (@gmail.com).")
        }
        return if (password.length >= 4) {
            AuthResult.Success(
                userId = "user_new_${System.currentTimeMillis()}",
                displayName = trimmed.substringBefore("@"),
                token = "jwt_mock_signup_token"
            )
        } else {
            AuthResult.Error("Password must be at least 4 characters.")
        }
    }

    override suspend fun signInWithGoogle(): AuthResult {
        return AuthResult.Success(
            userId = "google_user_99",
            displayName = "Google Commuter",
            token = "google_oauth_token"
        )
    }

    override suspend fun signInWithPhone(phoneNumber: String): AuthResult {
        return AuthResult.Success(
            userId = "phone_user_88",
            displayName = "Mobile Commuter",
            token = "phone_otp_token"
        )
    }

    override suspend fun resetPassword(identifier: String): AuthResult {
        val trimmed = identifier.trim()
        if (!EmailValidator.isValidGmail(trimmed)) {
            return AuthResult.Error("Please enter a valid Gmail address to receive reset link.")
        }
        return AuthResult.Success(
            userId = "",
            displayName = "",
            token = "Password reset link sent to $trimmed"
        )
    }
}

/**
 * Backend Connection Manager Singleton.
 */
object BackendManager {
    var authService: AuthService = MockAuthService()
    
    var BASE_URL: String = "https://api.bustracking.com/v1/"
}
