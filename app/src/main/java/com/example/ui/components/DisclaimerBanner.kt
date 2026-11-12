package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DemoBadgeBg
import com.example.ui.theme.DemoBadgeBorder
import com.example.ui.theme.DemoBadgeText
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.IndiaGreenLight
import com.example.ui.theme.TextNavyDark

@Composable
fun ResultVerificationNotice(
    isOfficialApi: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isOfficialApi) IndiaGreenLight else DemoBadgeBg)
            .border(
                1.dp,
                if (isOfficialApi) IndiaGreen.copy(alpha = 0.3f) else DemoBadgeBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isOfficialApi) Icons.Default.VerifiedUser else Icons.Default.Info,
                contentDescription = null,
                tint = if (isOfficialApi) IndiaGreen else DemoBadgeText,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 1.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = if (isOfficialApi) "Official Andhra Pradesh Government API Data" else "Demo data — not an official land record.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOfficialApi) IndiaGreen else DemoBadgeText
                )
                Text(
                    text = "Official verification required. For statutory transactions or legal title verification, consult the Tahsildar / Registration & Stamps Department.",
                    fontSize = 11.sp,
                    color = if (isOfficialApi) IndiaGreen else DemoBadgeText.copy(alpha = 0.9f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
