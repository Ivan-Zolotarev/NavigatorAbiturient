package ru.navigator.abiturient.data.dto

import com.google.gson.annotations.SerializedName

data class DocumentDto(
    val id: Int,
    val title: String,
    val content: String,
    @SerializedName("pdf_url") val pdfUrl: String? = null,
    @SerializedName("link_url") val linkUrl: String? = null,
)

data class DocumentsResponseDto(
    val documents: List<DocumentDto> = emptyList(),
)

data class FaqDto(
    val id: Int,
    val question: String,
    val answer: String,
)

data class FaqResponseDto(
    val items: List<FaqDto> = emptyList(),
)
