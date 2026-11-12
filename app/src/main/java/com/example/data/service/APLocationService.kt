package com.example.data.service

import com.example.data.model.District
import com.example.data.model.Mandal
import com.example.data.model.Village

/**
 * Reusable location service interface for Andhra Pradesh revenue administration hierarchy.
 *
 * Provides cascading administrative boundaries:
 * District -> Mandal/Tahsil -> Village -> Survey Numbers
 *
 * Can be backed by local normalized datasets or replaced with official
 * Andhra Pradesh Government APIs (e.g. Meebhoomi / Webland / CCLA) in the future.
 */
interface APLocationService {
    /** Returns all available Andhra Pradesh districts. */
    fun getDistricts(): List<District>

    /** Returns all mandals/tahsils belonging to the specified district. */
    fun getMandals(districtId: String): List<Mandal>

    /** Returns all revenue villages belonging to the specified mandal. */
    fun getVillages(mandalId: String): List<Village>

    /** Returns a specific district by its unique ID. */
    fun getDistrictById(districtId: String): District?

    /** Returns a specific mandal by its unique ID. */
    fun getMandalById(mandalId: String): Mandal?

    /** Returns a specific village by its unique ID. */
    fun getVillageById(villageId: String): Village?

    /** Returns common sample or verified survey numbers for a village. */
    fun getAvailableSurveyNumbers(villageId: String): List<String>
}
