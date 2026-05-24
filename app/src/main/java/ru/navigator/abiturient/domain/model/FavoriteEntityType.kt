package ru.navigator.abiturient.domain.model

enum class FavoriteEntityType {
    COLLEGE,
    SPECIALTY,
    ;

    companion object {
        fun fromStorage(value: String): FavoriteEntityType = when (value) {
            "SPECIALTY" -> SPECIALTY
            else -> COLLEGE
        }
    }
}
