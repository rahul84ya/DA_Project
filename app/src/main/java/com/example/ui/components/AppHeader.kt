package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DemoBadgeBg
import com.example.ui.theme.DemoBadgeBorder
import com.example.ui.theme.DemoBadgeText
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.IndiaGreenLight
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark

@Composable
fun AppHeader(
    isOfficialMode: Boolean,
    onOpenServicesMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & App Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Brand Home */ }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0B192C))
                            .border(1.dp, Color(0xFFF6C343).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_dhara_wheel),
                            contentDescription = "dhara logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "dhara",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextNavyDark,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Andhra Pradesh Land Discovery",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Status Badge & Services Action
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isOfficialMode) IndiaGreenLight else DemoBadgeBg)
                            .border(
                                width = 1.dp,
                                color = if (isOfficialMode) IndiaGreen.copy(alpha = 0.3f) else DemoBadgeBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isOfficialMode) IndiaGreen else SaffronPrimary)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isOfficialMode) "Official API" else "Official Portal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isOfficialMode) IndiaGreen else DemoBadgeText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onOpenServicesMenu,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("services_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "More Services & Portals",
                            tint = TextNavyDark
                        )
                    }
                }
            }

            // Tricolour subtle indicator rule (Saffron, White, India Green)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp)
                        .background(SaffronPrimary)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp)
                        .background(Color(0xFFE2E8F0))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp)
                        .background(IndiaGreen)
                )
            }
        }
    }
}
