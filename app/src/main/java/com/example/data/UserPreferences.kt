package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences_ds")

class UserPreferences(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // DataStore Preference Keys
    private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    private val KEY_USER_ROLE = stringPreferencesKey("user_role")
    private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
    private val KEY_LARGE_TEXT = booleanPreferencesKey("large_text")
    private val KEY_FONT_SCALE = floatPreferencesKey("font_scale")
    private val KEY_FONT_WEIGHT = intPreferencesKey("font_weight_offset")
    private val KEY_GOLD_ACTIVATED = booleanPreferencesKey("gold_activated")
    private val KEY_HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
    private val KEY_VOICE_GENDER = stringPreferencesKey("voice_gender")
    private val KEY_ARABIC_FONT_TYPE = stringPreferencesKey("arabic_font_type")
    private val KEY_PERSIAN_FONT_TYPE = stringPreferencesKey("persian_font_type")
    private val KEY_SUBTITLE_MODE = stringPreferencesKey("subtitle_mode")
    private val KEY_PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
    private val KEY_THEME_PALETTE = stringPreferencesKey("theme_palette")

    // Reactive StateFlows backed by DataStore
    val onboardingCompleted: StateFlow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_ONBOARDING_COMPLETED] ?: false }
        .stateIn(scope, SharingStarted.Eagerly, false)

    val userRole: StateFlow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_USER_ROLE] ?: ROLE_PILGRIM }
        .stateIn(scope, SharingStarted.Eagerly, ROLE_PILGRIM)

    val darkTheme: StateFlow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_DARK_THEME] ?: true }
        .stateIn(scope, SharingStarted.Eagerly, true)

    val largeTextEnabled: StateFlow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_LARGE_TEXT] ?: false }
        .stateIn(scope, SharingStarted.Eagerly, false)

    val fontScale: StateFlow<Float> = context.dataStore.data
        .map { prefs -> prefs[KEY_FONT_SCALE] ?: 1.0f }
        .stateIn(scope, SharingStarted.Eagerly, 1.0f)

    val fontWeightOffset: StateFlow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_FONT_WEIGHT] ?: 0 }
        .stateIn(scope, SharingStarted.Eagerly, 0)

    val isGoldVersionActivated: StateFlow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_GOLD_ACTIVATED] ?: false }
        .stateIn(scope, SharingStarted.Eagerly, false)

    val hapticsEnabled: StateFlow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_HAPTICS_ENABLED] ?: true }
        .stateIn(scope, SharingStarted.Eagerly, true)

    val voiceGender: StateFlow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_VOICE_GENDER] ?: VOICE_MALE }
        .stateIn(scope, SharingStarted.Eagerly, VOICE_MALE)

    val arabicFontType: StateFlow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_ARABIC_FONT_TYPE] ?: "امیری" }
        .stateIn(scope, SharingStarted.Eagerly, "امیری")

    val persianFontType: StateFlow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_PERSIAN_FONT_TYPE] ?: "وزیرمتن" }
        .stateIn(scope, SharingStarted.Eagerly, "وزیرمتن")

    val subtitleMode: StateFlow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_SUBTITLE_MODE] ?: "عربی خوانا" }
        .stateIn(scope, SharingStarted.Eagerly, "عربی خوانا")

    val playbackSpeed: StateFlow<Float> = context.dataStore.data
        .map { prefs -> prefs[KEY_PLAYBACK_SPEED] ?: 1.0f }
        .stateIn(scope, SharingStarted.Eagerly, 1.0f)

    val themePalette: StateFlow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_THEME_PALETTE] ?: THEME_EMERALD }
        .stateIn(scope, SharingStarted.Eagerly, THEME_EMERALD)

    fun setThemePalette(theme: String) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_THEME_PALETTE] = theme
            }
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_ONBOARDING_COMPLETED] = completed
            }
        }
    }

    fun setUserRole(role: String) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_USER_ROLE] = role
            }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_DARK_THEME] = enabled
            }
        }
    }

    fun setLargeTextEnabled(enabled: Boolean) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_LARGE_TEXT] = enabled
            }
        }
    }

    fun setFontScale(scale: Float) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_FONT_SCALE] = scale
            }
        }
    }

    fun setFontWeightOffset(offset: Int) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_FONT_WEIGHT] = offset
            }
        }
    }

    fun setGoldVersionActivated(activated: Boolean) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_GOLD_ACTIVATED] = activated
            }
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_HAPTICS_ENABLED] = enabled
            }
        }
    }

    fun setVoiceGender(gender: String) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_VOICE_GENDER] = gender
            }
        }
    }

    fun setArabicFontType(fontType: String) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_ARABIC_FONT_TYPE] = fontType
            }
        }
    }

    fun setPersianFontType(fontType: String) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_PERSIAN_FONT_TYPE] = fontType
            }
        }
    }

    fun setSubtitleMode(mode: String) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_SUBTITLE_MODE] = mode
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_PLAYBACK_SPEED] = speed
            }
        }
    }

    fun resetSettingsOnly() {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[KEY_USER_ROLE] = ROLE_PILGRIM
                prefs[KEY_DARK_THEME] = true
                prefs[KEY_LARGE_TEXT] = false
                prefs[KEY_FONT_SCALE] = 1.0f
                prefs[KEY_FONT_WEIGHT] = 0
                prefs[KEY_HAPTICS_ENABLED] = true
                prefs[KEY_VOICE_GENDER] = VOICE_MALE
                prefs[KEY_ARABIC_FONT_TYPE] = "امیری"
                prefs[KEY_SUBTITLE_MODE] = "عربی خوانا"
                prefs[KEY_PLAYBACK_SPEED] = 1.0f
                prefs[KEY_ONBOARDING_COMPLETED] = false
            }
        }
    }

    companion object {
        const val ROLE_PILGRIM = "PILGRIM"
        const val ROLE_MOKEB_OWNER = "MOKEB_OWNER"
        const val VOICE_MALE = "MALE"
        const val VOICE_FEMALE = "FEMALE"

        const val THEME_EMERALD = "EMERALD"
        const val THEME_NAVY = "NAVY"
        const val THEME_MAROON = "MAROON"
        const val THEME_KHAKI = "KHAKI"
    }
}
