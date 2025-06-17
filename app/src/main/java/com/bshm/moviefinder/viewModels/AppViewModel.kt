package com.bshm.moviefinder.viewModels

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bshm.moviefinder.data.Movie
import com.bshm.moviefinder.data.MovieListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class AppViewModel : ViewModel() {

    var api_key by mutableStateOf("ac946146fceb52f2399763f1878292d5")
        private set

    fun updateApiKey(newApiKey: String) {
        api_key = newApiKey
    }

    var movieList = mutableStateListOf<Movie>()
        private set

    fun apiValidation(onResult: (isValid: Boolean?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(api_key, StandardCharsets.UTF_8.toString())
                val url = URL("https://api.themoviedb.org/3/authentication?api_key=$encodedQuery")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) { onResult(true) }
                } else {
                    withContext(Dispatchers.Main) { onResult(false) }
                }
            } catch (_: Exception) {
                onResult(null)
            } finally {
                withContext(Dispatchers.Main) { }
            }
        }
    }

    fun movieSearch(
        query: String,
        includeAdult: Boolean,
        language: String,
        year: String,
        primaryReleaseYear: String,
        page: Int,
        region: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                movieList.clear()
                val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
                val urlBuilder = StringBuilder("https://api.themoviedb.org/3/search/movie?api_key=$api_key")
                urlBuilder.append("&language=$language")
                urlBuilder.append("&query=$encodedQuery")
                urlBuilder.append("&include_adult=$includeAdult")
                if (year.isNotEmpty()) urlBuilder.append("&year=$year")
                if (primaryReleaseYear.isNotEmpty()) urlBuilder.append("&primary_release_year=$primaryReleaseYear")
                urlBuilder.append("&page=$page")
                if (region.isNotEmpty()) urlBuilder.append("&region=$region")

                val result = URL(urlBuilder.toString()).readText()
                val jsonFormat = Json { ignoreUnknownKeys = true }
                val movieResponse = jsonFormat.decodeFromString<MovieListResponse>(result)
                if (movieResponse.results.isNotEmpty()) {
                    movieList.addAll(movieResponse.results)
                }
                withContext(Dispatchers.Main) { onResult(true) }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    @Composable
    fun SearchBarWithFilters(
        onSearch: (
            query: String,
            includeAdult: Boolean,
            language: String,
            year: String,
            primaryReleaseYear: String,
            page: Int,
            region: String
        ) -> Unit
    ) {
        var query by remember { mutableStateOf("") }
        var includeAdult by remember { mutableStateOf(false) }
        var language by remember { mutableStateOf("en-US") }
        var expandedLang by remember { mutableStateOf(false) }
        val languages = listOf("en-US", "es-ES", "fr-FR")
        var year by remember { mutableStateOf("") }
        var primaryReleaseYear by remember { mutableStateOf("") }
        var page by remember { mutableStateOf("1") }
        var region by remember { mutableStateOf("") }
        var expandedRegion by remember { mutableStateOf(false) }
        val regions = listOf("US", "ES", "FR")

        Column {
            TextField(value = query, onValueChange = { query = it }, label = { Text("Buscar") })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Incluir adultos")
                Checkbox(checked = includeAdult, onCheckedChange = { includeAdult = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Idioma: ")
                Button(onClick = { expandedLang = true }) { Text(language) }
                DropdownMenu(expanded = expandedLang, onDismissRequest = { expandedLang = false }) {
                    languages.forEach { lang ->
                        DropdownMenuItem(onClick = {
                            language = lang
                            expandedLang = false
                        }, text = { Text(lang) })
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Región: ")
                Button(onClick = { expandedRegion = true }) { Text(region.ifEmpty { "Seleccionar" }) }
                DropdownMenu(expanded = expandedRegion, onDismissRequest = { expandedRegion = false }) {
                    regions.forEach { reg ->
                        DropdownMenuItem(onClick = {
                            region = reg
                            expandedRegion = false
                        }, text = { Text(reg) })
                    }
                }
            }
            TextField(value = year, onValueChange = { year = it }, label = { Text("Año") })
            TextField(value = primaryReleaseYear, onValueChange = { primaryReleaseYear = it }, label = { Text("Año de estreno") })
            TextField(value = page, onValueChange = { page = it.filter { c -> c.isDigit() } }, label = { Text("Página") })
            Button(onClick = {
                onSearch(
                    query,
                    includeAdult,
                    language,
                    year,
                    primaryReleaseYear,
                    page.toIntOrNull() ?: 1,
                    region
                )
            }) {
                Text("Buscar")
            }
        }
    }
}