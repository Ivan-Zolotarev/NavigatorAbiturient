package ru.navigator.abiturient.data.dto

import com.google.gson.annotations.SerializedName

data class CollegeDto(
    val id: Int,
    val name: String,
    val type: String,
    @SerializedName("short_description") val shortDescription: String,
    val district: String? = null,
    val address: String?,
    @SerializedName("website_url") val websiteUrl: String?,
    val specialties: List<SpecialtyDto> = emptyList(),
    @SerializedName("tuition_cost") val tuitionCost: String? = null,
    @SerializedName("installment_available") val installmentAvailable: Boolean? = null,
    @SerializedName("special_admission_conditions") val specialAdmissionConditions: String? = null,
    @SerializedName("has_dormitory") val hasDormitory: Boolean? = null,
)

data class SpecialtyDto(
    val id: Int,
    val name: String,
    @SerializedName("budget_places") val budgetPlaces: Int? = null,
    @SerializedName("paid_places") val paidPlaces: Int? = null,
    @SerializedName("passing_score") val passingScore: Int? = null,
    @SerializedName("admission_info") val admissionInfo: String? = null,
)

data class CollegesResponseDto(
    val colleges: List<CollegeDto> = emptyList(),
)
