package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark

@Composable
fun HeroSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Tagline badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = IndiaGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Fast, structured cadastral lookup",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextNavyDark
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find Andhra Pradesh land records, simply.",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextNavyDark,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Look up Adangal, 1-B Record of Rights, FMB maps, and verified pattadar shares through guided revenue location flow.",
            fontSize = 13.sp,
            color = TextMuted,
            lineHeight = 18.sp
        )
    }
}
