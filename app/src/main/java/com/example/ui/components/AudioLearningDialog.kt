package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
fun AudioLearningDialog(
    phrase: PhraseEntity,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: (PhraseEntity) -> Unit,
    onOpenNoteEditor: (PhraseEntity) -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) } // 0.75f = آهسته, 1.0f = عادی
    var isRepeat by remember { mutableStateOf(false) }

    // Waveform infinite animation
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_phase"
    )

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
            // Header: Back button & Bookmark
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

                IconButton(
                    onClick = { onToggleFavorite(phrase) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) NightCard else DayCard)
                ) {
                    Icon(
                        imageVector = if (phrase.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "نشان کردن",
                        tint = GoldenAmber
                    )
                }
            }

            // Center: Large Arabic Text, Persian Translation, Waveform, Play Button, Speed
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Arabic Text
                Text(
                    text = phrase.arabicText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 50.sp,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Persian Meaning
                Text(
                    text = phrase.persianTranslation,
                    fontSize = 17.sp,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Animated Soundwave Waveform (Screen 5)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val barCount = 36
                    val waveColor = GoldenAmber.copy(alpha = if (isPlaying) 0.9f else 0.4f)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = 4.dp.toPx()
                        val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)

                        for (i in 0 until barCount) {
                            val x = i * (barWidth + spacing)
                            val normalized = (i - barCount / 2f) / (barCount / 2f)
                            val bell = (1f - normalized * normalized).coerceAtLeast(0.15f)
                            val waveEffect = if (isPlaying) {
                                kotlin.math.sin((i.toFloat() + wavePhase * 10f) * 0.5f).toFloat() * 0.4f + 0.6f
                            } else {
                                0.4f
                            }
                            val barHeight = (size.height * 0.85f * bell * waveEffect).coerceAtLeast(6f)
                            val top = (size.height - barHeight) / 2f

                            drawRoundRect(
                                color = waveColor,
                                topLeft = Offset(x, top),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Large Golden Play / Pause Button (Screen 5)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GoldenAmber)
                        .clickable {
                            isPlaying = !isPlaying
                            val helper = AudioPlayerHelper.getInstance(context)
                            if (isPlaying) {
                                helper.speak(phrase.id, phrase.arabicText, speed = selectedSpeed)
                            } else {
                                helper.stop()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "پخش",
                        tint = DarkEmeraldBg,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3 Speed / Repeat Options (تکرار, عادی, آهسته)
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // آهسته (Slow: 0.75x)
                    PlaybackOptionChip(
                        label = "آهسته",
                        iconRes = R.drawable.ic_modern_speed,
                        isSelected = selectedSpeed == 0.75f,
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            selectedSpeed = if (selectedSpeed == 0.75f) 1.0f else 0.75f
                        }
                    )

                    // عادی (Normal: 1.0x)
                    PlaybackOptionChip(
                        label = "عادی",
                        iconRes = R.drawable.ic_modern_speed,
                        isSelected = selectedSpeed == 1.0f,
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            selectedSpeed = 1.0f
                        }
                    )

                    // تکرار (Repeat)
                    PlaybackOptionChip(
                        label = "تکرار",
                        iconRes = R.drawable.ic_modern_repeat,
                        isSelected = isRepeat,
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            isRepeat = !isRepeat
                        }
                    )
                }
            }

            // Bottom Action Row: "یادداشت", "مورد علاقه", "کپی متن" (Screen 5)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) NightCard else DayCard
                ),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // کپی متن
                    ActionItemButton(
                        label = "کپی متن",
                        iconRes = R.drawable.ic_modern_copy,
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("phrase", "${phrase.arabicText} - ${phrase.persianTranslation}"))
                            Toast.makeText(context, "متن عبارت کپی شد", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // مورد علاقه
                    ActionItemButton(
                        label = "مورد علاقه",
                        icon = if (phrase.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        iconTint = if (phrase.isFavorite) FavoriteRed else null,
                        isDarkTheme = isDarkTheme,
                        onClick = { onToggleFavorite(phrase) }
                    )

                    // یادداشت
                    ActionItemButton(
                        label = "یادداشت",
                        iconRes = R.drawable.ic_modern_note_edit,
                        isDarkTheme = isDarkTheme,
                        onClick = { onOpenNoteEditor(phrase) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaybackOptionChip(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isSelected) GoldenAmber else (if (isDarkTheme) NightCard else DayCard))
                .border(
                    1.2.dp,
                    if (isSelected) GoldenAmber else (if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = if (isSelected) DarkEmeraldBg else (if (isDarkTheme) TextPrimaryDark else TextPrimaryLight),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) GoldenAmber else (if (isDarkTheme) TextSecondaryDark else TextSecondaryLight)
        )
    }
}

@Composable
private fun ActionItemButton(
    label: String,
    iconRes: Int? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: Color? = null,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = iconTint ?: (if (isDarkTheme) GoldenAmber else GoldenAmberDark),
                modifier = Modifier.size(22.dp)
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint ?: (if (isDarkTheme) GoldenAmber else GoldenAmberDark),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
        )
    }
}
