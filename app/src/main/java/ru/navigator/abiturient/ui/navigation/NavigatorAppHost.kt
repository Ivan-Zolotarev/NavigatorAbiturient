package ru.navigator.abiturient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ru.navigator.abiturient.NavigatorApp

@Composable
fun NavigatorAppHost() {
    val app = LocalContext.current.applicationContext as NavigatorApp
    NavigatorRoot(
        collegeRepository = app.collegeRepository,
        favoriteRepository = app.favoriteRepository,
        documentsRepository = app.documentsRepository,
    )
}
