package com.example.data.service

import com.example.data.model.CadastralMapQueryResult
import com.example.data.model.CadastralParcel
import com.example.data.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service for querying and retrieving verified Andhra Pradesh Cadastral GIS Parcel boundaries
 * from the official Andhra Pradesh Space Applications Centre (APSAC) ArcGIS MapServer.
 *
 * Source:
 * Official AP GIS Cadastral MapServer:
 * https://apsac.ap.gov.in/gisserver/rest/services/cadastral/Andhra_Cadastral/MapServer/0/query
 *
 * Layer ID: 0 (Parcel / administrative.sde.ap_parcel_boundary)
 * Format: GeoJSON (EPSG:4326)
 */
class APCadastralMapService(
    private val serviceUrl: String = DEFAULT_APSAC_ENDPOINT,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        const val DEFAULT_APSAC_ENDPOINT =
            "https://apsac.ap.gov.in/gisserver/rest/services/cadastral/Andhra_Cadastral/MapServer/0/query"
        const val SOURCE_NAME = "Andhra Pradesh Space Applications Centre (APSAC) — AP Official Cadastral GIS"
        const val ATTRIBUTION =
            "Official cadastral spatial data from Andhra Pradesh Space Applications Centre (APSAC), ITE&C Dept, Govt of AP. Cadastral boundaries are for visual identification and reference, and do not constitute statutory legal title certificates."
    }

    /**
     * Queries matching parcel polygon and neighboring village parcels.
     */
    suspend fun queryParcel(
        districtName: String,
        mandalName: String,
        villageName: String,
        surveyNumber: String,
        includeNeighbors: Boolean = true
    ): CadastralMapQueryResult = withContext(Dispatchers.IO) {
        val cleanSurvey = surveyNumber.trim()
        if (cleanSurvey.isBlank()) {
            return@withContext CadastralMapQueryResult(
                success = false,
                source = SOURCE_NAME,
                isOfficialSource = true,
                queryDistrict = districtName,
                queryMandal = mandalName,
                queryVillage = villageName,
                querySurveyNumber = surveyNumber,
                errorMessage = "Please enter a valid Survey Number (సర్వే నంబర్)."
            )
        }

        try {
            val distClause = buildDistrictClause(districtName)
            val cleanMandal = mandalName.replace("Urban", "").replace("Rural", "").trim()
            val cleanVillage = villageName.trim()

            // 1. First attempt: exact survey match or common subdivision notation
            val whereClause = "$distClause AND mandalnam LIKE '%$cleanMandal%' AND villagenam LIKE '%$cleanVillage%' AND (parcelnam = '$cleanSurvey' OR parcelnam LIKE '$cleanSurvey/%' OR parcelnam LIKE '$cleanSurvey-%')"
            val matchedParcels = executeGeoJsonQuery(whereClause, limit = 20)

            if (matchedParcels.isEmpty()) {
                // Fallback attempt: if village name has prefixes or suffixes, query by district, mandal, and survey
                val fallbackWhere = "$distClause AND mandalnam LIKE '%$cleanMandal%' AND (parcelnam = '$cleanSurvey' OR parcelnam LIKE '$cleanSurvey/%')"
                val fallbackMatches = executeGeoJsonQuery(fallbackWhere, limit = 20)

                if (fallbackMatches.isNotEmpty()) {
                    val neighbors = if (includeNeighbors && fallbackMatches.isNotEmpty()) {
                        val firstMatch = fallbackMatches.first()
                        val neighborWhere = "$distClause AND mandalnam LIKE '%$cleanMandal%' AND villagenam LIKE '%${firstMatch.village}%' AND objectid <> ${firstMatch.objectId}"
                        executeGeoJsonQuery(neighborWhere, limit = 12)
                    } else emptyList()

                    return@withContext CadastralMapQueryResult(
                        success = true,
                        source = SOURCE_NAME,
                        isOfficialSource = true,
                        queryDistrict = districtName,
                        queryMandal = mandalName,
                        queryVillage = villageName,
                        querySurveyNumber = cleanSurvey,
                        matchedParcels = fallbackMatches,
                        neighboringParcels = neighbors,
                        attributionNotice = ATTRIBUTION
                    )
                }

                return@withContext CadastralMapQueryResult(
                    success = false,
                    source = SOURCE_NAME,
                    isOfficialSource = true,
                    queryDistrict = districtName,
                    queryMandal = mandalName,
                    queryVillage = villageName,
                    querySurveyNumber = cleanSurvey,
                    errorMessage = "No matching parcel found in official APSAC cadastral layer for Survey No. '$cleanSurvey' in $villageName ($mandalName, $districtName). Try testing with verified samples: Srikakulam • Garugubilli (39), Guntur • Prathipadu (32), or Krishna • Jinjeru (166)."
                )
            }

            // 2. Fetch neighboring parcels in the same village for geographic context
            val neighboringParcels = if (includeNeighbors && matchedParcels.isNotEmpty()) {
                val matchedIds = matchedParcels.map { it.objectId }
                val neighborWhere = "$distClause AND mandalnam LIKE '%$cleanMandal%' AND villagenam LIKE '%$cleanVillage%' AND objectid NOT IN (${matchedIds.joinToString(",")})"
                executeGeoJsonQuery(neighborWhere, limit = 12)
            } else {
                emptyList()
            }

            CadastralMapQueryResult(
                success = true,
                source = SOURCE_NAME,
                isOfficialSource = true,
                queryDistrict = districtName,
                queryMandal = mandalName,
                queryVillage = villageName,
                querySurveyNumber = cleanSurvey,
                matchedParcels = matchedParcels,
                neighboringParcels = neighboringParcels,
                attributionNotice = ATTRIBUTION
            )
        } catch (e: Exception) {
            CadastralMapQueryResult(
                success = false,
                source = SOURCE_NAME,
                isOfficialSource = true,
                queryDistrict = districtName,
                queryMandal = mandalName,
                queryVillage = villageName,
                querySurveyNumber = cleanSurvey,
                errorMessage = "Unable to connect to official AP Cadastral GIS service: ${e.localizedMessage ?: "Network error"}. Please check network access or retry."
            )
        }
    }

    /**
     * Executes GeoJSON query on the ArcGIS MapServer endpoint.
     */
    private fun executeGeoJsonQuery(where: String, limit: Int = 20): List<CadastralParcel> {
        val urlBuilder = serviceUrl.toHttpUrlOrNull()?.newBuilder() ?: return emptyList()
        urlBuilder.addQueryParameter("where", where)
        urlBuilder.addQueryParameter("outFields", "*")
        urlBuilder.addQueryParameter("returnGeometry", "true")
        urlBuilder.addQueryParameter("resultRecordCount", limit.toString())
        urlBuilder.addQueryParameter("f", "geojson")

        val request = Request.Builder()
            .url(urlBuilder.build())
            .header("User-Agent", "Dhara-AP-LandRecords/1.0 (Android)")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            val bodyString = response.body?.string() ?: return emptyList()
            return parseGeoJsonFeatures(bodyString)
        }
    }

    /**
     * Parses GeoJSON string into list of [CadastralParcel] models.
     */
    fun parseGeoJsonFeatures(geoJsonString: String): List<CadastralParcel> {
        val parcels = mutableListOf<CadastralParcel>()
        try {
            val root = JSONObject(geoJsonString)
            val features = root.optJSONArray("features") ?: return emptyList()

            for (i in 0 until features.length()) {
                val feature = features.optJSONObject(i) ?: continue
                val properties = feature.optJSONObject("properties") ?: JSONObject()
                val geometry = feature.optJSONObject("geometry") ?: continue

                val objectId = properties.optLong("objectid", feature.optLong("id", (i + 1).toLong()))
                val surveyNumber = properties.optString("parcelnam", "").trim()
                val district = properties.optString("dstrctnam", "").trim()
                val mandal = properties.optString("mandalnam", "").trim()
                val village = properties.optString("villagenam", "").trim()
                val dmvCode = properties.optString("dmvcd", "").trim()
                val parcelType = properties.optString("parceltyp", "").trim()
                val parcelDescription = properties.optString("parceldsc", "PARCEL").trim()
                val remarks = properties.optString("remarks", "").trim()
                val area = properties.optDouble("st_area(shape)", 0.0)
                val length = properties.optDouble("st_length(shape)", 0.0)

                val rings = parseGeometryCoordinates(geometry)
                if (rings.isNotEmpty()) {
                    parcels.add(
                        CadastralParcel(
                            objectId = objectId,
                            surveyNumber = surveyNumber,
                            district = district,
                            mandal = mandal,
                            village = village,
                            dmvCode = dmvCode,
                            parcelType = parcelType,
                            parcelDescription = parcelDescription,
                            remarks = remarks,
                            areaSqUnits = area,
                            lengthUnits = length,
                            coordinates = rings,
                            rawGeoJson = feature.toString()
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Ignore parse errors on malformed features
        }
        return parcels
    }

    /**
     * Extracts polygon rings from GeoJSON Polygon or MultiPolygon geometry.
     */
    private fun parseGeometryCoordinates(geometry: JSONObject): List<List<LatLng>> {
        val result = mutableListOf<List<LatLng>>()
        val type = geometry.optString("type", "")
        val coordinates = geometry.optJSONArray("coordinates") ?: return emptyList()

        if (type.equals("Polygon", ignoreCase = true)) {
            // coordinates is Array of LinearRings (each LinearRing is Array of [lon, lat])
            for (r in 0 until coordinates.length()) {
                val ringArray = coordinates.optJSONArray(r) ?: continue
                val ringPoints = parseRing(ringArray)
                if (ringPoints.isNotEmpty()) {
                    result.add(ringPoints)
                }
            }
        } else if (type.equals("MultiPolygon", ignoreCase = true)) {
            // coordinates is Array of Polygons
            for (p in 0 until coordinates.length()) {
                val polyArray = coordinates.optJSONArray(p) ?: continue
                for (r in 0 until polyArray.length()) {
                    val ringArray = polyArray.optJSONArray(r) ?: continue
                    val ringPoints = parseRing(ringArray)
                    if (ringPoints.isNotEmpty()) {
                        result.add(ringPoints)
                    }
                }
            }
        }
        return result
    }

    private fun parseRing(ringArray: JSONArray): List<LatLng> {
        val points = mutableListOf<LatLng>()
        for (i in 0 until ringArray.length()) {
            val pt = ringArray.optJSONArray(i) ?: continue
            if (pt.length() >= 2) {
                val lon = pt.optDouble(0)
                val lat = pt.optDouble(1)
                if (!lon.isNaN() && !lat.isNaN()) {
                    points.add(LatLng(latitude = lat, longitude = lon))
                }
            }
        }
        return points
    }

    /**
     * Maps reorganized 26 Andhra Pradesh districts to the underlying dataset district names.
     */
    private fun buildDistrictClause(districtName: String): String {
        val d = districtName.trim()
        return when {
            d.contains("Palnadu", ignoreCase = true) || d.contains("Guntur", ignoreCase = true) ->
                "(dstrctnam LIKE '%Guntur%' OR dstrctnam LIKE '%Palnadu%')"
            d.contains("NTR", ignoreCase = true) || d.contains("Krishna", ignoreCase = true) ->
                "(dstrctnam LIKE '%Krishna%' OR dstrctnam LIKE '%NTR%')"
            d.contains("Anakapalli", ignoreCase = true) || d.contains("Visakhapatnam", ignoreCase = true) ->
                "(dstrctnam LIKE '%Visakhapatnam%' OR dstrctnam LIKE '%Anakapalli%')"
            d.contains("Kakinada", ignoreCase = true) || d.contains("Konaseema", ignoreCase = true) || d.contains("East Godavari", ignoreCase = true) ->
                "(dstrctnam LIKE '%East Godavari%' OR dstrctnam LIKE '%Kakinada%' OR dstrctnam LIKE '%Konaseema%')"
            d.contains("Eluru", ignoreCase = true) || d.contains("West Godavari", ignoreCase = true) ->
                "(dstrctnam LIKE '%West Godavari%' OR dstrctnam LIKE '%Eluru%')"
            d.contains("Bapatla", ignoreCase = true) || d.contains("Prakasam", ignoreCase = true) ->
                "(dstrctnam LIKE '%Prakasam%' OR dstrctnam LIKE '%Bapatla%' OR dstrctnam LIKE '%Guntur%')"
            d.contains("Tirupati", ignoreCase = true) || d.contains("Chittoor", ignoreCase = true) ->
                "(dstrctnam LIKE '%Chittoor%' OR dstrctnam LIKE '%Tirupati%' OR dstrctnam LIKE '%Nellore%')"
            d.contains("Nellore", ignoreCase = true) ->
                "(dstrctnam LIKE '%Nellore%' OR dstrctnam LIKE '%Tirupati%')"
            d.contains("Annamayya", ignoreCase = true) || d.contains("Kadapa", ignoreCase = true) ->
                "(dstrctnam LIKE '%Kadapa%' OR dstrctnam LIKE '%Annamayya%' OR dstrctnam LIKE '%Chittoor%')"
            d.contains("Nandyal", ignoreCase = true) || d.contains("Kurnool", ignoreCase = true) ->
                "(dstrctnam LIKE '%Kurnool%' OR dstrctnam LIKE '%Nandyal%')"
            d.contains("Sri Sathya Sai", ignoreCase = true) || d.contains("Ananthapuramu", ignoreCase = true) || d.contains("Anantapur", ignoreCase = true) ->
                "(dstrctnam LIKE '%Anantapur%' OR dstrctnam LIKE '%Sri Sathya Sai%')"
            d.contains("Parvathipuram", ignoreCase = true) || d.contains("Vizianagaram", ignoreCase = true) ->
                "(dstrctnam LIKE '%Vizianagaram%' OR dstrctnam LIKE '%Parvathipuram%')"
            d.contains("Alluri", ignoreCase = true) ->
                "(dstrctnam LIKE '%Visakhapatnam%' OR dstrctnam LIKE '%East Godavari%')"
            d.contains("Srikakulam", ignoreCase = true) ->
                "(dstrctnam LIKE '%Srikakulam%')"
            else ->
                "dstrctnam LIKE '%$d%'"
        }
    }
}
