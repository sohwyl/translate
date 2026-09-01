package com.example.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserPreferences
import com.example.ui.components.EslimiCorner
import com.example.ui.components.EslimiCornerBreathingOrnament
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    initialRole: String,
    initialDarkTheme: Boolean,
    initialLargeText: Boolean,
    onFinishOnboarding: (role: String, darkTheme: Boolean, largeText: Boolean) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var selectedRole by remember { mutableStateOf(initialRole) }
    var isDarkTheme by remember { mutableStateOf(initialDarkTheme) }
    var isLargeText by remember { mutableStateOf(initialLargeText) }

    OnboardingStepBackground(
        step = step,
        isDarkTheme = isDarkTheme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Step indicators and Skip / Page number
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Bar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isActive = (index + 1) == step
                        Box(
                            modifier = Modifier
                                .height(5.dp)
                                .width(if (isActive) 26.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) GoldenAmber else (if (isDarkTheme) Color(0xFF1E3A2E) else Color(0xFFD6CFC0))
                                )
                        )
                    }
                }

                if (step == 1) {
                    TextButton(
                        onClick = {
                            onFinishOnboarding(selectedRole, isDarkTheme, isLargeText)
                        }
                    ) {
                        Text(
                            text = "رد کردن",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) GoldenAmber else GoldenAmberDark
                        )
                    }
                } else {
                    Text(
                        text = "$step از ۳",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) GoldenAmber else GoldenAmberDark
                    )
                }
            }

            // Step Content with Horizontal Slide Animation
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "onboarding_step"
            ) { currentStep ->
                when (currentStep) {
                    1 -> Step1WelcomeContent(isDarkTheme)
                    2 -> Step2RoleContent(
                        selectedRole = selectedRole,
                        isDarkTheme = isDarkTheme,
                        onRoleSelect = { selectedRole = it }
                    )
                    3 -> Step3SettingsContent(
                        isDarkTheme = isDarkTheme,
                        isLargeText = isLargeText,
                        onDarkThemeToggle = { isDarkTheme = it },
                        onLargeTextToggle = { isLargeText = it }
                    )
                }
            }

            // Bottom Navigation CTA Bar
            if (step == 1) {
                // Step 1: Full-width golden button "شروع کنید"
                Button(
                    onClick = { step = 2 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldenAmber
                    ),
                    shape = RoundedCornerShape(27.dp)
                ) {
                    Text(
                        text = "شروع کنید",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkEmeraldBg
                    )
                }
            } else {
                // Step 2 & 3: Back button + Large Continue button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Next / Finish Button (Large Pill)
                    Button(
                        onClick = {
                            if (step < 3) {
                                step++
                            } else {
                                onFinishOnboarding(selectedRole, isDarkTheme, isLargeText)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldenAmber
                        ),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text(
                            text = if (step == 3) "شروع برنامه" else "ادامه",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEmeraldBg
                        )
                    }

                    // Circular Back Button on the right
                    IconButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(1.2.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), CircleShape)
                            .background(if (isDarkTheme) Color(0xFF0F2B20) else Color(0xFFFAF6EE))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "قبلی",
                            tint = if (isDarkTheme) GoldenAmber else GoldenAmberDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1WelcomeContent(isDarkTheme: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StaggeredEntrance(key = "step1_emblem", index = 0) {
            // Elegant Brand Emblem with glowing golden mandala
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_brand_emblem),
                    contentDescription = "نشان برند مترجم عربی عراقی",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(156.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        StaggeredEntrance(key = "step1_title", index = 1) {
            Text(
                text = "به مترجم عربی عراقی\nخوش آمدید",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StaggeredEntrance(key = "step1_desc", index = 2) {
            Text(
                text = "بیش از ۱۰۰۰ عبارت کاربردی در مسیر پیاده‌روی اربعین همراه شماست",
                fontSize = 13.5.sp,
                color = if (isDarkTheme) GoldenAmber else DayEmerald,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        StaggeredEntrance(key = "step1_badges", index = 3) {
            // 3 feature cards row matching the design
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureBadge(icon = Icons.Default.Search, label = "جستجوی هوشمند", isDarkTheme = isDarkTheme)
                FeatureBadge(icon = Icons.Default.VolumeUp, label = "تلفظ صوتی", isDarkTheme = isDarkTheme)
                FeatureBadge(icon = Icons.Default.WifiOff, label = "کاملاً آفلاین", isDarkTheme = isDarkTheme)
            }
        }
    }
}

@Composable
private fun FeatureBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isDarkTheme: Boolean
) {
    Box(
        modifier = Modifier
            .width(96.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkTheme) Color(0xFF0F2E22) else LightCreamSurface)
            .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), RoundedCornerShape(16.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GoldenAmber,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Step2RoleContent(
    selectedRole: String,
    isDarkTheme: Boolean,
    onRoleSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StaggeredEntrance(key = "step2_title", index = 0) {
            Text(
                text = "شما در چه وضعیتی هستید؟",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        StaggeredEntrance(key = "step2_desc", index = 1) {
            Text(
                text = "برای تنظیم بهترین مکالمات، نقش خود را در مسیر انتخاب کنید:",
                fontSize = 13.sp,
                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Role 1 Card: Iranian Pilgrim (with real photo avatar)
        StaggeredEntrance(key = "step2_card1", index = 2) {
            val isPilgrim = selectedRole == UserPreferences.ROLE_PILGRIM
            RolePhotoCard(
                title = "من زائر ایرانی هستم",
                description = "نیاز به صحبت با موکب‌داران، رانندگان و پزشکان عراقی دارم.",
                imageResId = R.drawable.img_pilgrim_avatar,
                isSelected = isPilgrim,
                isDarkTheme = isDarkTheme,
                onClick = { onRoleSelect(UserPreferences.ROLE_PILGRIM) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Role 2 Card: Mokeb Host / Iraqi (with real photo avatar)
        StaggeredEntrance(key = "step2_card2", index = 3) {
            val isMokeb = selectedRole == UserPreferences.ROLE_MOKEB_OWNER
            RolePhotoCard(
                title = "من موکب‌دار / خادم هستم",
                description = "نیاز به راهنمایی و پذیرایی از زائران عراقی و عرب‌زبان دارم.",
                imageResId = R.drawable.img_mokeb_host_avatar,
                isSelected = isMokeb,
                isDarkTheme = isDarkTheme,
                onClick = { onRoleSelect(UserPreferences.ROLE_MOKEB_OWNER) }
            )
        }
    }
}

@Composable
private fun RolePhotoCard(
    title: String,
    description: String,
    imageResId: Int,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                if (isSelected) 1.8.dp else 1.dp,
                if (isSelected) GoldenAmber else (if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)),
                RoundedCornerShape(22.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF0F2E22) else LightCreamSurface
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Radio Circle on the left
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GoldenAmber else Color.Transparent)
                    .border(1.5.dp, if (isSelected) GoldenAmber else TextMutedDark, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = DarkEmeraldBg,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text in the middle
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 11.5.sp,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                    lineHeight = 17.sp
                )
            }

            // Real Photo Avatar on the right
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDarkTheme) Color(0xFF173E2F) else Color(0xFFE2DDD1))
                    .border(1.dp, if (isSelected) GoldenAmber else Color.Transparent, RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun Step3SettingsContent(
    isDarkTheme: Boolean,
    isLargeText: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
    onLargeTextToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StaggeredEntrance(key = "step3_emblem", index = 0) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color(0xFF103325) else Color(0xFFE5DEC9))
                    .border(1.5.dp, GoldenAmber, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StaggeredEntrance(key = "step3_title", index = 1) {
            Text(
                text = "برنامه را برای خود آماده کنید",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        StaggeredEntrance(key = "step3_desc", index = 2) {
            Text(
                text = "این تنظیمات را می‌توانید هر زمان از بخش تنظیمات تغییر دهید.",
                fontSize = 12.sp,
                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        StaggeredEntrance(key = "step3_card1", index = 3) {
            // Section 1: Theme Select
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) Color(0xFF0F2E22) else LightCreamSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "پوسته برنامه",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Day Mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isDarkTheme) GoldenAmber else Color(0xFF0C241B))
                                .border(1.dp, if (!isDarkTheme) GoldenAmberLight else DarkEmeraldCardBorder, RoundedCornerShape(12.dp))
                                .clickable { onDarkThemeToggle(false) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = if (!isDarkTheme) DarkEmeraldBg else GoldenAmber
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "حالت روز",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isDarkTheme) DarkEmeraldBg else TextPrimaryDark
                                )
                            }
                        }

                        // Night Mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkTheme) GoldenAmber else Color(0xFFE0D8C8))
                                .border(1.dp, if (isDarkTheme) GoldenAmberLight else Color(0xFFC4BCA8), RoundedCornerShape(12.dp))
                                .clickable { onDarkThemeToggle(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Nightlight,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) DarkEmeraldBg else GoldenAmberDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "حالت شب",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) DarkEmeraldBg else TextPrimaryLight
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        StaggeredEntrance(key = "step3_card2", index = 4) {
            // Section 2: Large Text Toggle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFAF6EE)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "متن بزرگ برای خواندن راحت‌تر",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "مخصوص افراد بالای ۵۰ سال یا استفاده در نور کم",
                                fontSize = 11.sp,
                                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                            )
                        }

                        Switch(
                            checked = isLargeText,
                            onCheckedChange = onLargeTextToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = if (isDarkTheme) DarkEmeraldBg else Color.White,
                                checkedTrackColor = if (isDarkTheme) GoldenAmber else GoldenAmberDark,
                                uncheckedThumbColor = if (isDarkTheme) Color(0xFF757575) else Color(0xFF8D8D8D),
                                uncheckedTrackColor = if (isDarkTheme) Color(0xFF1E3A2E) else Color(0xFFE2DCCE)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDarkTheme) Color(0xFF091F17) else Color(0xFFEFE8D8))
                            .border(0.5.dp, if (isDarkTheme) Color(0xFF1B4031) else Color(0xFFDECDB7), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "السَّلاَمُ عَلَيْكُمْ",
                                fontSize = if (isLargeText) 23.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenAmber
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "سلام و درود بر شما",
                                fontSize = if (isLargeText) 14.sp else 12.sp,
                                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                            )
                        }
                    }
                }
            }
        }
    }
}
