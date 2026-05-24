package ru.navigator.abiturient.data.repository

data class LoadResult<T>(
    val data: T,
    val isFromCache: Boolean = false,
)
