package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryUtils
import com.example.data.PhraseEntity
import com.example.ui.theme.*
import com.example.ui.utils.HapticUtil

@Composable
fun PhraseCard(
    phrase: PhraseEntity,
    isLargeText: Boolean,
    isPlaying: Boolean,
    isDarkTheme: Boolean = true,
    isGoldActivated: Boolean = false,
    hapticsEnabled: Boolean = true,
    searchQuery: String = "",
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    fontWeightOffset: Int = 0,
    onPlayAudio: () -> Unit,
    onOpenFullScreen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenVipDialog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLocked = CategoryUtils.isPhraseLocked(phrase, isGoldActivated)

    val arabicFontSize = if (isLargeText) 25.sp else 20.sp
    val iraqiFontSize = if (isLargeText) 16.sp else 13.sp
    val persianFontSize = if (isLargeText) 16.sp else 13.sp

    val cardBg = if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF)
    val cardBorder = if (isLocked) {
        if (isDarkTheme) GoldenAmber.copy(alpha = 0.6f) else DayEmerald.copy(alpha = 0.5f)
    } else {
        if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFEBE7DD)
    }
    val arabicTextColor = if (isDarkTheme) TextPrimaryDark else DayText
    val iraqiTextColor = if (isDarkTheme) GoldenAmber else DayEmerald
    val translationBoxBg = if (isDarkTheme) Color(0xFF091F17) else Color(0xFFF4F1EA)
    val translationTextColor = if (isDarkTheme) TextPrimaryDark else DayText
    val actionBtnBg = if (isDarkTheme) Color(0xFF0F3227) else Color(0xFFEBF2EE)
    val actionBtnBorder = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFC3D4CC)
    val actionIconTint = if (isDarkTheme) GoldenAmber else DayEmerald
    val highlightColor = if (isDarkTheme) GoldenAmber else DayEmerald

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, cardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(
            containerColor = cardBg
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Breathing Ambient Islamic Corner Ornament in background of Card
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.TopEnd),
                isDarkTheme = isDarkTheme,
                sizeDp = 72.dp,
                corner = EslimiCorner.TOP_RIGHT
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header: Category Label & Lock Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDarkTheme) Color(0x33DAA520) else Color(0xFFFEF3C7))
                                .clickable {
                                    HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                    onOpenVipDialog()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "قفل",
                                    tint = if (isDarkTheme) GoldenAmber else Color(0xFFD97706),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "نسخه طلایی",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) GoldenAmber else Color(0xFFD97706)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Category Label Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkTheme) Color(0xFF13362A) else Color(0xFFEBF5F3))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = phrase.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) GoldenAmber else DayEmerald
                        )
                    }
                }

                // 1. Primary Top Text: Arabic_Voweled (عربی با اعراب عراقی - iraqiPronunciation)
                val highlightedVoweled = if (isLocked) {
                    AnnotatedString("🔒 قفل نسخه طلایی")
                } else {
                    highlightText(phrase.iraqiPronunciation, searchQuery, highlightColor)
                }
                Text(
                    text = highlightedVoweled,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = arabicFontSize,
                        fontWeight = getMappedFontWeight(fontWeightOffset),
                        fontFamily = getArabicFontFamily(arabicFontType),
                        lineHeight = (arabicFontSize.value * 1.45).sp,
                        color = arabicTextColor,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                // 2. Secondary Subtext: Arabic_Plain (عربی ساده بدون اعراب - arabicText)
                val highlightedPlain = if (isLocked) {
                    AnnotatedString("متن کامل و تلفظ آن ویژه‌ی نسخه‌ی طلایی است")
                } else {
                    highlightText(phrase.arabicText, searchQuery, highlightColor)
                }
                Text(
                    text = highlightedPlain,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = iraqiFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = getArabicFontFamily(arabicFontType),
                        color = iraqiTextColor,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                // Persian Translation Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(translationBoxBg)
                        .clickable(enabled = isLocked) { onOpenVipDialog() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    val highlightedPersian = if (isLocked) {
                        AnnotatedString("🔒 برای مشاهده کامل و تلفظ صوتی، نسخه طلایی را فعال کنید.")
                    } else {
                        highlightText(phrase.persianTranslation, searchQuery, highlightColor)
                    }
                    Text(
                        text = highlightedPersian,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = persianFontSize,
                            fontFamily = getPersianFontFamily(persianFontType),
                            color = if (isLocked) (if (isDarkTheme) GoldenAmber else DayEmerald) else translationTextColor,
                            fontWeight = if (isLocked) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Bar (Play Audio, Enlarge, Copy, Favorite)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Morphing Audio Play Button (Idle -> Equalizer Wave -> Checkmark)
                    MorphingSpiritualAudioButton(
                        isPlaying = isPlaying,
                        isLocked = isLocked,
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            HapticUtil.triggerLightImpact(context, hapticsEnabled)
                            if (isLocked) {
                                onOpenVipDialog()
                            } else {
                                onPlayAudio()
                            }
                        },
                        actionBtnBg = actionBtnBg,
                        actionBtnBorder = actionBtnBorder
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Full Screen Enlarge
                    IconButton(
                        onClick = {
                            HapticUtil.triggerLightImpact(context, hapticsEnabled)
                            if (isLocked) {
                                onOpenVipDialog()
                            } else {
                                onOpenFullScreen()
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.dp, actionBtnBorder, CircleShape)
                            .background(actionBtnBg)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "نمایش بزرگ",
                            tint = actionIconTint
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Copy Text
                    IconButton(
                        onClick = {
                            HapticUtil.triggerLightImpact(context, hapticsEnabled)
                            if (isLocked) {
                                onOpenVipDialog()
                            } else {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Phrase", "${phrase.iraqiPronunciation}\n${phrase.arabicText}\n${phrase.persianTranslation}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "عبارت کپی شد", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.dp, actionBtnBorder, CircleShape)
                            .background(actionBtnBg)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "کپی عبارت",
                            tint = actionIconTint
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Favorite Bookmark with Disney Anticipation & Overshoot Micro-interaction
                    FavoriteMicroInteractionButton(
                        isFavorite = phrase.isFavorite,
                        isDarkTheme = isDarkTheme,
                        onToggleFavorite = {
                            HapticUtil.triggerLightImpact(context, hapticsEnabled)
                            onToggleFavorite()
                        },
                        actionBtnBg = actionBtnBg,
                        actionBtnBorder = actionBtnBorder
                    )
                }
            }
        }
    }
}

@Composable
fun AudioWaveAnimation(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave1"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave2"
    )
    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave3"
    )
    val scale4 by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(320, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave4"
    )

    Row(
        modifier = modifier
            .height(18.dp)
            .width(18.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val scales = listOf(scale1, scale2, scale3, scale4)
        scales.forEach { scale ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(scale)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

fun highlightText(text: String, query: String, highlightColor: Color): AnnotatedString {
    if (query.isEmpty()) return AnnotatedString(text)
    
    val lowerText = text.lowercase()
    val lowerQuery = query.lowercase()
    
    return buildAnnotatedString {
        var start = 0
        while (true) {
            val index = lowerText.indexOf(lowerQuery, start)
            if (index == -1) {
                append(text.substring(start))
                break
            }
            
            append(text.substring(start, index))
            withStyle(
                SpanStyle(
                    background = highlightColor.copy(alpha = 0.22f),
                    color = highlightColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(index, index + query.length))
            }
            start = index + query.length
        }
    }
}
