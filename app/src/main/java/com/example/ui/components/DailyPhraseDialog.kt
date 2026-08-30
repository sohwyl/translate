package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PhraseEntity
import com.example.ui.theme.*

@Composable
fun DailyPhraseDialog(
    allPhrases: List<PhraseEntity>,
    initialIndex: Int = 0,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: (PhraseEntity) -> Unit
) {
    val context = LocalContext.current
    val phrases = remember(allPhrases) {
        if (allPhrases.isNotEmpty()) allPhrases else listOf(
            PhraseEntity(
                id = 1,
                arabicText = "إِنْ شَاءَ اللَّه",
                persianTranslation = "ان شاء الله",
                iraqiPronunciation = "[in sha allah] - به خواست خدا",
                category = "احوالپرسی و تعارفات"
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(initialIndex.coerceIn(0, phrases.lastIndex.coerceAtLeast(0))) }
    val currentPhrase = phrases[currentIndex]
    var isPlaying by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = if (isDarkTheme) NightBackground else DayBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) NightCard else DayCard)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = GoldenAmber
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_modern_sparkle),
                        contentDescription = null,
                        tint = GoldenAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "عبارت روز",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                }

                Spacer(modifier = Modifier.width(42.dp))
            }

            // Center Content (Screen 3)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Arabic Text
                Text(
                    text = currentPhrase.arabicText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Persian translation
                Text(
                    text = currentPhrase.persianTranslation,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) GoldenAmber else GoldenAmberDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pronunciation Transliteration
                Text(
                    text = currentPhrase.iraqiPronunciation,
                    fontSize = 14.sp,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Action Buttons: Favorite + Share
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Favorite Heart Button
                    IconButton(
                        onClick = { onToggleFavorite(currentPhrase) },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) NightCard else DayCard)
                            .border(
                                1.dp,
                                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (currentPhrase.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "علاقه‌مندی",
                            tint = if (currentPhrase.isFavorite) FavoriteRed else (if (isDarkTheme) TextSecondaryDark else TextSecondaryLight),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${currentPhrase.arabicText}\n${currentPhrase.persianTranslation}\n(لهجه عراقی: ${currentPhrase.iraqiPronunciation})\n— مترجم اربعین"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "اشتراک عبارت"))
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) NightCard else DayCard)
                            .border(
                                1.dp,
                                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                                CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_modern_share),
                            contentDescription = "اشتراک",
                            tint = if (isDarkTheme) GoldenAmber else GoldenAmberDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Large Golden "گوش دادن" Button
                Button(
                    onClick = {
                        isPlaying = !isPlaying
                        val helper = AudioPlayerHelper.getInstance(context)
                        if (isPlaying) {
                            helper.speak(currentPhrase.id, currentPhrase.arabicText)
                        } else {
                            helper.stop()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                    shape = RoundedCornerShape(27.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_modern_volume_speaker),
                            contentDescription = null,
                            tint = DarkEmeraldBg,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isPlaying) "در حال پخش..." else "گوش دادن",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEmeraldBg
                        )
                    }
                }
            }

            // Bottom Navigation: "عبارت قبلی" | "عبارت بعدی"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Next Phrase
                OutlinedButton(
                    onClick = {
                        if (currentIndex < phrases.lastIndex) {
                            currentIndex++
                        } else {
                            currentIndex = 0
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
                        )
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isDarkTheme) NightCard else DayCard
                    )
                ) {
                    Text(
                        text = "عبارت بعدی",
                        color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Previous Phrase
                OutlinedButton(
                    onClick = {
                        if (currentIndex > 0) {
                            currentIndex--
                        } else {
                            currentIndex = phrases.lastIndex
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
                        )
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isDarkTheme) NightCard else DayCard
                    )
                ) {
                    Text(
                        text = "عبارت قبلی",
                        color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
