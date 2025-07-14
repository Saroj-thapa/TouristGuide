package com.example.touristguide.guest

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

object GuestManager {
    private const val PREFS_NAME = "guest_prefs"
    private const val KEY_GUEST_ID = "guest_id"

    fun getOrCreateGuestId(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var guestId = prefs.getString(KEY_GUEST_ID, null)
        if (guestId == null) {
            guestId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_GUEST_ID, guestId).apply()
        }
        return guestId
    }

    fun clearGuestId(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_GUEST_ID).apply()
    }
}