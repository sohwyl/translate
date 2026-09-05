package com.example.ui.utils

private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

/**
 * Converts any Latin digits (0-9) inside this string to their Persian equivalents (۰-۹).
 * Used to keep numbers consistent across the fully-Persian UI (avoids strings like "2 از ۳").
 */
fun String.toPersianDigits(): String {
    val builder = StringBuilder(length)
    for (char in this) {
        if (char in '0'..'9') {
            builder.append(persianDigits[char - '0'])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}

/** Converts this integer to a string of Persian digits (e.g. 12 -> "۱۲"). */
fun Int.toPersianDigits(): String = this.toString().toPersianDigits()
