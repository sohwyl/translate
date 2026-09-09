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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserPreferences
import com.example.ui.components.EslimiCorner
import com.example.ui.components.EslimiCornerBreathingOrnament
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*
import com.example.ui.utils.toPersianDigits

/**
 * Shared "liquid glass" styling for the 3 video-background onboarding steps.
 * Deliberately theme-agnostic (no isDarkTheme branching) — the point is that
 * the user shouldn't see the app's own light/dark theme yet on these steps.
 */
private val GlassTextShadow = Shadow(color = Color.Black.copy(alpha = 0.75f), offset = Offset(0f, 2f), blurRadius = 14f)

/**
 * Subtle tiled grain texture (see res/drawable-nodpi/tex_glass_noise.png) —
 * the classic "frosted glass" grain that keeps a translucent panel from
 * looking like a flat, dead rectangle. Cached per-Context.
 */
@Composable
private fun rememberGlassNoiseBrush(): Brush {
    val context = LocalContext.current
    return remember {
        val bitmap = android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.tex_glass_noise)
        val shader = android.graphics.BitmapShader(
            bitmap,
            android.graphics.Shader.TileMode.REPEAT,
            android.graphics.Shader.TileMode.REPEAT
        )
        ShaderBrush(shader)
    }
}

/**
 * Liquid-glass panel: a smoked (darkened) translucent base + subtle grain +
 * a soft light border. The dark base is what keeps content legible over any
 * busy video frame — pure bright "glass" alone wasn't enough contrast.
 */
@Composable
private fun Modifier.glassPanel(cornerRadius: Dp = 20.dp, borderAlpha: Float = 0.40f): Modifier {
    val noiseBrush = rememberGlassNoiseBrush()
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(Color.Black.copy(alpha = 0.30f))
        .background(
            Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.05f))
            )
        )
        .background(brush = noiseBrush, alpha = 0.05f)
        .border(1.dp, Color.White.copy(alpha = borderAlpha), RoundedCornerShape(cornerRadius))
}

/**
 * A minimal dark backdrop (no border/gradient, just enough smoked-glass to
 * guarantee contrast) for text that floats directly over the video with no
 * card around it — e.g. headlines and descriptions.
 */
@Composable
private fun Modifier.glassTextBackdrop(cornerRadius: Dp = 16.dp): Modifier {
    val noiseBrush = rememberGlassNoiseBrush()
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(Color.Black.copy(alpha = 0.38f))
        .background(brush = noiseBrush, alpha = 0.04f)
}

private fun glassTextStyle(
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.White,
    textAlign: TextAlign = TextAlign.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
): TextStyle = TextStyle(
    fontSize = fontSize,
    fontWeight = fontWeight,
    color = color,
    textAlign = textAlign,
    lineHeight = lineHeight,
    shadow = GlassTextShadow
)

@Composable
fun OnboardingScreen(
    initialRole: String,
    initialDarkTheme: Boolean,
    initialLargeText: Boolean,
    onFinishOnboarding: (role: String, darkTheme: Boolean, largeText: Boolean, playbackSpeed: Float) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var selectedRole by remember { mutableStateOf(initialRole) }
    var isDarkTheme by remember { mutableStateOf(initialDarkTheme) }
    var isLargeText by remember { mutableStateOf(initialLargeText) }
    var selectedPlaybackSpeed by remember { mutableFloatStateOf(1.0f) }

    val totalSteps = 4
    val isGlassStep = step <= 3 // steps 1-3: video bg + glass chrome, no app theme revealed yet

    val videoResId = remember(step) {
        when (step) {
            1 -> R.raw.onboarding_bg_step1
            2 -> R.raw.onboarding_bg_step2
            else -> R.raw.onboarding_bg_step3
        }
    }

    val scaffold: @Composable () -> Unit = {
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
                    repeat(totalSteps) { index ->
                        val isActive = (index + 1) == step
                        val inactiveColor = if (isGlassStep) {
                            Color.White.copy(alpha = 0.35f)
                        } else {
                            if (isDarkTheme) Color(0xFF1E3A2E) else Color(0xFFD6CFC0)
                        }
                        Box(
                            modifier = Modifier
                                .height(5.dp)
                                .width(if (isActive) 26.dp else 7.dp)
                                .clip(CircleShape)
                                .background(if (isActive) GoldenAmber else inactiveColor)
                        )
                    }
                }

                if (step == 1) {
                    TextButton(
                        onClick = {
                            onFinishOnboarding(selectedRole, isDarkTheme, isLargeText, selectedPlaybackSpeed)
                        }
                    ) {
                        Text(
                            text = "رد کردن",
                            style = glassTextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GoldenAmber)
                        )
                    }
                } else {
                    Text(
                        text = "${step.toPersianDigits()} از ${totalSteps.toPersianDigits()}",
                        style = if (isGlassStep) {
                            glassTextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GoldenAmber)
                        } else {
                            TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) GoldenAmber else GoldenAmberDark
                            )
                        }
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
                    1 -> Step1WelcomeContent()
                    2 -> Step2RoleContent(
                        selectedRole = selectedRole,
                        onRoleSelect = { selectedRole = it }
                    )
                    3 -> Step3AudioSpeedContent(
                        selectedSpeed = selectedPlaybackSpeed,
                        onSpeedSelect = { selectedPlaybackSpeed = it }
                    )
                    4 -> Step4SettingsContent(
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
                // Step 2, 3 & 4: Back button + Large Continue button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Circular Back Button — placed first so it lands on the right
                    // in the app's RTL layout (previous step = closer to the start).
                    IconButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .then(
                                if (isGlassStep) {
                                    Modifier
                                        .glassPanel(cornerRadius = 27.dp)
                                } else {
                                    Modifier
                                        .border(1.2.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), CircleShape)
                                        .background(if (isDarkTheme) Color(0xFF0F2B20) else Color(0xFFFAF6EE))
                                }
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "قبلی",
                            tint = if (isGlassStep) GoldenAmber else (if (isDarkTheme) GoldenAmber else GoldenAmberDark),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Next / Finish Button (Large Pill) — placed second so it lands
                    // on the left, matching forward progress in RTL reading order.
                    Button(
                        onClick = {
                            if (step < totalSteps) {
                                step++
                            } else {
                                onFinishOnboarding(selectedRole, isDarkTheme, isLargeText, selectedPlaybackSpeed)
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
                            text = if (step == totalSteps) "شروع برنامه" else "ادامه",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEmeraldBg
                        )
                    }
                }
            }
        }
    }

    if (isGlassStep) {
        OnboardingVideoBackground(videoResId = videoResId) {
            scaffold()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) DarkEmeraldBg else LightCreamBg)
        ) {
            scaffold()
        }
    }
}

@Composable
private fun Step1WelcomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StaggeredEntrance(key = "step1_title", index = 1) {
            Column(
                modifier = Modifier
                    .glassTextBackdrop()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "به مترجم عربی عراقی\nخوش آمدید",
                    style = glassTextStyle(
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "بیش از ۱۰۰۰ عبارت کاربردی در مسیر پیاده‌روی اربعین همراه شماست",
                    style = glassTextStyle(
                        fontSize = 13.5.sp,
                        color = GoldenAmberLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 6 feature cards in a 3x2 grid, liquid-glass style
        StaggeredEntrance(key = "step1_badges_row1", index = 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureBadge(icon = Icons.Default.Search, label = "جستجوی هوشمند")
                FeatureBadge(icon = Icons.Default.VolumeUp, label = "تلفظ صوتی")
                FeatureBadge(icon = Icons.Default.WifiOff, label = "کاملاً آفلاین")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        StaggeredEntrance(key = "step1_badges_row2", index = 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureBadge(icon = Icons.Default.Translate, label = "عربی و فارسی")
                FeatureBadge(icon = Icons.Default.Category, label = "۳۲ دسته‌بندی")
                FeatureBadge(icon = Icons.Default.Groups, label = "زائر و موکب‌دار")
            }
        }
    }
}

@Composable
private fun FeatureBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Box(
        modifier = Modifier
            .width(96.dp)
            .height(92.dp)
            .glassPanel(cornerRadius = 18.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.24f))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = GoldenAmberLight,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = glassTextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp
                )
            )
        }
    }
}

@Composable
private fun Step2RoleContent(
    selectedRole: String,
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
            Column(
                modifier = Modifier
                    .glassTextBackdrop()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "شما در چه وضعیتی هستید؟",
                    style = glassTextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "برای تنظیم بهترین مکالمات، نقش خود را در مسیر انتخاب کنید:",
                    style = glassTextStyle(fontSize = 13.sp, color = GoldenAmberLight, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
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
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isSelected) {
                        listOf(GoldenAmber.copy(alpha = 0.32f), GoldenAmber.copy(alpha = 0.14f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.07f))
                    }
                )
            )
            .border(
                if (isSelected) 1.8.dp else 1.dp,
                if (isSelected) GoldenAmberLight else Color.White.copy(alpha = 0.40f),
                RoundedCornerShape(22.dp)
            )
            .clickable { onClick() }
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
                    .border(1.5.dp, if (isSelected) GoldenAmber else Color.White.copy(alpha = 0.6f), CircleShape),
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
                    style = glassTextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = glassTextStyle(fontSize = 11.5.sp, color = Color(0xFFEFEAE0), lineHeight = 17.sp)
                )
            }

            // Real Photo Avatar on the right
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.dp, if (isSelected) GoldenAmberLight else Color.White.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
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
private fun Step3AudioSpeedContent(
    selectedSpeed: Float,
    onSpeedSelect: (Float) -> Unit
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
                    .glassPanel(cornerRadius = 43.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = GoldenAmberLight,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StaggeredEntrance(key = "step3_title", index = 1) {
            Column(
                modifier = Modifier
                    .glassTextBackdrop()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "سرعت پخش صدا را انتخاب کنید",
                    style = glassTextStyle(fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "تلفظ عراقی گویندگان را با سرعتی که برایتان راحت‌تر است بشنوید. این را هر زمان از تنظیمات هم می‌توانید تغییر دهید.",
                    style = glassTextStyle(fontSize = 12.5.sp, color = GoldenAmberLight, textAlign = TextAlign.Center, lineHeight = 19.sp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        val speedOptions = listOf(
            0.75f to Triple("آهسته", "برای یادگیری دقیق‌تر", "۰.۷۵×"),
            1.0f to Triple("عادی", "سرعت طبیعی گفتار", "۱.۰×"),
            1.25f to Triple("تند", "برای مرور سریع‌تر", "۱.۲۵×"),
            1.5f to Triple("خیلی تند", "برای گوش‌های حرفه‌ای", "۱.۵×")
        )

        speedOptions.forEachIndexed { i, (speedValue, labels) ->
            val (title, subtitle, badge) = labels
            val isSelected = kotlin.math.abs(selectedSpeed - speedValue) < 0.01f
            StaggeredEntrance(key = "step3_speed_$i", index = 3 + i) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (isSelected) {
                                    listOf(GoldenAmber.copy(alpha = 0.34f), GoldenAmber.copy(alpha = 0.14f))
                                } else {
                                    listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.06f))
                                }
                            )
                        )
                        .border(
                            if (isSelected) 1.8.dp else 1.dp,
                            if (isSelected) GoldenAmberLight else Color.White.copy(alpha = 0.38f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSpeedSelect(speedValue) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) GoldenAmber else Color.Transparent)
                                .border(1.5.dp, if (isSelected) GoldenAmber else Color.White.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = DarkEmeraldBg,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(text = title, style = glassTextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White))
                            Text(text = subtitle, style = glassTextStyle(fontSize = 11.sp, color = Color(0xFFEFEAE0)))
                        }

                        Text(text = badge, style = glassTextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldenAmberLight))
                    }
                }
            }
        }
    }
}

@Composable
private fun Step4SettingsContent(
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
        StaggeredEntrance(key = "step4_emblem", index = 0) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color(0xFF103325) else Color(0xFFE5DEC9))
                    .border(1.5.dp, GoldenAmber, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StaggeredEntrance(key = "step4_title", index = 1) {
            Text(
                text = "برنامه را برای خود آماده کنید",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        StaggeredEntrance(key = "step4_desc", index = 2) {
            Text(
                text = "این تنظیمات را می‌توانید هر زمان از بخش تنظیمات تغییر دهید.",
                fontSize = 12.sp,
                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        StaggeredEntrance(key = "step4_preview", index = 3) {
            AppThemeMockup(isDarkTheme = isDarkTheme, isLargeText = isLargeText)
        }

        Spacer(modifier = Modifier.height(14.dp))

        StaggeredEntrance(key = "step4_card1", index = 4) {
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

        StaggeredEntrance(key = "step4_card2", index = 5) {
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
                                text = "برای راحتی بیشتر چشمان شما، به‌خصوص در نور کم",
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
                }
            }
        }
    }
}

/**
 * A tiny "phone screen" mockup that renders a stylized miniature of the app's
 * own real UI (header, sample phrase rows, bottom nav) in the theme the user
 * is currently choosing — so the choice is concrete, not abstract.
 */
@Composable
private fun AppThemeMockup(isDarkTheme: Boolean, isLargeText: Boolean) {
    val screenBg = if (isDarkTheme) DarkEmeraldBg else LightCreamBg
    val cardBg = if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE1DACB)
    val titleColor = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
    val subColor = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight

    Box(
        modifier = Modifier
            .width(220.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(screenBg)
            .border(2.dp, GoldenAmber.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Mini header bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(GoldenAmber)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "مترجم عربی عراقی", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = titleColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two mini phrase rows
            repeat(2) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(cardBg)
                        .border(0.5.dp, cardBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (i == 0) "شُكراً" else "مِن فَضْلِك",
                            fontSize = if (isLargeText) 12.sp else 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Text(
                            text = if (i == 0) "متشکرم" else "لطفاً",
                            fontSize = if (isLargeText) 9.sp else 7.5.sp,
                            color = subColor
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = GoldenAmber,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mini bottom nav
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(cardBg)
                    .border(0.5.dp, cardBorder, RoundedCornerShape(10.dp))
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Settings).forEach { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GoldenAmber,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
