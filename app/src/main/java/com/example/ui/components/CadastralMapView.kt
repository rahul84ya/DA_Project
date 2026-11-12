package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.CadastralParcel
import com.example.data.model.LatLng
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark
import org.json.JSONArray
import org.json.JSONObject

enum class MapDisplayMode {
    GIS_WEBVIEW,
    VECTOR_BLUEPRINT
}

/**
 * Interactive Cadastral Map View rendering real Andhra Pradesh parcel polygons.
 *
 * Provides:
 * 1. Leaflet / OSM & Esri Satellite web map with real coordinates, pan, zoom, and popups.
 * 2. Native Compose Vector Blueprint view with pan/zoom gestures, coordinate grid, and north arrow.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CadastralMapView(
    selectedParcel: CadastralParcel,
    neighboringParcels: List<CadastralParcel> = emptyList(),
    isSatelliteMode: Boolean = false,
    showNeighbors: Boolean = true,
    onToggleSatellite: () -> Unit = {},
    onToggleNeighbors: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var displayMode by remember { mutableStateOf(MapDisplayMode.GIS_WEBVIEW) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapTilesLoading by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .testTag("cadastral_map_container")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar with Mode Switch & Layer Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(IndiaGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Survey No. ${selectedParcel.surveyNumber} Boundary",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Switch between GIS Map and Vector Blueprint
                    FilterChip(
                        selected = displayMode == MapDisplayMode.GIS_WEBVIEW,
                        onClick = { displayMode = MapDisplayMode.GIS_WEBVIEW },
                        label = { Text("GIS Map", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                    FilterChip(
                        selected = displayMode == MapDisplayMode.VECTOR_BLUEPRINT,
                        onClick = { displayMode = MapDisplayMode.VECTOR_BLUEPRINT },
                        label = { Text("Blueprint", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            // Map Viewport (Height 320dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Color(0xFFF1F5F9))
            ) {
                when (displayMode) {
                    MapDisplayMode.GIS_WEBVIEW -> {
                        val htmlContent = remember(selectedParcel, neighboringParcels, isSatelliteMode, showNeighbors) {
                            generateLeafletHtml(
                                selected = selectedParcel,
                                neighbors = if (showNeighbors) neighboringParcels else emptyList(),
                                isSatellite = isSatelliteMode
                            )
                        }

                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.allowFileAccess = true
                                    settings.setSupportZoom(true)
                                    settings.builtInZoomControls = false
                                    settings.displayZoomControls = false

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                            isMapTilesLoading = true
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            isMapTilesLoading = false
                                        }

                                        override fun onReceivedError(
                                            view: WebView?,
                                            request: WebResourceRequest?,
                                            error: WebResourceError?
                                        ) {
                                            isMapTilesLoading = false
                                        }
                                    }
                                    webViewRef = this
                                    loadDataWithBaseURL("https://apsac.ap.gov.in", htmlContent, "text/html", "UTF-8", null)
                                }
                            },
                            update = { webView ->
                                webViewRef = webView
                                webView.loadDataWithBaseURL("https://apsac.ap.gov.in", htmlContent, "text/html", "UTF-8", null)
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Overlay Controls for GIS Web Map
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Satellite / Street Toggle
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.95f),
                                shadowElevation = 2.dp,
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(
                                    onClick = onToggleSatellite,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = "Toggle Satellite",
                                        tint = if (isSatelliteMode) SaffronPrimary else TextNavyDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Re-center / Fit bounds
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.95f),
                                shadowElevation = 2.dp,
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        webViewRef?.evaluateJavascript("if (window.fitParcel) { window.fitParcel(); }", null)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CenterFocusStrong,
                                        contentDescription = "Fit Parcel",
                                        tint = TextNavyDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Neighbors toggle chip on bottom-start
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.92f),
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    FilterChip(
                                        selected = showNeighbors,
                                        onClick = onToggleNeighbors,
                                        label = {
                                            Text(
                                                text = if (showNeighbors) "Neighbors: On (${neighboringParcels.size})" else "Neighbors: Off",
                                                fontSize = 10.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = IndiaGreen.copy(alpha = 0.15f),
                                            selectedLabelColor = IndiaGreen
                                        ),
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    MapDisplayMode.VECTOR_BLUEPRINT -> {
                        // Native Compose Vector Canvas
                        NativeVectorBlueprintCanvas(
                            selected = selectedParcel,
                            neighbors = if (showNeighbors) neighboringParcels else emptyList()
                        )
                    }
                }

                // Loading spinner overlay if map is rendering
                if (isMapTilesLoading && displayMode == MapDisplayMode.GIS_WEBVIEW) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                strokeWidth = 1.5.dp,
                                modifier = Modifier.size(12.dp),
                                color = SaffronPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Loading GIS tiles...", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }

            // Bottom Legend & Scale Bar Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp, 8.dp)
                            .background(SaffronPrimary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Selected Parcel (S.No ${selectedParcel.surveyNumber})",
                        fontSize = 10.sp,
                        color = TextNavyDark,
                        fontWeight = FontWeight.Medium
                    )

                    if (showNeighbors && neighboringParcels.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp, 8.dp)
                                .border(1.dp, Color(0xFF64748B), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Neighbors (${neighboringParcels.size})",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Text(
                    text = "EPSG:4326 (WGS84)",
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }
        }
    }
}

/**
 * Pure Jetpack Compose Canvas for vector blueprint parcel inspection.
 * Supports touch pan & pinch-to-zoom, coordinate grid, and north arrow.
 */
@Composable
fun NativeVectorBlueprintCanvas(
    selected: CadastralParcel,
    neighbors: List<CadastralParcel>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 5.0f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Calculate overall bounding box
            val bbox = selected.boundingBox
            val minLat = bbox[0]
            val minLng = bbox[1]
            val maxLat = bbox[2]
            val maxLng = bbox[3]

            val latSpan = (maxLat - minLat).coerceAtLeast(0.0001)
            val lngSpan = (maxLng - minLng).coerceAtLeast(0.0001)

            val padding = 40f
            val drawWidth = width - (padding * 2)
            val drawHeight = height - (padding * 2)

            // Function to project LatLng to screen coordinates
            fun project(pt: LatLng): Offset {
                val normX = ((pt.longitude - minLng) / lngSpan).toFloat()
                val normY = (1.0f - ((pt.latitude - minLat) / latSpan).toFloat()) // Invert Y for screen
                val screenX = padding + (normX * drawWidth)
                val screenY = padding + (normY * drawHeight)
                return Offset(
                    (screenX - width / 2) * scale + width / 2 + offsetX,
                    (screenY - height / 2) * scale + height / 2 + offsetY
                )
            }

            // Draw Coordinate Grid Lines
            val gridColor = Color(0xFFE2E8F0)
            for (i in 1..4) {
                val gx = width * (i / 5f)
                val gy = height * (i / 5f)
                drawLine(gridColor, Offset(gx, 0f), Offset(gx, height), strokeWidth = 1f)
                drawLine(gridColor, Offset(0f, gy), Offset(width, gy), strokeWidth = 1f)
            }

            // 1. Draw Neighboring Parcels in subtle gray stroke
            neighbors.forEach { neighbor ->
                neighbor.coordinates.forEach { ring ->
                    if (ring.size >= 3) {
                        val path = Path()
                        val start = project(ring.first())
                        path.moveTo(start.x, start.y)
                        for (i in 1 until ring.size) {
                            val p = project(ring[i])
                            path.lineTo(p.x, p.y)
                        }
                        path.close()
                        drawPath(path, color = Color(0xFF94A3B8), style = Stroke(width = 1.5f))
                    }
                }
            }

            // 2. Draw Selected Parcel with India Saffron fill and stroke
            selected.coordinates.forEach { ring ->
                if (ring.size >= 3) {
                    val path = Path()
                    val start = project(ring.first())
                    path.moveTo(start.x, start.y)
                    for (i in 1 until ring.size) {
                        val p = project(ring[i])
                        path.lineTo(p.x, p.y)
                    }
                    path.close()

                    // Fill
                    drawPath(path, color = Color(0xFFFF9800).copy(alpha = 0.25f))
                    // Border stroke
                    drawPath(path, color = Color(0xFFE65100), style = Stroke(width = 3.5f))

                    // Draw vertices
                    ring.forEach { pt ->
                        val screenPt = project(pt)
                        drawCircle(color = Color(0xFFE65100), radius = 3.5f, center = screenPt)
                        drawCircle(color = Color.White, radius = 2.0f, center = screenPt)
                    }
                }
            }
        }

        // Compass Rose / North Indicator
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 2.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "North",
                    tint = SaffronPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Reset gestures button
        IconButton(
            onClick = {
                scale = 1.0f
                offsetX = 0f
                offsetY = 0f
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.9f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.CenterFocusStrong,
                contentDescription = "Reset View",
                tint = TextNavyDark,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Generates interactive Leaflet.js HTML string for WebView rendering.
 */
private fun generateLeafletHtml(
    selected: CadastralParcel,
    neighbors: List<CadastralParcel>,
    isSatellite: Boolean
): String {
    val selectedCoordsJson = JSONArray()
    selected.coordinates.forEach { ring ->
        val ringArray = JSONArray()
        ring.forEach { pt ->
            val coord = JSONArray()
            coord.put(pt.latitude)
            coord.put(pt.longitude)
            ringArray.put(coord)
        }
        selectedCoordsJson.put(ringArray)
    }

    val neighborsJson = JSONArray()
    neighbors.forEach { neighbor ->
        val nObj = JSONObject()
        nObj.put("surveyNumber", neighbor.surveyNumber)
        val rings = JSONArray()
        neighbor.coordinates.forEach { ring ->
            val ringArray = JSONArray()
            ring.forEach { pt ->
                val coord = JSONArray()
                coord.put(pt.latitude)
                coord.put(pt.longitude)
                ringArray.put(coord)
            }
            rings.put(ringArray)
        }
        nObj.put("coordinates", rings)
        neighborsJson.put(nObj)
    }

    val basemapTile = if (isSatellite) {
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
    } else {
        "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
    }

    val basemapAttr = if (isSatellite) {
        "&copy; Esri &mdash; Maxar, Earthstar Geographics"
    } else {
        "&copy; OpenStreetMap contributors"
    }

    val centroid = selected.centroid

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <style>
            html, body, #map {
                height: 100%;
                width: 100%;
                margin: 0;
                padding: 0;
                background: #f1f5f9;
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            }
            .leaflet-popup-content-wrapper {
                border-radius: 8px;
                padding: 4px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            }
            .parcel-badge {
                background: #E65100;
                color: #FFFFFF;
                font-size: 11px;
                font-weight: 700;
                padding: 2px 6px;
                border-radius: 4px;
                border: 1px solid #FFF;
                box-shadow: 0 1px 4px rgba(0,0,0,0.3);
                white-space: nowrap;
            }
        </style>
    </head>
    <body>
        <div id="map"></div>
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <script>
            var map = L.map('map', {
                zoomControl: true,
                attributionControl: false
            }).setView([${centroid.latitude}, ${centroid.longitude}], 16);

            L.tileLayer('$basemapTile', {
                maxZoom: 19,
                attribution: '$basemapAttr'
            }).addTo(map);

            var selectedCoords = $selectedCoordsJson;
            var neighbors = $neighborsJson;

            // Draw Neighbors
            neighbors.forEach(function(n) {
                if (n.coordinates && n.coordinates.length > 0) {
                    var nPoly = L.polygon(n.coordinates, {
                        color: '#64748B',
                        weight: 1.5,
                        fillColor: '#94A3B8',
                        fillOpacity: 0.12,
                        dashArray: '4, 4'
                    }).addTo(map);
                    nPoly.bindTooltip("Neighbor S.No: " + n.surveyNumber, { direction: 'center', permanent: false });
                }
            });

            // Draw Selected Parcel
            var selectedPolygon = L.polygon(selectedCoords, {
                color: '#E65100',
                weight: 3.5,
                fillColor: '#FF9800',
                fillOpacity: 0.35
            }).addTo(map);

            var popupContent = '<div style="font-size:12px;line-height:1.4;">' +
                '<strong style="color:#E65100;font-size:13px;">Survey No: ${selected.surveyNumber}</strong><br/>' +
                '<span>Village: <b>${selected.village}</b></span><br/>' +
                '<span>Mandal: ${selected.mandal}</span><br/>' +
                '<span>District: ${selected.district}</span><br/>' +
                '<span>Extent: <b>${selected.estimatedExtentText}</b></span><br/>' +
                '<span style="color:#64748B;font-size:10px;">Source: APSAC AP Cadastral GIS</span>' +
                '</div>';

            selectedPolygon.bindPopup(popupContent).openPopup();

            // Auto-fit to parcel with bounds padding
            function fitParcel() {
                if (selectedPolygon) {
                    map.fitBounds(selectedPolygon.getBounds(), { padding: [50, 50], maxZoom: 18 });
                }
            }
            window.fitParcel = fitParcel;
            fitParcel();
        </script>
    </body>
    </html>
    """.trimIndent()
}
