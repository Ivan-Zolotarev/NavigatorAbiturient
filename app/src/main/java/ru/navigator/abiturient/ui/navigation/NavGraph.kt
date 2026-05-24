package ru.navigator.abiturient.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.data.repository.DocumentsRepository
import ru.navigator.abiturient.data.repository.FavoriteRepository
import ru.navigator.abiturient.domain.model.CollegeType
import ru.navigator.abiturient.ui.colleges.CollegeDetailScreen
import ru.navigator.abiturient.ui.colleges.CollegeListScreen
import ru.navigator.abiturient.ui.documents.DocumentsScreen
import ru.navigator.abiturient.ui.home.HomeScreen
import ru.navigator.abiturient.ui.profile.FavoritesScreen
import ru.navigator.abiturient.ui.profile.ProfileScreen
import ru.navigator.abiturient.ui.profile.SettingsScreen
import ru.navigator.abiturient.ui.search.SearchScreen
import ru.navigator.abiturient.ui.splash.SplashScreen

@Composable
fun NavigatorRoot(
    collegeRepository: CollegeRepository,
    favoriteRepository: FavoriteRepository,
    documentsRepository: DocumentsRepository,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in BottomNavItem.items.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        },
    ) { innerPadding ->
        NavigatorNavHost(
            navController = navController,
            collegeRepository = collegeRepository,
            favoriteRepository = favoriteRepository,
            documentsRepository = documentsRepository,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        BottomNavItem.items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (item) {
                            BottomNavItem.Home -> Icons.Default.Home
                            BottomNavItem.Search -> Icons.Default.Search
                            BottomNavItem.Documents -> Icons.Default.Description
                            BottomNavItem.Profile -> Icons.Default.Person
                        },
                        contentDescription = stringResource(item.labelResId),
                    )
                },
                label = { Text(stringResource(item.labelResId)) },
            )
        }
    }
}

@Composable
private fun NavigatorNavHost(
    navController: NavHostController,
    collegeRepository: CollegeRepository,
    favoriteRepository: FavoriteRepository,
    documentsRepository: DocumentsRepository,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                collegeRepository = collegeRepository,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { type ->
                    navController.navigate(Screen.CollegeList.createRoute(type))
                },
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                collegeRepository = collegeRepository,
                favoriteRepository = favoriteRepository,
                onCollegeClick = { id ->
                    navController.navigate(Screen.CollegeDetail.createRoute(id))
                },
            )
        }
        composable(Screen.Documents.route) {
            DocumentsScreen(documentsRepository = documentsRepository)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                documentsRepository = documentsRepository,
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                favoriteRepository = favoriteRepository,
                onBack = { navController.popBackStack() },
                onCollegeClick = { id ->
                    navController.navigate(Screen.CollegeDetail.createRoute(id))
                },
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                collegeRepository = collegeRepository,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.CollegeList.ROUTE,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val typeValue = backStackEntry.arguments?.getString("type") ?: "state"
            val collegeType = CollegeType.fromApiValue(typeValue)
            CollegeListScreen(
                collegeType = collegeType,
                collegeRepository = collegeRepository,
                favoriteRepository = favoriteRepository,
                onBack = { navController.popBackStack() },
                onCollegeClick = { id ->
                    navController.navigate(Screen.CollegeDetail.createRoute(id))
                },
            )
        }
        composable(
            route = Screen.CollegeDetail.ROUTE,
            arguments = listOf(
                navArgument(Screen.CollegeDetail.ID_ARG) { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(Screen.CollegeDetail.ID_ARG) ?: return@composable
            CollegeDetailScreen(
                collegeId = id,
                collegeRepository = collegeRepository,
                favoriteRepository = favoriteRepository,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
