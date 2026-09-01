package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

data class QuizQuestion(
    val arabic: String,
    val options: List<String>,
    val correctIndex: Int
)

@Composable
fun DailyChallengeDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val questions = remember {
        listOf(
            QuizQuestion(
                arabic = "أَيْنَ يَقَعُ الْحَرَمْ؟",
                options = listOf("حمام کجاست؟", "حرم کجاست؟", "بازار کجاست؟"),
                correctIndex = 1
            ),
            QuizQuestion(
                arabic = "كَمْ سِعْرُ هَذَا؟",
                options = listOf("قیمت این چقدر است؟", "ساعت چند است؟", "این مسیر کجاست؟"),
                correctIndex = 0
            ),
            QuizQuestion(
                arabic = "شُكْراً جَزِيلاً لَكُمْ",
                options = listOf("خیلی ممنونم", "خیلی از شما متشکرم", "خوش آمدید"),
                correctIndex = 1
            ),
            QuizQuestion(
                arabic = "أُرِيدُ مَاءً بَارِداً",
                options = listOf("آب خنک می‌خواهم", "چای گرم می‌خواهم", "غذا می‌خواهم"),
                correctIndex = 0
            )
        )
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var streakDays by remember { mutableIntStateOf(12) }

    val currentQuestion = questions[currentQuestionIndex]

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
                    text = "چالش روزانه",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                )

                Spacer(modifier = Modifier.width(42.dp))
            }

            // Center: Flame Streak + Quiz Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Flame Streak Badge (Screen 9)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) Color(0xFF1E382B) else Color(0xFFFAF2DC))
                        .border(1.5.dp, GoldenAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_modern_flame),
                        contentDescription = null,
                        tint = GoldenAmber,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$streakDays",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                )

                Text(
                    text = "روز متوالی",
                    fontSize = 13.sp,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Question Card Container
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ترجمه صحیح را انتخاب کنید",
                            fontSize = 13.sp,
                            color = if (isDarkTheme) TextMutedDark else Color(0xFF777777)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentQuestion.arabic,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // 3 Multiple Choice Options
                        currentQuestion.options.forEachIndexed { index, optionText ->
                            val isSelected = selectedOptionIndex == index
                            val isCorrect = index == currentQuestion.correctIndex
                            val showGreen = showResult && isCorrect
                            val showRed = showResult && isSelected && !isCorrect

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        when {
                                            showGreen -> Color(0xFF134E35)
                                            showRed -> Color(0xFF4A1A1A)
                                            isSelected -> if (isDarkTheme) Color(0xFF163C2B) else Color(0xFFEFE8D6)
                                            else -> if (isDarkTheme) Color(0xFF0D251C) else Color(0xFFFAF6EE)
                                        }
                                    )
                                    .border(
                                        1.5.dp,
                                        when {
                                            showGreen -> Color(0xFF2ECC71)
                                            showRed -> FavoriteRed
                                            isSelected -> GoldenAmber
                                            else -> if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
                                        },
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        selectedOptionIndex = index
                                        showResult = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = optionText,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                                    )

                                    if (showGreen) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "صحیح",
                                            tint = Color(0xFF2ECC71),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Large Golden "ادامه" Button
            Button(
                onClick = {
                    if (currentQuestionIndex < questions.lastIndex) {
                        currentQuestionIndex++
                        selectedOptionIndex = null
                        showResult = false
                    } else {
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                shape = RoundedCornerShape(27.dp)
            ) {
                Text(
                    text = "ادامه",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkEmeraldBg
                )
            }
        }
    }
}
