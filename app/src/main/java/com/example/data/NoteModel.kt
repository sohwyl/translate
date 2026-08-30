package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class PersonalNote(
    val id: String = System.currentTimeMillis().toString(),
    val title: String = "",
    val content: String = "",
    val linkedPhraseId: Int? = null,
    val linkedPhraseArabic: String? = null,
    val colorHex: String = "#F6C543",
    val timestamp: Long = System.currentTimeMillis(),
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false
)

class NotesManager private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("app_user_notes", Context.MODE_PRIVATE)
    private val _notesFlow = MutableStateFlow<List<PersonalNote>>(emptyList())
    val notesFlow: StateFlow<List<PersonalNote>> = _notesFlow.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        val raw = prefs.getString("saved_notes_json", "[]") ?: "[]"
        try {
            val arr = JSONArray(raw)
            val list = mutableListOf<PersonalNote>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    PersonalNote(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        title = obj.optString("title", ""),
                        content = obj.optString("content", ""),
                        linkedPhraseId = if (obj.has("linkedPhraseId") && !obj.isNull("linkedPhraseId")) obj.optInt("linkedPhraseId") else null,
                        linkedPhraseArabic = obj.optString("linkedPhraseArabic", null),
                        colorHex = obj.optString("colorHex", "#F6C543"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isBold = obj.optBoolean("isBold", false),
                        isItalic = obj.optBoolean("isItalic", false),
                        isUnderline = obj.optBoolean("isUnderline", false)
                    )
                )
            }
            _notesFlow.value = list
        } catch (e: Exception) {
            _notesFlow.value = emptyList()
        }
    }

    fun saveNote(note: PersonalNote) {
        val current = _notesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == note.id }
        if (index >= 0) {
            current[index] = note
        } else {
            current.add(0, note)
        }
        _notesFlow.value = current
        persist(current)
    }

    fun deleteNote(noteId: String) {
        val current = _notesFlow.value.filter { it.id != noteId }
        _notesFlow.value = current
        persist(current)
    }

    private fun persist(list: List<PersonalNote>) {
        val arr = JSONArray()
        for (n in list) {
            val obj = JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("content", n.content)
                put("linkedPhraseId", n.linkedPhraseId)
                put("linkedPhraseArabic", n.linkedPhraseArabic)
                put("colorHex", n.colorHex)
                put("timestamp", n.timestamp)
                put("isBold", n.isBold)
                put("isItalic", n.isItalic)
                put("isUnderline", n.isUnderline)
            }
            arr.put(obj)
        }
        prefs.edit().putString("saved_notes_json", arr.toString()).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: NotesManager? = null

        fun getInstance(context: Context): NotesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
