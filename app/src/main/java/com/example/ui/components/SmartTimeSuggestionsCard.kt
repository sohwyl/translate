package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PhraseEntity
import com.example.ui.theme.*
import com.example.ui.utils.HapticUtil
import java.util.Calendar

data class TimeSuggestion(
    val title: String,
    val subtitle: String,
    val phrase: PhraseEntity
)

@Composable
fun SmartTimeSuggestionsCard(
    allPhrases: List<PhraseEntity>,
    isDarkTheme: Boolean,
    arabicFontType: String = "امیری",
    hapticsEnabled: Boolean = true,
    speakingPhraseId: Int?,
    onPlayAudio: (PhraseEntity) -> Unit,
    onShowToast: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Determine current time period
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }

    val (timeTitle, timeTag, icon, bgGradient) = remember(hour, isDarkTheme) {
        when (hour) {
            in 5..11 -> Quadruple(
                "پیشنهادهای هوشمند صبحگاه",
                "صبح",
                Icons.Default.WbSunny,
                if (isDarkTheme) {
                    Brush.horizontalGradient(listOf(Color(0xFF1B382B), Color(0xFF2A4E3D)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFFFFF8E7), Color(0xFFFFF0CB)))
                }
            )
            in 12..16 -> Quadruple(
                "پیشنهادهای هوشمند نیمروز",
                "ظهر و عصر",
                Icons.Default.LightMode,
                if (isDarkTheme) {
                    Brush.horizontalGradient(listOf(Color(0xFF234434), Color(0xFF193628)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFFF3FAF7), Color(0xFFE2F3EC)))
                }
            )
            else -> Quadruple(
                "پیشنهادهای هوشمند شامگاه",
                "شبانه",
                Icons.Default.NightsStay,
                if (isDarkTheme) {
                    Brush.horizontalGradient(listOf(Color(0xFF12281E), Color(0xFF1E3A2C)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFFEDF4F2), Color(0xFFDCEDE7)))
                }
            )
        }
    }

    // Filter relevant phrases based on time of day
    val suggestedPhrases = remember(hour, allPhrases) {
        val targetIds = when (hour) {
            in 5..11 -> listOf(5, 1, 3, 2, 8, 14, 51) // Morning greetings, breakfast, tea
            in 12..16 -> listOf(35, 12, 11, 42, 64, 25, 33) // Directions, rest, prayer, toilets
            else -> listOf(6, 44, 29, 7, 60, 2, 21) // Night rest, family lodging, thanks
        }
        val matched = allPhrases.filter { it.id in targetIds }
        if (matched.isNotEmpty()) matched else allPhrases.take(6)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgGradient)
                .border(
                    1.dp,
                    if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3),
                    RoundedCornerShape(20.dp)
                )
        ) {
            // Breathing Eslimi Corner Ornament
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.TopEnd),
                isDarkTheme = isDarkTheme,
                sizeDp = 64.dp,
                corner = EslimiCorner.TOP_RIGHT
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) GoldenAmber.copy(alpha = 0.2f) else DayEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = timeTag,
                                tint = if (isDarkTheme) GoldenAmber else DayEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = timeTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) TextPrimaryDark else DayText
                            )
                            Text(
                                text = "عبارات پیشنهادی متناسب با زمان فعلی شما",
                                fontSize = 11.sp,
                                color = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDarkTheme) DarkEmeraldBg else Color.White)
                            .border(1.dp, if (isDarkTheme) GoldenAmber.copy(0.4f) else DayEmerald.copy(0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = timeTag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) GoldenAmber else DayEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Suggestions Horizontal Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(suggestedPhrases, key = { it.id }) { phrase ->
                        val isPlaying = speakingPhraseId != null && (speakingPhraseId == phrase.id || (phrase.numeric_id > 0 && speakingPhraseId == phrase.numeric_id))

                        Box(
                            modifier = Modifier
                                .width(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDarkTheme) DarkEmeraldCard else Color.White)
                                .border(
                                    1.dp,
                                    if (isPlaying) (if (isDarkTheme) GoldenAmber else DayEmerald)
                                    else if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE5E1D6),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                    onPlayAudio(phrase)
                                }
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = phrase.iraqiPronunciation,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = getArabicFontFamily(arabicFontType),
                                    color = if (isDarkTheme) GoldenAmberLight else DayEmerald,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = phrase.persianTranslation,
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) TextSecondaryDark else DayText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Audio Play Button
                                    IconButton(
                                        onClick = {
                                            HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                            onPlayAudio(phrase)
                                        },
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(if (isPlaying) GoldenAmber else (if (isDarkTheme) DarkEmeraldBg else Color(0xFFF0FDF4)))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "پخش",
                                            tint = if (isPlaying) DarkEmeraldBg else (if (isDarkTheme) GoldenAmber else DayEmerald),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Copy Text Button
                                    IconButton(
                                        onClick = {
                                            HapticUtil.triggerLightImpact(context, hapticsEnabled)
                                            clipboardManager.setText(AnnotatedString(phrase.arabicText))
                                            onShowToast("عبارت عربی کپی شد 📋", false)
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "کپی",
                                            tint = if (isDarkTheme) TextMutedDark else Color(0xFF8A9A93),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
