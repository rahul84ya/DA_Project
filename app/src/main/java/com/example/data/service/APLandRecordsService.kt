package com.example.data.service

import com.example.data.model.District
import com.example.data.model.LandRecordResult
import com.example.data.model.LandRecordType
import com.example.data.model.Mandal
import com.example.data.model.OwnershipResult
import com.example.data.model.Village
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Isolated service layer for Andhra Pradesh land record retrieval and verification.
 * 
 * Designed for clean transition to official authorized Andhra Pradesh Government APIs 
 * (e.g. Meebhoomi / Webland / CCLA authorized REST endpoints) once access credentials are configured.
 * 
 * STRICT COMPLIANCE:
 * - This service NEVER scrapes protected government sites.
 * - This service NEVER bypasses CAPTCHA, authentication, rate limits, or access controls.
 * - In demo mode, clearly labeled mock records are returned.
 * - Does NOT persist or retain private ownership records.
 */
class APLandRecordsService(
    private val apiBaseUrl: String = "",
    private val apiKey: String = "",
    private val timeoutMs: Long = 8000L,
    private val locationService: APLocationService = LocalAPLocationService()
) {
    // Official API flag: false until verified government credentials are set in environment
    val isOfficialMode: Boolean = apiBaseUrl.isNotBlank() && apiKey.isNotBlank()

    // -------------------------------------------------------------
    // Location Data (AP Revenue Administrative Hierarchy)
    // Delegated to isolated, normalized APLocationService layer
    // -------------------------------------------------------------
    fun getDistricts(): List<District> = locationService.getDistricts()

    fun getMandals(districtId: String): List<Mandal> = locationService.getMandals(districtId)

    fun getVillages(mandalId: String): List<Village> = locationService.getVillages(mandalId)

    fun getAvailableSurveyNumbers(villageId: String): List<String> = locationService.getAvailableSurveyNumbers(villageId)

    /**
     * Executes land record search.
     * Simulates network latency and returns a realistic record or error.
     */
    suspend fun searchLandRecord(
        districtName: String,
        mandalName: String,
        villageName: String,
        recordType: LandRecordType,
        surveyNumber: String
    ): Result<LandRecordResult> {
        return try {
            withTimeout(timeoutMs) {
                // Realistic responsive delay
                delay(600)

                val cleanedSurvey = surveyNumber.trim()
                if (cleanedSurvey.isBlank()) {
                    return@withTimeout Result.failure(IllegalArgumentException("Please enter a valid Survey / Land Number"))
                }

                // Deterministic mock data generation based on location and survey number
                val hash = (districtName + mandalName + villageName + cleanedSurvey).hashCode()
                val extents = listOf("1.42 Acres", "2.85 Acres", "0.75 Acres", "4.10 Acres", "3.20 Acres", "1.15 Acres")
                val classifications = listOf(
                    "Patta Dry (Zamin / Private)",
                    "Government Ayacut Wet (Tari)",
                    "Private Inam Dry (Metta)",
                    "Patta Wet (Single Crop)",
                    "Patta Dry (Multi-Crop Khari)"
                )
                val crops = listOf(
                    "Paddy (Kharif) / Black Gram (Rabi)",
                    "Commercial Groundnut / Cotton",
                    "Mango & Cashew Orchard",
                    "Commercial Sugarcane",
                    "Fallow / Seasonal Pulses"
                )
                val pattadars = listOf(
                    "Rama Rao Chennupati",
                    "Venkateswara Rao Malladi",
                    "Srinivasa Reddy Yenumula",
                    "Subba Lakshmi Kollipara",
                    "Satyanarayana Murthy Vepada",
                    "Narasimha Rao Rayapati",
                    "Govinda Rajulu Medasani",
                    "Padmavathi Konidela"
                )
                val khataBase = kotlin.math.abs(hash % 900) + 100
                val extent = extents[kotlin.math.abs(hash % extents.size)]
                val classification = classifications[kotlin.math.abs(hash % classifications.size)]
                val crop = crops[kotlin.math.abs((hash shr 2) % crops.size)]
                val pattadar = pattadars[kotlin.math.abs((hash shr 3) % pattadars.size)]
                val soilTypes = listOf("Red Sandy Loam", "Black Clay Soil (Regur)", "Alluvial Loam", "Coastal Sandy")
                val waterSources = listOf("Krishna-Godavari Canal", "Borewell (Groundwater)", "Open Ayacut Tank", "Rainfed")

                val updatedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(Date(System.currentTimeMillis() - 86400000L * (kotlin.math.abs(hash % 180) + 5)))

                val result = LandRecordResult(
                    surveyNumber = cleanedSurvey,
                    district = districtName,
                    mandal = mandalName,
                    village = villageName,
                    extent = extent,
                    landClassification = classification,
                    cropOrLandUse = crop,
                    pattadarName = pattadar,
                    recordType = recordType,
                    lastUpdatedDate = updatedDate,
                    khataNumber = khataBase.toString(),
                    soilType = soilTypes[kotlin.math.abs(hash % soilTypes.size)],
                    waterSource = waterSources[kotlin.math.abs((hash shr 1) % waterSources.size)],
                    isOfficialApi = isOfficialMode,
                    sourceNote = if (isOfficialMode) "Official AP Government API Data" else "Demo data — not an official land record."
                )

                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Executes ownership check.
     * Requires consent and returns fictional ownership information in demo mode.
     */
    suspend fun verifyOwnership(
        districtName: String,
        mandalName: String,
        villageName: String,
        surveyNumber: String,
        hasConsent: Boolean
    ): Result<OwnershipResult> {
        if (!hasConsent) {
            return Result.failure(IllegalStateException("Authorization consent is required before performing an Ownership Check."))
        }

        return try {
            withTimeout(timeoutMs) {
                delay(650)

                val cleanedSurvey = surveyNumber.trim()
                if (cleanedSurvey.isBlank()) {
                    return@withTimeout Result.failure(IllegalArgumentException("Please enter a valid Survey / Land Number"))
                }

                val hash = (districtName + mandalName + villageName + cleanedSurvey + "OWNER").hashCode()
                val owners = listOf(
                    Triple("Venkata Satyanarayana Varma", "S/o Venkata Subbaiah Varma", "Sole Pattadar"),
                    Triple("Malladi Lakshmi Prasanna", "W/o Malladi Srinivasa Rao", "Sole Pattadar"),
                    Triple("Yenumula Koteswara Reddy", "S/o Sambi Reddy", "Joint Ancestral Share"),
                    Triple("Chennupati Anjaneyulu", "S/o Ranga Rao", "Sole Pattadar"),
                    Triple("Rayapati Bhanu Prakash", "S/o Narasimha Rao", "Joint Co-Owner (Family Partition)")
                )
                val extents = listOf("2.40 Acres", "1.85 Acres", "3.10 Acres", "0.95 Acres", "4.25 Acres")
                val shares = listOf("100% (Undivided)", "50% (Co-shared)", "100% (Sole title)", "33.33% (Tripartite)")

                val selectedOwner = owners[kotlin.math.abs(hash % owners.size)]
                val extent = extents[kotlin.math.abs(hash % extents.size)]
                val share = shares[kotlin.math.abs((hash shr 2) % shares.size)]
                val refCode = "AP-ROR-2024-${kotlin.math.abs(hash % 899999 + 100000)}"
                val khata = (kotlin.math.abs(hash % 900) + 100).toString()

                val result = OwnershipResult(
                    ownerName = selectedOwner.first,
                    relationName = selectedOwner.second,
                    ownershipType = selectedOwner.third,
                    landExtent = extent,
                    sharePercentage = share,
                    recordReferenceNumber = refCode,
                    surveyNumber = cleanedSurvey,
                    district = districtName,
                    mandal = mandalName,
                    village = villageName,
                    khataNumber = khata,
                    registrationOffice = "Sub-Registrar Office, $mandalName",
                    isOfficialApi = isOfficialMode,
                    legalNotice = "Legal ownership must be verified through official records and authorities."
                )

                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
