package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// ---------------------------------------------------------------------------
// Real, embedded typefaces (Google Fonts, OFL licensed — see res/font/OFL.txt).
// Previously every one of these names was just an alias for the generic
// system FontFamily.SansSerif / Serif / Cursive, so switching "font" in
// Settings never actually changed anything and Arabic/Persian text rendered
// with whatever ugly fallback the OEM shipped (broken tashkeel, tofu glyphs
// on some devices, inconsistent widths). These now point at real font files
// bundled in res/font/.
// ---------------------------------------------------------------------------

// 1. Vazirmatn (وزیرمتن) - Primary Persian & Arabic Sans Font
val VazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn, weight = FontWeight.Normal),
    Font(R.font.vazirmatn, weight = FontWeight.Bold)
)

// 2. Lalezar (لاله‌زار) - Distinctive Display Font
val LalezarFontFamily = FontFamily(
    Font(R.font.lalezar_regular, weight = FontWeight.Normal),
    Font(R.font.lalezar_regular, weight = FontWeight.Bold)
)

// 3. Amiri (امیری) - Classic Naskh Calligraphy
val AmiriFontFamily = FontFamily(
    Font(R.font.amiri_regular, weight = FontWeight.Normal),
    Font(R.font.amiri_bold, weight = FontWeight.Bold)
)

// 4. Noto Naskh (نسخ خوانا) - Modern High-Legibility Naskh
val NotoNaskhFontFamily = FontFamily(
    Font(R.font.noto_naskh_arabic, weight = FontWeight.Normal),
    Font(R.font.noto_naskh_arabic, weight = FontWeight.Bold)
)

// 5. Scheherazade New (شهرزاد) - Traditional Flowing Arabic
val ScheherazadeFontFamily = FontFamily(
    Font(R.font.scheherazade_regular, weight = FontWeight.Normal),
    Font(R.font.scheherazade_bold, weight = FontWeight.Bold)
)

// 6. Tajawal (تجاول) - Clean Modern Geometric Sans
val TajawalFontFamily = FontFamily(
    Font(R.font.tajawal_regular, weight = FontWeight.Normal),
    Font(R.font.tajawal_bold, weight = FontWeight.Bold)
)

// 7. Almarai (المرائی) - Modern Square/Round Yekan Style
val AlmaraiFontFamily = FontFamily(
    Font(R.font.almarai_regular, weight = FontWeight.Normal),
    Font(R.font.almarai_bold, weight = FontWeight.Bold)
)

fun getArabicFontFamily(fontType: String): FontFamily {
    return when {
        fontType.contains("امیری") -> AmiriFontFamily
        fontType.contains("وزیر") -> VazirmatnFontFamily
        fontType.contains("نسخ") -> NotoNaskhFontFamily
        fontType.contains("لاله") || fontType.contains("قاهره") -> LalezarFontFamily
        fontType.contains("شهرزاد") -> ScheherazadeFontFamily
        fontType.contains("تجاول") -> TajawalFontFamily
        fontType.contains("یکان") || fontType.contains("المرائی") -> AlmaraiFontFamily
        else -> AmiriFontFamily
    }
}

fun getPersianFontFamily(fontType: String): FontFamily {
    return when {
        fontType.contains("لاله") -> LalezarFontFamily
        fontType.contains("وزیر") -> VazirmatnFontFamily
        else -> VazirmatnFontFamily
    }
}

fun getMappedFontWeight(offset: Int): FontWeight {
    return when (offset) {
        0 -> FontWeight.Normal // Normal (عادی)
        2 -> FontWeight.Bold   // Bold (ضخیم)
        else -> if (offset > 0) FontWeight.Bold else FontWeight.Normal
    }
}

// NOTE ON LINE HEIGHT: Arabic/Persian text carries diacritics (tashkeel) both
// above and below the baseline (e.g. َ ِ ُ ّ ـً). The line heights below were
// bumped ~15-20% over the Latin-typical ratio used by the previous values so
// marks are not clipped against the line above/below — this was clipping
// vowel marks on several phrase cards before this fix.
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 46.sp
    ),
    displayMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 41.sp
    ),
    displaySmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 27.sp
    ),
    titleLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 25.sp
    ),
    titleSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 27.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 23.sp
    ),
    bodySmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    labelMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 19.sp
    ),
    labelSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 16.sp
    )
)
