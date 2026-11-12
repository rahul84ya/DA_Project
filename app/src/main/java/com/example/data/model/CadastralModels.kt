package com.example.data.model

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

/**
 * Cadastral parcel polygon boundary and spatial metadata from verified AP GIS / APSAC service.
 */
data class CadastralParcel(
    val objectId: Long,
    val surveyNumber: String,
    val district: String,
    val mandal: String,
    val village: String,
    val dmvCode: String = "",
    val parcelType: String = "",
    val parcelDescription: String = "PARCEL",
    val remarks: String = "",
    val areaSqUnits: Double = 0.0,
    val lengthUnits: Double = 0.0,
    val coordinates: List<List<LatLng>>, // Polygon rings: list of rings, each ring has LatLng list
    val rawGeoJson: String = ""
) {
    // Helper to calculate approximate centroid for map centering
    val centroid: LatLng
        get() {
            val allPoints = coordinates.flatten()
            return if (allPoints.isEmpty()) {
                LatLng(16.5, 80.5) // Default AP center
            } else {
                val avgLat = allPoints.map { it.latitude }.average()
                val avgLng = allPoints.map { it.longitude }.average()
                LatLng(avgLat, avgLng)
            }
        }

    // Helper to get bounding box [minLat, minLng, maxLat, maxLng]
    val boundingBox: List<Double>
        get() {
            val allPoints = coordinates.flatten()
            return if (allPoints.isEmpty()) {
                listOf(16.0, 80.0, 17.0, 81.0)
            } else {
                val minLat = allPoints.minOf { it.latitude }
                val minLng = allPoints.minOf { it.longitude }
                val maxLat = allPoints.maxOf { it.latitude }
                val maxLng = allPoints.maxOf { it.longitude }
                listOf(minLat, minLng, maxLat, maxLng)
            }
        }

    // Extent approximate estimation for AP revenue display
    // st_area is in degrees squared for EPSG:4326. In AP latitude (~16 deg N), 1 sq degree is approx 12,321 sq km (~3,044,584 acres).
    val estimatedExtentText: String
        get() {
            if (areaSqUnits <= 0.0) return "Not specified"
            // At lat 16.5 N: 1 deg lat ~ 110.6 km, 1 deg lon ~ 106.8 km -> 1 deg^2 ~ 11,812 km^2 = 1.1812e10 m^2
            val areaSqMeters = areaSqUnits * 1.1812e10
            val acres = areaSqMeters / 4046.86
            val cents = (acres - acres.toInt()) * 100.0
            return if (acres >= 1.0) {
                String.format(java.util.Locale.US, "%.2f Acres (~%d Ac %.0f Cents)", acres, acres.toInt(), cents)
            } else {
                String.format(java.util.Locale.US, "%.0f Cents (~%.0f sq.m)", cents, areaSqMeters)
            }
        }
}

/**
 * Normalized response structure for AP Cadastral Survey Map queries.
 */
data class CadastralMapQueryResult(
    val success: Boolean,
    val source: String,
    val isOfficialSource: Boolean,
    val queryDistrict: String,
    val queryMandal: String,
    val queryVillage: String,
    val querySurveyNumber: String,
    val matchedParcels: List<CadastralParcel> = emptyList(),
    val neighboringParcels: List<CadastralParcel> = emptyList(),
    val errorMessage: String? = null,
    val attributionNotice: String = "Official cadastral spatial data from Andhra Pradesh Space Applications Centre (APSAC), ITE&C Dept, Govt of AP. Cadastral boundaries are for visual identification and reference, and do not constitute statutory legal title certificates."
)
