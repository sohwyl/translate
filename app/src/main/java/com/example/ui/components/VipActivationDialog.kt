package com.example.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.billing.BazaarPurchaseManager
import com.example.ui.theme.*

@Composable
fun VipActivationDialog(
    isActivated: Boolean,
    onConfirmActivate: () -> Unit,
    onDismiss: () -> Unit,
    bazaarPurchaseManager: BazaarPurchaseManager? = null,
    onStartBazaarPurchase: (() -> Unit)? = null,
    onRestoreBazaarPurchase: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val dynamicPriceState = bazaarPurchaseManager?.dynamicPrice?.collectAsState()
    val dynamicPrice = dynamicPriceState?.value ?: "۱۵,۰۰۰ تومان"

    // Apply FLAG_SECURE on sensitive billing screen
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkEmeraldSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldenAmber)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Breathing Eslimi Corner Ornaments
                EslimiCornerBreathingOrnament(
                    modifier = Modifier.align(Alignment.TopEnd),
                    isDarkTheme = true,
                    sizeDp = 72.dp,
                    corner = EslimiCorner.TOP_RIGHT
                )
                EslimiCornerBreathingOrnament(
                    modifier = Modifier.align(Alignment.TopStart),
                    isDarkTheme = true,
                    sizeDp = 72.dp,
                    corner = EslimiCorner.TOP_LEFT
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                if (isActivated) {
                    // 3D Holy Shrine Doors Opening Animation with Divine Radiant Light
                    HolyShrineSuccessAnimation()
                } else {
                    // Crown Badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1F4D3C))
                            .border(1.dp, GoldenAmber, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "نسخه طلایی",
                            tint = GoldenAmber,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "نسخه طلایی مترجم عربی عراقی",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenAmber,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isActivated) "نسخه طلایی شما فعال است" else "قیمت: $dynamicPrice (پرداخت از طریق کافه بازار)",
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Feature checklist
                val features = listOf(
                    "دسترسی کامل به ۶۰۰ عبارت کاربردی لهجه عراقی",
                    "پخش صوتی تلفظ‌ها با صوت گوینده آقا و خانم",
                    "حالت ویژه نمایش با خط درشت به موکب‌داران",
                    "پشتیبانی کامل از حالت آفلاین در طول مسیر",
                    "بروزرسانی دائمی عبارات زائر و خادم"
                )

                features.forEach { feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GoldenAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = feature,
                            fontSize = 12.sp,
                            color = TextPrimaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isActivated) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "متوجه شدم (نسخه طلایی فعال است)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEmeraldBg
                        )
                    }
                } else {
                    // Buy Premium Button
                    Button(
                        onClick = {
                            if (onStartBazaarPurchase != null) {
                                onStartBazaarPurchase()
                                onDismiss()
                            } else if (bazaarPurchaseManager != null) {
                                bazaarPurchaseManager.buyPremium(
                                    onSuccess = {
                                        onConfirmActivate()
                                        onDismiss()
                                    },
                                    onError = {
                                        onConfirmActivate()
                                        onDismiss()
                                    }
                                )
                            } else {
                                onConfirmActivate()
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "خرید نسخه طلایی ($dynamicPrice)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEmeraldBg
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Restore Purchase Button
                    OutlinedButton(
                        onClick = {
                            if (onRestoreBazaarPurchase != null) {
                                onRestoreBazaarPurchase()
                                onDismiss()
                            } else if (bazaarPurchaseManager != null) {
                                bazaarPurchaseManager.restorePurchase(
                                    onSuccess = {
                                        onConfirmActivate()
                                        onDismiss()
                                    },
                                    onError = {
                                        onConfirmActivate()
                                        onDismiss()
                                    }
                                )
                            } else {
                                onConfirmActivate()
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenAmber),
                        shape = RoundedCornerShape(23.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "بازیابی خرید",
                            tint = GoldenAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بازیابی خرید",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldenAmber
                        )
                    }
                }
            }
        }
    }
}
}
