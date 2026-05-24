package ru.navigator.abiturient

import android.app.Application
import ru.navigator.abiturient.data.api.NetworkModule
import ru.navigator.abiturient.data.local.AppDatabase
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.data.repository.DocumentsRepository
import ru.navigator.abiturient.data.repository.FavoriteRepository

class NavigatorApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    private val collegeApi by lazy { NetworkModule.createCollegeApi() }

    val collegeRepository: CollegeRepository by lazy {
        CollegeRepository(
            context = applicationContext,
            collegeCacheDao = database.collegeCacheDao(),
            collegeApi = collegeApi,
        )
    }

    val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepository(database.favoriteDao())
    }

    val documentsRepository: DocumentsRepository by lazy {
        DocumentsRepository(
            context = applicationContext,
            collegeApi = collegeApi,
        )
    }
}
