package com.example.data

object ArabicNormalizer {
    fun normalize(input: String): String {
        if (input.isBlank()) return ""
        return input
            .replace(Regex("[\u064B-\u065F\u0670]"), "") // Remove tashkeel/diacritics
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ة', 'ه')
            .replace('ى', 'ي')
            .replace('ئ', 'ي')
            .replace('ؤ', 'و')
            .lowercase()
            .trim()
    }
}

object CategoryUtils {
    // 5 Categories (Phase 1)
    val FREE_CATEGORIES = setOf(
        "احوالپرسی و احترام",
        "زیارت و حرم",
        "موکب و خدمات",
        "مسیر و آدرس",
        "غذا و نوشیدنی",
        // Fallbacks for backwards compatibility
        "سلام و تعارفات اولیه",
        "آدرس و مسیر عمودها",
        "درمان، اورژانس و داروخانه"
    )

    // 10 Premium Categories (Phase 2 & Phase 3)
    val PREMIUM_CATEGORIES = setOf(
        "خرید و بازار",
        "تاکسی و حمل‌ونقل",
        "اسکان و محل اقامت",
        "درمان و دارو",
        "پول، بانک و صرافی",
        "موبایل، اینترنت و شارژ",
        "شرایط اضطراری",
        "خانواده و کودکان",
        "پیاده‌روی اربعین",
        "اصطلاحات مذهبی و زیارتی",
        "اصطلاحات روزمره عراقی",
        "مکالمه با خادم موکب",
        "مکالمه با راننده تاکسی",
        "مکالمه با فروشنده",
        "اصطلاحات پرکاربرد عراقی",
        "اصطلاحات و تکیه‌کلام‌های عراقی",
        // Additional fallbacks
        "غذا و درخواست‌ها در موکب",
        "گم‌شدن و امنیت کاروان",
        "اماکن زیارتی و احکام"
    )

    fun isCategoryFree(categoryName: String): Boolean {
        if (categoryName == "همه" || categoryName.startsWith("همه")) return true
        return categoryName in FREE_CATEGORIES
    }

    fun isPhrasePremium(phrase: PhraseEntity): Boolean {
        return phrase.isVip || !isCategoryFree(phrase.category)
    }

    fun isPhraseLocked(phrase: PhraseEntity, isGoldActivated: Boolean): Boolean {
        if (isGoldActivated) return false
        return isPhrasePremium(phrase)
    }
}
