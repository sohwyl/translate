package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class PhraseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "numeric_id")
    val numeric_id: Int = 0,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "category_id")
    val category_id: String = "",
    
    @ColumnInfo(name = "arabic")
    val arabicText: String, // Full Arabic with Tashkeel
    
    @ColumnInfo(name = "arabic_readable")
    val iraqiPronunciation: String, // Spoken Iraqi dialect guide
    
    @ColumnInfo(name = "finglish")
    val finglish: String = "",
    
    @ColumnInfo(name = "persian")
    val persianTranslation: String, // Persian translation
    
    @ColumnInfo(name = "forRole")
    val forRole: String = "BOTH", // "PILGRIM", "MOKEB_OWNER", or "BOTH"
    
    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "is_premium")
    val isVip: Boolean = false
    // NOTE: audio_male_path / audio_female_path columns were removed — they were
    // dead fields that nothing ever wrote to. AudioPlayerHelper resolves audio
    // files purely by naming convention from the phrase id:
    //   assets/audio/male/phrase_<id>_male.mp3
    //   assets/audio/female/phrase_<id>_female.mp3
    // See AudioPlayerHelper.speak() and generate_voice_over.py.
)

