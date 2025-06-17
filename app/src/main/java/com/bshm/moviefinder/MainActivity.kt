package com.bshm.moviefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.bshm.moviefinder.navigation.AppNavigation
import com.bshm.moviefinder.ui.theme.MovieFinderTheme
import com.bshm.moviefinder.viewModels.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        setContent {
            MovieFinderTheme {
                AppNavigation(appViewModel)

            }
        }
    }
}

