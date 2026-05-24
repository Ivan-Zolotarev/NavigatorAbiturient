package ru.navigator.abiturient.domain.model

data class College(
    val id: Int,
    val name: String,
    val type: CollegeType,
    val shortDescription: String,
    val district: String?,
    val address: String?,
    val websiteUrl: String?,
    val specialties: List<Specialty>,
    val tuitionCost: String?,
    val installmentAvailable: Boolean?,
    val specialAdmissionConditions: String?,
    val hasDormitory: Boolean?,
)
