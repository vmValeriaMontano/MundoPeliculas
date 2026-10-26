package com.example.mundopeliculas.utils

import android.content.Context
import com.example.mundopeliculas.R

object ErrorUtils {
    fun getFriendlyErrorMessage(context: Context, error: String?): String {
        return when {
            error == null -> context.getString(R.string.error_unknown)
            error.contains("network", ignoreCase = true) -> context.getString(R.string.error_network)
            error.contains("user-not-found", ignoreCase = true) -> context.getString(R.string.error_user_not_found)
            error.contains("wrong-password", ignoreCase = true) -> context.getString(R.string.error_invalid_credentials)
            error.contains("email-already-in-use", ignoreCase = true) -> context.getString(R.string.error_email_already_in_use)
            error.contains("invalid-email", ignoreCase = true) -> context.getString(R.string.error_invalid_email)
            error.contains("weak-password", ignoreCase = true) -> context.getString(R.string.error_password_weak)
            else -> context.getString(R.string.error_generic)
        }
    }
}