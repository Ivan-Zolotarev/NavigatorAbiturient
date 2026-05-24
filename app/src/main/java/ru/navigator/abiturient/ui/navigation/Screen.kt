package ru.navigator.abiturient.ui.navigation

import ru.navigator.abiturient.domain.model.CollegeType

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Documents : Screen("documents")
    data object Profile : Screen("profile")

    data class CollegeList(val type: CollegeType) : Screen("colleges/{type}") {
        companion object {
            const val ROUTE = "colleges/{type}"
            fun createRoute(type: CollegeType): String =
                "colleges/${CollegeType.toApiValue(type)}"
        }
    }

    data object CollegeDetail : Screen("college/{id}") {
        const val ROUTE = "college/{id}"
        const val ID_ARG = "id"
        fun createRoute(id: Int): String = "college/$id"
    }

    data object Favorites : Screen("favorites")
    data object Settings : Screen("settings")
}

sealed class BottomNavItem(
    val screen: Screen,
    val labelResId: Int,
    val iconName: String,
) {
    data object Home : BottomNavItem(Screen.Home, ru.navigator.abiturient.R.string.nav_home, "home")
    data object Search : BottomNavItem(Screen.Search, ru.navigator.abiturient.R.string.nav_search, "search")
    data object Documents : BottomNavItem(Screen.Documents, ru.navigator.abiturient.R.string.nav_documents, "description")
    data object Profile : BottomNavItem(Screen.Profile, ru.navigator.abiturient.R.string.nav_profile, "person")

    companion object {
        val items = listOf(Home, Search, Documents, Profile)
    }
}
