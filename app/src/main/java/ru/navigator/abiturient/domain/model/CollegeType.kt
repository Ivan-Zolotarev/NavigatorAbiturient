package ru.navigator.abiturient.domain.model

enum class CollegeType {
    STATE,
    PRIVATE,
    FEDERAL,
    ;

    companion object {
        fun fromApiValue(value: String): CollegeType = when (value.lowercase()) {
            "state" -> STATE
            "private" -> PRIVATE
            "federal" -> FEDERAL
            else -> STATE
        }

        fun toApiValue(type: CollegeType): String = when (type) {
            STATE -> "state"
            PRIVATE -> "private"
            FEDERAL -> "federal"
        }
    }
}
