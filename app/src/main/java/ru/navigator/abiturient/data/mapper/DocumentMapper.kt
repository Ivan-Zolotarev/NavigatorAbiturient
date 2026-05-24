package ru.navigator.abiturient.data.mapper

import ru.navigator.abiturient.data.dto.DocumentDto
import ru.navigator.abiturient.data.dto.FaqDto
import ru.navigator.abiturient.domain.model.DocumentItem
import ru.navigator.abiturient.domain.model.FaqItem

fun DocumentDto.toDomain(): DocumentItem = DocumentItem(
    id = id,
    title = title,
    content = content,
    pdfUrl = pdfUrl,
    linkUrl = linkUrl,
)

fun FaqDto.toDomain(): FaqItem = FaqItem(
    id = id,
    question = question,
    answer = answer,
)
