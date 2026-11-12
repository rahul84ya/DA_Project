package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlaceholderFeature
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark
import com.example.ui.theme.TextNavyMedium

val ALL_PLACEHOLDER_FEATURES = listOf(
    PlaceholderFeature(
        title = "Document Vault",
        tag = "vault",
        description = "Securely organize downloaded title deeds, 1-B extracts, and mutation orders with tamper-proof checksum verification.",
        iconName = "Description"
    ),
    PlaceholderFeature(
        title = "Encumbrance Certificate (EC)",
        tag = "ec",
        description = "Track registered mortgages, charges, leaseholds, and sales deeds registered at the Sub-Registrar Office over a 30-year span.",
        iconName = "Shield"
    ),
    PlaceholderFeature(
        title = "Property Alerts",
        tag = "alerts",
        description = "Receive instant alerts if any survey number in your watch list undergoes mutation, litigation flag, or transaction filing.",
        iconName = "NotificationsActive"
    ),
    PlaceholderFeature(
        title = "FMB Map Viewer",
        tag = "fmb",
        description = "Interactive cadastral vector viewer with survey boundaries, sub-division ladders, and road/waterway overlaps.",
        iconName = "Map"
    ),
    PlaceholderFeature(
        title = "Saved Searches",
        tag = "saved",
        description = "Bookmark frequently audited land parcels and survey numbers for quick one-tap verification updates.",
        iconName = "Bookmark"
    ),
    PlaceholderFeature(
        title = "Help & Guidelines",
        tag = "help",
        description = "Step-by-step guidance on understanding RoR 1-B registers, mutation processes, and revenue terminology.",
        iconName = "HelpOutline"
    ),
    PlaceholderFeature(
        title = "Login / Profile",
        tag = "profile",
        description = "Authorized credentials management, audit logs, and verified organizational workspace access.",
        iconName = "AccountCircle"
    )
)

fun getFeatureIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Description" -> Icons.Default.Description
        "Shield" -> Icons.Default.Shield
        "NotificationsActive" -> Icons.Default.NotificationsActive
        "Map" -> Icons.Default.Map
        "Bookmark" -> Icons.Default.Bookmark
        "HelpOutline" -> Icons.Default.HelpOutline
        else -> Icons.Default.AccountCircle
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesOverviewSheet(
    onSelectFeature: (PlaceholderFeature) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Services & Features",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                    Text(
                        text = "Upcoming modules planned for verified integration",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = TextNavyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(340.dp)
            ) {
                items(ALL_PLACEHOLDER_FEATURES) { feature ->
                    Card(
                        onClick = { onSelectFeature(feature) },
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getFeatureIcon(feature.iconName),
                                    contentDescription = null,
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = feature.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextNavyDark,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Coming Soon",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderDetailSheet(
    feature: PlaceholderFeature,
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SaffronPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getFeatureIcon(feature.iconName),
                    contentDescription = null,
                    tint = SaffronPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = feature.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextNavyDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Planned Feature • Official Integration Required",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextNavyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = feature.description,
                fontSize = 14.sp,
                color = TextNavyMedium,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "This area is intentionally lightweight in this prototype release. Real-time activation will connect to authorized state portal endpoints.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onShowToast("${feature.title}: Notify list registered.")
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Notify Me When Live", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close", color = TextNavyDark)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
