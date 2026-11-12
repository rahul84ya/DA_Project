package com.example.data.model

data class District(
    val id: String,
    val name: String,
    val code: String = ""
)

data class Mandal(
    val id: String,
    val districtId: String,
    val name: String,
    val code: String = ""
)

data class Village(
    val id: String,
    val districtId: String = "",
    val mandalId: String,
    val name: String,
    val code: String = "",
    val censusCode: String = ""
) {
    // Constructor preserving compatibility with existing (id, mandalId, name, censusCode) calls
    constructor(id: String, mandalId: String, name: String, censusCode: String = "") :
        this(id = id, districtId = "", mandalId = mandalId, name = name, code = "", censusCode = censusCode)
}

enum class LandRecordType(val displayName: String, val code: String, val description: String) {
    ADANGAL(
        displayName = "Adangal",
        code = "ADANGAL",
        description = "Village Account No. 3 showing crop, cultivator & nature of land possession"
    ),
    ROR_1B(
        displayName = "1-B / Record of Rights",
        code = "1B_ROR",
        description = "Pattadar Title Register under AP Rights in Land and Pattadar Pass Books Act"
    ),
    FMB_MAP(
        displayName = "FMB Map",
        code = "FMB",
        description = "Field Measurement Book cadastral survey parcel sketch and sub-division bounds"
    ),
    E_PASSBOOK(
        displayName = "E-Passbook",
        code = "EPASSBOOK",
        description = "Digitally signed electronic Pattadar Passbook and title statement"
    )
}

data class LandRecordResult(
    val surveyNumber: String,
    val district: String,
    val mandal: String,
    val village: String,
    val extent: String,
    val landClassification: String,
    val cropOrLandUse: String,
    val pattadarName: String,
    val recordType: LandRecordType,
    val lastUpdatedDate: String,
    val khataNumber: String,
    val soilType: String = "Red Sandy Loam",
    val waterSource: String = "Canal (Krishna Delta)",
    val isOfficialApi: Boolean = false,
    val sourceNote: String = "Demo data — not an official land record."
)

data class OwnershipResult(
    val ownerName: String,
    val relationName: String,
    val ownershipType: String,
    val landExtent: String,
    val sharePercentage: String,
    val recordReferenceNumber: String,
    val surveyNumber: String,
    val district: String,
    val mandal: String,
    val village: String,
    val khataNumber: String,
    val registrationOffice: String = "Sub-Registrar Office (Registration & Stamps Dept)",
    val isOfficialApi: Boolean = false,
    val legalNotice: String = "Legal ownership must be verified through official records and authorities."
)

enum class ActiveTab {
    LAND_RECORD_SEARCH,
    OWNERSHIP_CHECK,
    SURVEY_MAP,
    MEEBHOOMI_PORTAL
}

data class PlaceholderFeature(
    val title: String,
    val tag: String,
    val description: String,
    val iconName: String
)
