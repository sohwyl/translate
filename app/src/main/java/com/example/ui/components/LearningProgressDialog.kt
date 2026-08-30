package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LearningProgressDialog(
    learnedCount: Int = 432,
    favoritesCount: Int = 87,
    streakDays: Int = 12,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val progressPercentage = 72f
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress_ring"
    )

    val weekDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    val activityValues = listOf(0.6f, 0.8f, 0.4f, 0.95f, 0.7f, 0.85f, 0.5f)

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

                Text(
                    text = "پیشرفت شما",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                )

                IconButton(
                    onClick = { /* Share progress */ },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) NightCard else DayCard)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "اشتراک",
                        tint = GoldenAmber
                    )
                }
            }

            // Center Content: Progress Ring & Stats (Screen 8)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Percentage Ring
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val ringColor = GoldenAmber
                    val ringBgColor = if (isDarkTheme) Color(0xFF0F2B1F) else Color(0xFFE5DECC)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 12.dp.toPx()
                        // Background track
                        drawCircle(
                            color = ringBgColor,
                            radius = size.minDimension / 2 - strokeWidth / 2,
                            style = Stroke(width = strokeWidth)
                        )
                        // Progress arc
                        drawArc(
                            color = ringColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "عالیه! به همین ادامه بده",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) GoldenAmber else GoldenAmberDark
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats row (3 metric cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1
                    StatItemCard(
                        modifier = Modifier.weight(1f),
                        value = "$learnedCount",
                        label = "عبارات یادگرفته شده",
                        isDarkTheme = isDarkTheme
                    )
                    // Card 2
                    StatItemCard(
                        modifier = Modifier.weight(1f),
                        value = "$favoritesCount",
                        label = "عبارات نشان‌شده",
                        isDarkTheme = isDarkTheme
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card 3: Days streak
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) NightCard else DayCard
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "روزهای متوالی استفاده",
                            fontSize = 13.sp,
                            color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                        )
                        Text(
                            text = "$streakDays روز",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Weekly Activity Bar Chart (Screen 8)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) NightCard else DayCard
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "فعالیت هفتگی",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            weekDays.forEachIndexed { i, dayName ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.height(110.dp)
                                ) {
                                    val barHeight = (80 * activityValues[i]).dp
                                    Box(
                                        modifier = Modifier
                                            .width(18.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                            .background(if (i == 3) GoldenAmber else Color(0xFF2ECC71))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = dayName,
                                        fontSize = 11.5.sp,
                                        color = if (isDarkTheme) TextMutedDark else Color(0xFF777777)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Continue
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = "بستن",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkEmeraldBg
                )
            }
        }
    }
}

@Composable
private fun StatItemCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    isDarkTheme: Boolean
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) NightCard else DayCard
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldenAmber
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                textAlign = TextAlign.Center
            )
        }
    }
}
