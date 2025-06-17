package com.bshm.moviefinder.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bshm.moviefinder.data.Movie
import com.bshm.moviefinder.data.MovieListResponse
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
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
                val urlBuilder =
                    StringBuilder("https://api.themoviedb.org/3/search/movie?api_key=$api_key")
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
}