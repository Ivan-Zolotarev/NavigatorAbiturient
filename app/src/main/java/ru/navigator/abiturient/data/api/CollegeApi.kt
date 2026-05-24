package ru.navigator.abiturient.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.navigator.abiturient.data.dto.CollegeDto
import ru.navigator.abiturient.data.dto.CollegesResponseDto
import ru.navigator.abiturient.data.dto.DocumentsResponseDto
import ru.navigator.abiturient.data.dto.FaqResponseDto

interface CollegeApi {
    @GET("colleges")
    suspend fun getColleges(
        @Query("type") type: String? = null,
    ): CollegesResponseDto

    @GET("colleges/{id}")
    suspend fun getCollege(
        @Path("id") id: Int,
    ): CollegeDto

    @GET("colleges/search")
    suspend fun searchColleges(
        @Query("q") query: String,
        @Query("budget") budget: Boolean? = null,
        @Query("paid") paid: Boolean? = null,
        @Query("minScore") minScore: Int? = null,
        @Query("district") district: String? = null,
    ): CollegesResponseDto

    @GET("documents")
    suspend fun getDocuments(): DocumentsResponseDto

    @GET("faq")
    suspend fun getFaq(): FaqResponseDto

}
