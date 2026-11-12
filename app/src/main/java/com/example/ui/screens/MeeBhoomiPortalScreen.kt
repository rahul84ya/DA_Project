package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.IndiaGreenLight
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark

private const val MEEBHOOMI_URL = "https://meebhoomi.ap.gov.in/"
private const val MEEBHOOMI_HOST = "meebhoomi.ap.gov.in"

/**
 * A user-controlled view of the official MeeBhoomi portal.
 *
 * This WebView provides direct interaction with the Government of Andhra Pradesh
 * website, with customized User-Agent and cookie handling to reduce WAF 403 Forbidden
 * rejections on state portals.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MeeBhoomiPortalScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasForbiddenError by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.apply {
                stopLoading()
                loadUrl("about:blank")
                destroy()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = IndiaGreenLight,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, IndiaGreen.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = IndiaGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Official MeeBhoomi portal", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "You are interacting directly with the Government of Andhra Pradesh website. Complete login, CAPTCHA, and any verification yourself. Dhara does not collect portal credentials or automate requests.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        if (hasForbiddenError) {
            Surface(
                color = Color(0xFFFFF3CD),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFFFEEBA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFF856404),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Access Blocked by Government Server (403)",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF856404),
                            fontSize = 13.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "The MeeBhoomi server frequently blocks requests from overseas IP addresses, cloud environments, or embedded WebViews using its state firewall. Tap 'Open in Browser' below to launch in your device browser directly.",
                        color = Color(0xFF856404),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "meebhoomi.ap.gov.in",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MEEBHOOMI_URL))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Open in Browser", fontSize = 12.sp)
            }

            Spacer(Modifier.width(6.dp))

            Button(
                onClick = {
                    hasForbiddenError = false
                    webView?.loadUrl(MEEBHOOMI_URL)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Reload", fontSize = 12.sp)
            }
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AndroidView(
                    factory = {
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.databaseEnabled = true
                            settings.allowFileAccess = false
                            settings.allowContentAccess = false
                            settings.setSupportMultipleWindows(false)

                            // Strip the WebView identifier (; wv) so WAF doesn't reject as a bot/crawler
                            val defaultUA = settings.userAgentString
                            if (defaultUA.contains("; wv")) {
                                settings.userAgentString = defaultUA.replace("; wv", "")
                            }

                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                    val host = request.url.host?.lowercase().orEmpty()
                                    return host != MEEBHOOMI_HOST && host != "www.$MEEBHOOMI_HOST"
                                }

                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                }

                                override fun onReceivedHttpError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    errorResponse: WebResourceResponse?
                                ) {
                                    super.onReceivedHttpError(view, request, errorResponse)
                                    if (request?.isForMainFrame == true && errorResponse?.statusCode == 403) {
                                        hasForbiddenError = true
                                    }
                                }
                            }
                            loadUrl(MEEBHOOMI_URL)
                            webView = this
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (isLoading) {
                    CircularProgressIndicator(color = SaffronPrimary, modifier = Modifier.size(30.dp))
                }
            }
        }
    }
}
