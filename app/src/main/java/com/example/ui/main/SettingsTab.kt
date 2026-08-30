package com.example.ui.main

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.UserPreferences
import com.example.ui.theme.*
import com.example.ui.utils.HapticUtil
import java.io.File

@Composable
fun SettingsTab(
    currentRole: String,
    isDarkTheme: Boolean,
    isLargeText: Boolean,
    fontScale: Float = 1.0f,
    fontWeightOffset: Int = 0,
    hapticsEnabled: Boolean = true,
    voiceGender: String = UserPreferences.VOICE_MALE,
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    subtitleMode: String = "عربی خوانا",
    playbackSpeed: Float = 1.0f,
    isGoldActivated: Boolean = false,
    onRoleChange: (String) -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onLargeTextToggle: (Boolean) -> Unit,
    onFontScaleChange: (Float) -> Unit = {},
    onFontWeightChange: (Int) -> Unit = {},
    onHapticsToggle: (Boolean) -> Unit = {},
    onVoiceGenderChange: (String) -> Unit = {},
    onArabicFontTypeChange: (String) -> Unit = {},
    onPersianFontTypeChange: (String) -> Unit = {},
    onSubtitleModeChange: (String) -> Unit = {},
    onPlaybackSpeedChange: (Float) -> Unit = {},
    onActivateGoldClick: () -> Unit = {},
    onResetSettingsOnly: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDeveloperLetter by remember { mutableStateOf(false) }
    var showDangerZoneDialog by remember { mutableStateOf(false) }

    val headerTextColor = if (isDarkTheme) GoldenAmber else DayEmerald
    val cardBg = if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFEBE7DD)
    val itemTitleColor = if (isDarkTheme) TextPrimaryDark else DayText
    val itemDescColor = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)
    val accentTint = if (isDarkTheme) GoldenAmber else DayEmerald

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = accentTint,
        checkedBorderColor = accentTint,
        uncheckedThumbColor = if (isDarkTheme) Color(0xFFD0D0D0) else Color(0xFF475569),
        uncheckedTrackColor = if (isDarkTheme) Color(0xFF1E2E28) else Color(0xFFE2E8F0),
        uncheckedBorderColor = if (isDarkTheme) Color(0xFF3F544A) else Color(0xFF94A3B8)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "تنظیمات",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = headerTextColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Card 0: انتخاب نقش (زائر یا موکب‌دار)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = accentTint)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "انتخاب نقش در اربعین", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                        Text(text = "نمایش اولویت‌دار عبارات متناسب با وضعیت شما", fontSize = 11.sp, color = itemDescColor)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isPilgrim = currentRole == UserPreferences.ROLE_PILGRIM
                    val pilgrimBg = if (isPilgrim) accentTint else (if (isDarkTheme) Color.Transparent else Color(0xFFF1F5F9))
                    val pilgrimBorder = if (isPilgrim) accentTint else (if (isDarkTheme) cardBorder else Color(0xFFCBD5E1))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(pilgrimBg)
                            .border(1.dp, pilgrimBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                onRoleChange(UserPreferences.ROLE_PILGRIM)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "زائر امام حسین (ع)",
                            fontSize = 13.sp,
                            color = if (isPilgrim) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor,
                            fontWeight = if (isPilgrim) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    val isMokeb = currentRole == UserPreferences.ROLE_MOKEB_OWNER
                    val mokebBg = if (isMokeb) accentTint else (if (isDarkTheme) Color.Transparent else Color(0xFFF1F5F9))
                    val mokebBorder = if (isMokeb) accentTint else (if (isDarkTheme) cardBorder else Color(0xFFCBD5E1))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(mokebBg)
                            .border(1.dp, mokebBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                onRoleChange(UserPreferences.ROLE_MOKEB_OWNER)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "خادم یا موکب‌دار",
                            fontSize = 13.sp,
                            color = if (isMokeb) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor,
                            fontWeight = if (isMokeb) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Card: انتخاب تم برنامه (Screen 10)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = accentTint)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "انتخاب رنگ تم برنامه", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                        Text(text = "۴ پالت رنگی ویژه و هماهنگ اربعین", fontSize = 11.sp, color = itemDescColor)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                val themes = listOf(
                    Triple(UserPreferences.THEME_EMERALD, "سبز زمردی", Color(0xFF117A65)),
                    Triple(UserPreferences.THEME_NAVY, "سرمه‌ای", Color(0xFF14213D)),
                    Triple(UserPreferences.THEME_MAROON, "جگری", Color(0xFF5A1827)),
                    Triple(UserPreferences.THEME_KHAKI, "خاکی", Color(0xFF8C6239))
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    themes.forEach { (themeKey, themeName, colorSwatch) ->
                        val isSelected = false // managed globally
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDarkTheme) Color(0xFF0F2C21) else Color(0xFFF1F5F9))
                                .border(1.dp, if (isSelected) accentTint else cardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                    val userPrefs = UserPreferences(context)
                                    userPrefs.setThemePalette(themeKey)
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(colorSwatch)
                                    .border(1.dp, Color.White.copy(alpha = 0.6f), androidx.compose.foundation.shape.CircleShape)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = themeName,
                                fontSize = 11.sp,
                                color = itemTitleColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }


        // Card 1: جنسیت صدای پخش
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = accentTint)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "جنسیت صدای پخش", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                        Text(text = "انتخاب صدای گوینده آقای عراقی یا بانوی عراقی", fontSize = 11.sp, color = itemDescColor)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isMale = (voiceGender == UserPreferences.VOICE_MALE || voiceGender == "مرد")
                    val maleBg = if (isMale) accentTint else (if (isDarkTheme) Color.Transparent else Color(0xFFF1F5F9))
                    val maleBorder = if (isMale) accentTint else (if (isDarkTheme) cardBorder else Color(0xFFCBD5E1))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(maleBg)
                            .border(1.dp, maleBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                onVoiceGenderChange(UserPreferences.VOICE_MALE)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "مرد",
                            fontSize = 13.sp,
                            color = if (isMale) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor,
                            fontWeight = if (isMale) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    val isFemale = (voiceGender == UserPreferences.VOICE_FEMALE || voiceGender == "زن")
                    val femaleBg = if (isFemale) accentTint else (if (isDarkTheme) Color.Transparent else Color(0xFFF1F5F9))
                    val femaleBorder = if (isFemale) accentTint else (if (isDarkTheme) cardBorder else Color(0xFFCBD5E1))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(femaleBg)
                            .border(1.dp, femaleBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                onVoiceGenderChange(UserPreferences.VOICE_FEMALE)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "زن",
                            fontSize = 13.sp,
                            color = if (isFemale) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor,
                            fontWeight = if (isFemale) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Card: سرعت پخش صدا (Media3 ExoPlayer Speed Setting)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = accentTint)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "سرعت پخش صدا", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                        Text(text = "تنظیم سرعت گوینده عراقی برای یادگیری آسان‌تر", fontSize = 11.sp, color = itemDescColor)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                val speedOptions = listOf(
                    0.75f to "۰.۷۵x (کند)",
                    1.0f to "۱.۰x (عادی)",
                    1.25f to "۱.۲۵x (تند)",
                    1.5f to "۱.۵x (خیلی تند)"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    speedOptions.forEach { (speedValue, label) ->
                        val isSelected = kotlin.math.abs(playbackSpeed - speedValue) < 0.05f
                        val btnBg = if (isSelected) accentTint else (if (isDarkTheme) Color(0xFF0F2C21) else Color(0xFFF1F5F9))
                        val btnBorder = if (isSelected) accentTint else (if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFCBD5E1))
                        val btnTextColor = if (isSelected) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(btnBg)
                                .border(1.dp, btnBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                    onPlaybackSpeedChange(speedValue)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = btnTextColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Card 2: بازخورد لمسی (ویبره)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = accentTint)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "بازخورد لمسی (ویبره)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                        Text(text = "ارتعاش هنگام لمس دکمه‌ها و عبارت‌ها", fontSize = 11.sp, color = itemDescColor)
                    }
                }
                Switch(
                    checked = hapticsEnabled,
                    onCheckedChange = {
                        onHapticsToggle(it)
                        HapticUtil.triggerLightImpact(context, it)
                    },
                    colors = switchColors
                )
            }
        }

        // Card 3: پوسته برنامه (حالت شب)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Brightness4, contentDescription = null, tint = accentTint)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "پوسته برنامه (حالت شب)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                        Text(text = "حالت تم زمردی اربعین مناسب پیاده‌روی شبانه", fontSize = 11.sp, color = itemDescColor)
                    }
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {
                        HapticUtil.triggerLightImpact(context, hapticsEnabled)
                        onDarkThemeToggle(it)
                    },
                    colors = switchColors
                )
            }
        }

        // Card 4: تایپوگرافی ویژه بزرگسالان
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FormatSize, contentDescription = null, tint = accentTint)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "تایپوگرافی ویژه بزرگسالان", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                        Text(text = "با یک دکمه همه متن‌ها خواناتر و بزرگ‌تر می‌شوند.", fontSize = 11.sp, color = itemDescColor)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val onBg = if (isLargeText) accentTint else (if (isDarkTheme) Color.Transparent else Color(0xFFF1F5F9))
                    val onBorder = if (isLargeText) accentTint else (if (isDarkTheme) cardBorder else Color(0xFFCBD5E1))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(onBg)
                            .border(1.dp, onBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                onLargeTextToggle(true)
                                onFontScaleChange(1.3f)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "روشن",
                            fontSize = 13.sp,
                            color = if (isLargeText) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor,
                            fontWeight = if (isLargeText) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    val offBg = if (!isLargeText) accentTint else (if (isDarkTheme) Color.Transparent else Color(0xFFF1F5F9))
                    val offBorder = if (!isLargeText) accentTint else (if (isDarkTheme) cardBorder else Color(0xFFCBD5E1))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(offBg)
                            .border(1.dp, offBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                onLargeTextToggle(false)
                                onFontScaleChange(1.0f)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "خاموش",
                            fontSize = 13.sp,
                            color = if (!isLargeText) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor,
                            fontWeight = if (!isLargeText) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Card 5: تنظیمات دستی اندازه و فونت
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "تنظیمات دستی اندازه و فونت", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                Spacer(modifier = Modifier.height(12.dp))

                // Font Size Slider
                Text(
                    text = "اندازه عربی: ${(24 * fontScale).toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentTint
                )
                Slider(
                    value = fontScale,
                    onValueChange = { onFontScaleChange(it) },
                    valueRange = 0.9f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = accentTint, activeTrackColor = accentTint)
                )

                // Font Weight Selectors
                Text(
                    text = "ضخامت عربی:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentTint,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val weights = listOf("عادی" to 0, "ضخیم" to 2)
                    weights.forEach { (label, value) ->
                        val isSelected = fontWeightOffset == value
                        val btnBg = if (isSelected) accentTint else (if (isDarkTheme) Color(0xFF0F2C21) else Color(0xFFEBF2EE))
                        val btnBorder = if (isSelected) accentTint else (if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFC3D4CC))
                        val btnTextColor = if (isSelected) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(btnBg)
                                .border(1.dp, btnBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                    onFontWeightChange(value)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = btnTextColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Arabic Font Family Selectors (6 choices)
                Text(
                    text = "نوع قلم عربی (۶ فونت زیبا):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentTint,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val arabicFonts = listOf("امیری", "وزیرمتن", "نسخ خوانا", "لاله‌زار", "شهرزاد", "تجاول")
                val chunkedArabicFonts = arabicFonts.chunked(3)
                chunkedArabicFonts.forEachIndexed { index, fontRow ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = if (index < chunkedArabicFonts.size - 1) 6.dp else 0.dp)
                    ) {
                        fontRow.forEach { fontName ->
                            val isSelected = arabicFontType == fontName
                            val btnBg = if (isSelected) accentTint else (if (isDarkTheme) Color(0xFF0F2C21) else Color(0xFFEBF2EE))
                            val btnBorder = if (isSelected) accentTint else (if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFC3D4CC))
                            val btnTextColor = if (isSelected) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(btnBg)
                                    .border(1.dp, btnBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                        onArabicFontTypeChange(fontName)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fontName,
                                    fontSize = 11.sp,
                                    fontFamily = getArabicFontFamily(fontName),
                                    color = btnTextColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Persian Font Family Selectors (2 choices: وزیرمتن, لاله‌زار)
                Text(
                    text = "نوع قلم فارسی (فونت اصلی برنامه):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentTint,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val persianFonts = listOf("وزیرمتن", "لاله‌زار")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    persianFonts.forEach { fontName ->
                        val isSelected = persianFontType == fontName
                        val btnBg = if (isSelected) accentTint else (if (isDarkTheme) Color(0xFF0F2C21) else Color(0xFFEBF2EE))
                        val btnBorder = if (isSelected) accentTint else (if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFC3D4CC))
                        val btnTextColor = if (isSelected) (if (isDarkTheme) DarkEmeraldBg else Color.White) else itemTitleColor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(btnBg)
                                .border(1.dp, btnBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                    onPersianFontTypeChange(fontName)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fontName,
                                fontSize = 11.sp,
                                fontFamily = getPersianFontFamily(fontName),
                                color = btnTextColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkTheme) Color(0xFF091F17) else Color(0xFFF4F1EA))
                        .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "پیش‌نمایش زنده قلم عربی و فارسی", fontSize = 11.sp, color = accentTint, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "أَهْلَاً وَسَهْلَاً بِكُمْ فِي مَوْكِبِنَا",
                            fontSize = (24 * fontScale).sp,
                            fontWeight = getMappedFontWeight(fontWeightOffset),
                            fontFamily = getArabicFontFamily(arabicFontType),
                            color = itemTitleColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "خوش آمدید، بفرمایید داخل موکب ما",
                            fontSize = 13.sp,
                            fontFamily = getPersianFontFamily(persianFontType),
                            color = itemDescColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Card 6: درباره برنامه
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "درباره برنامه", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = itemTitleColor)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkTheme) Color(0xFF13362A) else Color(0xFFEBF5F3))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("نسخه ۱.۰.۱ کاملاً آفلاین", fontSize = 10.sp, color = accentTint, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "این اپلیکیشن برای زائران اربعین طراحی شده است. تمام جملات به لهجه واقعی عراقی هستند و بدون نیاز به اینترنت کار می‌کنند.",
                    fontSize = 12.sp,
                    color = itemDescColor,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Metric Cards
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDarkTheme) Color(0xFF0D281E) else Color(0xFFF4F1EA))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+۶۰۰", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accentTint)
                            Text("عبارات", fontSize = 10.sp, color = itemDescColor)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDarkTheme) Color(0xFF0D281E) else Color(0xFFF4F1EA))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("۱۵", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accentTint)
                            Text("دسته‌ها", fontSize = 10.sp, color = itemDescColor)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDarkTheme) Color(0xFF0D281E) else Color(0xFFF4F1EA))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("۱۰۰٪", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accentTint)
                            Text("آفلاین", fontSize = 10.sp, color = itemDescColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Guidance Bullet Points
                val bullets = listOf(
                    "برنامه بدون اینترنت کار می‌کند و اطلاعات شخص شما را ارسال نمی‌کند.",
                    "برای تشکر از موکب‌داران، عبارت «رَحِمَ الله والِدِیک» بسیار محترمانه و رایج است.",
                    "هنگام پرسیدن مسیر، آرام صحبت کنید و اگر لازم شد صفحه گوشی را نشان دهید.",
                    "در موکب‌ها، احترام و لبخند بهترین شروع گفتگو با میزبانان عراقی است."
                )
                bullets.forEach { bullet ->
                    Row(modifier = Modifier.padding(bottom = 6.dp), verticalAlignment = Alignment.Top) {
                        Text("• ", color = accentTint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(bullet, fontSize = 11.sp, color = itemDescColor, lineHeight = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Share APK File Button
                OutlinedButton(
                    onClick = {
                        HapticUtil.triggerLightImpact(context, hapticsEnabled)
                        shareApkFile(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentTint)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = accentTint, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ارسال اپلیکیشن برای همسفران", fontSize = 12.sp, color = accentTint, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // About Developer Button
                OutlinedButton(
                    onClick = {
                        HapticUtil.triggerLightImpact(context, hapticsEnabled)
                        showDeveloperLetter = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = itemTitleColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("درباره توسعه‌دهنده", fontSize = 12.sp, color = itemTitleColor, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Card 7: منطقه خطر
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x44EF4444), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) Color(0xFF1E0A0A) else Color(0xFFFEF2F2)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "منطقه خطر (عملیات حساس)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FavoriteRed)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "با این کار تنظیمات ظاهری به حالت اولیه برمی‌گردد، اما نشان‌شده‌ها و اشتراک شما محفوظ می‌ماند.",
                    fontSize = 11.sp,
                    color = itemDescColor,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        HapticUtil.triggerLightImpact(context, hapticsEnabled)
                        showDangerZoneDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = FavoriteRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بازنشانی تمام تنظیمات", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (showDangerZoneDialog) {
        AlertDialog(
            onDismissRequest = { showDangerZoneDialog = false },
            title = { Text("بازنشانی تنظیمات") },
            text = { Text("آیا از بازنشانی تمام تنظیمات به حالت اولیه اطمینان دارید؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetSettingsOnly()
                        showDangerZoneDialog = false
                    }
                ) {
                    Text("بله، بازنشانی شود", color = FavoriteRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDangerZoneDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    if (showDeveloperLetter) {
        com.example.ui.components.DeveloperLetter3DDialog(
            isDarkTheme = isDarkTheme,
            onDismiss = { showDeveloperLetter = false }
        )
    }
}

private fun shareApkFile(context: Context) {
    try {
        val appInfo = context.applicationInfo
        val sourceFile = File(appInfo.sourceDir)
        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            sourceFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, apkUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "ارسال اپلیکیشن اربعین برای همسفران"))
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "دانلود برنامه عبارت‌نامه اربعین با ۶۰۰ عبارت صوتی لهجه عراقی")
        }
        context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری"))
    }
}

@Composable
private fun EnvelopeLetterDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(0.92f)
                    .clickable(enabled = false) {},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Envelope Flap Top Graphic
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(if (isDarkTheme) Color(0xFF1B3B2B) else Color(0xFFD8CCB4))
                        .border(1.dp, GoldenAmber, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = GoldenAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Animated Letter Card Sliding Up out of Envelope
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = progress.value
                            translationY = (1f - progress.value) * 120f
                        }
                        .shadow(16.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkTheme) Color(0xFF132A20) else Color(0xFFFFFDF7))
                        .border(1.5.dp, GoldenAmber, RoundedCornerShape(20.dp))
                        .padding(22.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "۞ نامه توسعه‌دهنده به زائران ۞",
                            fontFamily = FontFamily.Serif,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(
                            color = GoldenAmber.copy(alpha = 0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "بسم الله الرحمن الرحیم\n\nزائر گرامی و همسفر عزیز،\nالتماس دعا در مسیر پیاده‌روی نورانی نجف تا کربلا.\n\nاین نرم‌افزار به صورت دلی و وقف خدمت‌رسانی به زائران حضرت اباعبدالله الحسین (ع) طرّاحی و ساخته شده است.\n\nامید است در ثواب قدم‌های پاکتان، این خادم کوچک را هم سهیم بفرمایید.\n\nالتماس دعای فرج\nخادم زائران - توسعه‌دهنده",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            color = if (isDarkTheme) TextPrimaryDark else Color(0xFF2C2518),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(0.75f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = DarkEmeraldBg,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "قبول باشد - بستن",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkEmeraldBg
                            )
                        }
                    }
                }
            }
        }
    }
}
