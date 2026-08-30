package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PhraseEntity
import com.example.ui.theme.*

@Composable
fun FullScreenPhraseDialog(
    phrase: PhraseEntity,
    isPlaying: Boolean,
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    onPlayAudio: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkEmeraldBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Action Bar - Perfect Symmetric Close Circle Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onDismiss,
                        shape = CircleShape,
                        color = DarkEmeraldCard,
                        border = BorderStroke(1.dp, GoldenAmber.copy(alpha = 0.5f)),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = TextPrimaryDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Display Content Card - Smooth Rounded Corners (24.dp)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkEmeraldSurface),
                    border = BorderStroke(2.dp, GoldenAmber)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "نمایش بزرگنمایی جهت راهنمایی مخاطب عراقی",
                            fontSize = 12.sp,
                            color = GoldenAmberLight,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            fontFamily = getPersianFontFamily(persianFontType)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Primary Top Text: Arabic_Voweled (با اعراب عراقی)
                        Text(
                            text = phrase.iraqiPronunciation,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = getArabicFontFamily(arabicFontType),
                            color = GoldenAmber,
                            lineHeight = 46.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Secondary Subtext: Arabic_Plain (عربی ساده)
                        Text(
                            text = phrase.arabicText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = getArabicFontFamily(arabicFontType),
                            color = TextPrimaryDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF091F17))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = phrase.persianTranslation,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = getPersianFontFamily(persianFontType),
                                color = TextSecondaryDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Audio Play CTA Button
                Button(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "پخش صوتی",
                        tint = DarkEmeraldBg
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlaying) "توقف پخش" else "پخش صوتی تلفظ عراقی",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkEmeraldBg
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
