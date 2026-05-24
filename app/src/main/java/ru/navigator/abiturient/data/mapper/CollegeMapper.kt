package ru.navigator.abiturient.data.mapper

import ru.navigator.abiturient.data.dto.CollegeDto
import ru.navigator.abiturient.data.dto.SpecialtyDto
import ru.navigator.abiturient.domain.model.College
import ru.navigator.abiturient.domain.model.CollegeType
import ru.navigator.abiturient.domain.model.Specialty

fun CollegeDto.toDomain(): College = College(
    id = id,
    name = name,
    type = CollegeType.fromApiValue(type),
    shortDescription = shortDescription,
    district = district,
    address = address,
    websiteUrl = websiteUrl,
    specialties = specialties.map { it.toDomain() },
    tuitionCost = tuitionCost,
    installmentAvailable = installmentAvailable,
    specialAdmissionConditions = specialAdmissionConditions,
    hasDormitory = hasDormitory,
)

fun SpecialtyDto.toDomain(): Specialty = Specialty(
    id = id,
    name = name,
    budgetPlaces = budgetPlaces,
    paidPlaces = paidPlaces,
    passingScore = passingScore,
    admissionInfo = admissionInfo,
)
