package ru.navigator.abiturient.domain.model

data class DocumentItem(
    val id: Int,
    val title: String,
    val content: String,
    val pdfUrl: String?,
    val linkUrl: String?,
)
