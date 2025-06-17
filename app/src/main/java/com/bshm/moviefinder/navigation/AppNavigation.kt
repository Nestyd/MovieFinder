package com.bshm.moviefinder.navigation


import MovieDetailScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bshm.moviefinder.ui.screens.MainScreen

import com.bshm.moviefinder.ui.screens.WelcomeScreen
import com.bshm.moviefinder.viewModels.AppViewModel


@Composable
fun AppNavigation(appViewModel: AppViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "WelcomeScreen") {
        composable("WelcomeScreen") {
            WelcomeScreen(appViewModel, navController)
        }
        composable("MainScreen") {
            MainScreen(appViewModel, navController)
        }
        composable(
            "MovieDetailScreen/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            MovieDetailScreen(movieId, appViewModel)
        }
    }
}
