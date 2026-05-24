package ru.navigator.abiturient.domain.model

data class Specialty(
    val id: Int,
    val name: String,
    val budgetPlaces: Int?,
    val paidPlaces: Int?,
    val passingScore: Int?,
    val admissionInfo: String?,
)
