package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ElderlyTypographyConfig(
    isDarkTheme: Boolean,
    isLargeText: Boolean,
    fontScale: Float,
    fontWeightOffset: Int,
    onLargeTextToggle: (Boolean) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onFontWeightChange: (Int) -> Unit
) {
    val cardBg = if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFEBE7DD)
    val accentTint = if (isDarkTheme) GoldenAmber else DayEmerald
    val titleColor = if (isDarkTheme) TextPrimaryDark else DayText
    val descColor = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)
    val previewBg = if (isDarkTheme) Color(0xFF091F17) else Color(0xFFF4F1EA)

    val currentFontWeight = if (fontWeightOffset > 0) FontWeight.Bold else FontWeight.Normal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = null,
                    tint = accentTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "تنظیمات فونت و خوانایی (ویژه سالمندان)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = "تنظیم اندازه و ضخامت قلم وزیرمتن و فونت عربی",
                        fontSize = 11.sp,
                        color = descColor
                    )
                }
            }

            Divider(color = cardBorder, modifier = Modifier.padding(bottom = 12.dp))

            // Quick Switch for Large Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "حالت متون بزرگ (حالت درشت)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                Switch(
                    checked = isLargeText,
                    onCheckedChange = { enabled ->
                        onLargeTextToggle(enabled)
                        if (enabled) {
                            onFontScaleChange(1.3f)
                        } else {
                            onFontScaleChange(1.0f)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = if (isDarkTheme) DarkEmeraldBg else Color.White,
                        checkedTrackColor = accentTint
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Size Control Slider
            Text(
                text = "اندازه قلم: ${(fontScale * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = accentTint
            )
            Slider(
                value = fontScale,
                onValueChange = { onFontScaleChange(it) },
                valueRange = 0.9f..1.6f,
                steps = 6,
                colors = SliderDefaults.colors(
                    thumbColor = accentTint,
                    activeTrackColor = accentTint,
                    inactiveTrackColor = cardBorder
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Font Weight Selectors
            Text(
                text = "ضخامت قلم (وزن خط):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = accentTint,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val weights = listOf("عادی" to 0, "ضخیم" to 2)
                weights.forEach { (label, value) ->
                    val isSelected = fontWeightOffset == value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) accentTint else (if (isDarkTheme) Color(0xFF13362A) else Color(0xFFEBF5F3))
                            )
                            .border(
                                1.dp,
                                if (isSelected) accentTint else cardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onFontWeightChange(value) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) (if (isDarkTheme) DarkEmeraldBg else Color.White) else titleColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Preview Card
            Text(
                text = "پیش‌نمایش زنده خط و خوانایی:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = descColor,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            val baseArabicSize = 22.sp * fontScale
            val basePersianSize = 14.sp * fontScale

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(previewBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Calligraphic Arabic
                    Text(
                        text = "السَّلاَمُ عَلَيْكُمْ يَا أَبَا عَبْدِ اللَّهِ",
                        fontSize = baseArabicSize,
                        fontWeight = currentFontWeight,
                        fontFamily = FontFamily.Serif,
                        color = GoldenAmber,
                        textAlign = TextAlign.Center,
                        lineHeight = (baseArabicSize.value * 1.3f).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Spoken Iraqi Dialect Phonetic
                    Text(
                        text = "تلفظ: السَّلام عَلِيكُم يا اَبا عَبْدِالله",
                        fontSize = (basePersianSize.value * 0.95f).sp,
                        fontWeight = currentFontWeight,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isDarkTheme) GoldenAmberLight else DayEmerald,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Persian Vazirmatn Translation
                    Text(
                        text = "ترجمه: سلام بر تو ای اباعبدالله الحسین (ع)",
                        fontSize = basePersianSize,
                        fontWeight = currentFontWeight,
                        fontFamily = FontFamily.SansSerif,
                        color = titleColor,
                        textAlign = TextAlign.Center,
                        lineHeight = (basePersianSize.value * 1.4f).sp
                    )
                }
            }
        }
    }
}
