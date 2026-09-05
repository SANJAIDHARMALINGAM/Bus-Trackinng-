package com.example.bustracking.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages persistent user preferences, session state, and first-launch onboarding flag.
 */
object AppPreferences {
    private const val PREFS_NAME = "bus_tracking_app_prefs"
    private const val KEY_HAS_SEEN_WELCOME = "has_seen_welcome_screen"
    private const val KEY_IS_LOGGED_IN = "is_user_logged_in"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Checks if the user has already opened the app and seen the welcome screen.
     */
    fun hasSeenWelcome(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_HAS_SEEN_WELCOME, false)
    }

    /**
     * Marks the welcome screen as seen so it never appears again on subsequent launches.
     */
    fun setHasSeenWelcome(context: Context, seen: Boolean = true) {
        getPrefs(context).edit().putBoolean(KEY_HAS_SEEN_WELCOME, seen).apply()
    }

    /**
     * Checks if the user is currently authenticated.
     */
    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Sets authentication status.
     */
    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()
    }

    private const val KEY_FAVORITE_ROUTES = "favorite_routes_json"
    private const val KEY_RECENT_SEARCHES = "recent_searches_list"

    fun getFavoriteRoutesJson(context: Context): String? {
        return getPrefs(context).getString(KEY_FAVORITE_ROUTES, null)
    }

    fun saveFavoriteRoutesJson(context: Context, json: String) {
        getPrefs(context).edit().putString(KEY_FAVORITE_ROUTES, json).apply()
    }

    fun getRecentSearches(context: Context): List<Pair<String, String>> {
        val json = getPrefs(context).getString(KEY_RECENT_SEARCHES, null) ?: return listOf(
            Pair("Bengaluru", "Mysuru"),
            Pair("Shivamogga", "Bengaluru")
        )
        return try {
            val array = org.json.JSONArray(json)
            val list = mutableListOf<Pair<String, String>>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val from = obj.optString("from", "")
                val to = obj.optString("to", "")
                if (from.isNotBlank() && to.isNotBlank()) {
                    list.add(Pair(from, to))
                }
            }
            if (list.isEmpty()) listOf(Pair("Bengaluru", "Mysuru"), Pair("Shivamogga", "Bengaluru")) else list
        } catch (e: Exception) {
            listOf(Pair("Bengaluru", "Mysuru"), Pair("Shivamogga", "Bengaluru"))
        }
    }

    fun addRecentSearch(context: Context, from: String, to: String) {
        val current = getRecentSearches(context).toMutableList()
        current.removeAll { it.first.equals(from, ignoreCase = true) && it.second.equals(to, ignoreCase = true) }
        current.add(0, Pair(from, to))
        val trimmed = current.take(10)
        try {
            val array = org.json.JSONArray()
            trimmed.forEach {
                val obj = org.json.JSONObject()
                obj.put("from", it.first)
                obj.put("to", it.second)
                array.put(obj)
            }
            getPrefs(context).edit().putString(KEY_RECENT_SEARCHES, array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRecentCities(context: Context): List<String> {
        val searches = getRecentSearches(context)
        val cities = linkedSetOf<String>()
        searches.forEach {
            cities.add(it.first)
            cities.add(it.second)
        }
        return cities.toList()
    }

    /**
     * Determines which destination to start on when the app launches:
     * 1. First time opening the app -> "welcome"
     * 2. Subsequent launches (logged in) -> "home"
     * 3. Subsequent launches (not logged in) -> "login"
     */
    fun getStartDestination(context: Context): String {
        val seenWelcome = hasSeenWelcome(context)
        if (!seenWelcome) {
            return "welcome"
        }
        val loggedIn = isLoggedIn(context)
        return if (loggedIn) "home" else "login"
    }
}

