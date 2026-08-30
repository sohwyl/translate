package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*

@Composable
fun HeroBanner(
    currentRole: String,
    onRoleChange: (String) -> Unit,
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier
) {
    val activeRoleBg = if (isDarkTheme) GoldenAmber else DayAccent
    val activeRoleTextColor = if (isDarkTheme) DarkEmeraldBg else Color.White
    val inactiveRoleTextColor = if (isDarkTheme) TextSecondaryDark else Color(0xFFCCCCCC)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .border(1.dp, GoldenAmber.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkEmeraldSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1B4031),
                                DarkEmeraldBg
                            )
                        )
                    )
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                DarkEmeraldBg.copy(alpha = 0.75f),
                                DarkEmeraldBg.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Breathing Eslimi Corner Ornaments in Hero Banner
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.TopEnd),
                isDarkTheme = isDarkTheme,
                sizeDp = 80.dp,
                corner = EslimiCorner.TOP_RIGHT
            )
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.TopStart),
                isDarkTheme = isDarkTheme,
                sizeDp = 80.dp,
                corner = EslimiCorner.TOP_LEFT
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Role Switcher Switch Box (RTL: Pilgrim on right, Mokeb Owner on left)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color(0xCC081D15))
                        .border(1.dp, DarkEmeraldCardBorder, RoundedCornerShape(30.dp))
                        .padding(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Pilgrim Role Button (Right)
                        val isPilgrim = currentRole == UserPreferences.ROLE_PILGRIM
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isPilgrim) activeRoleBg else Color.Transparent)
                                .clickable { onRoleChange(UserPreferences.ROLE_PILGRIM) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isPilgrim) activeRoleTextColor else inactiveRoleTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "حالت زائر",
                                fontSize = 13.sp,
                                fontWeight = if (isPilgrim) FontWeight.Bold else FontWeight.Medium,
                                color = if (isPilgrim) activeRoleTextColor else inactiveRoleTextColor
                            )
                        }

                        // Mokeb Owner Role Button (Left)
                        val isMokeb = currentRole == UserPreferences.ROLE_MOKEB_OWNER
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isMokeb) activeRoleBg else Color.Transparent)
                                .clickable { onRoleChange(UserPreferences.ROLE_MOKEB_OWNER) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isMokeb) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x33000000))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("فعال", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = activeRoleTextColor)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = if (isMokeb) activeRoleTextColor else inactiveRoleTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "حالت موکب‌دار",
                                fontSize = 13.sp,
                                fontWeight = if (isMokeb) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMokeb) activeRoleTextColor else inactiveRoleTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "عبارت را انتخاب کنید و متن عربی را به میزبان نشان دهید.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
