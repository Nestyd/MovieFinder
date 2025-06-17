import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bshm.moviefinder.viewModels.AppViewModel

@Composable
fun MovieDetailScreen(movieId: Int, appViewModel: AppViewModel) {
    val movie = appViewModel.movieList.find { it.id == movieId }
    if (movie != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Fondo con blur y gradiente
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${movie.backdrop_path}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(12.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.7f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            )
            // Contenido principal con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/original/${movie.poster_path}",
                    contentDescription = null,
                    modifier = Modifier
                        .height(320.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .shadow(12.dp, RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = movie.overview,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Año: ${movie.release_date}", color = Color.White)
                    Text("Idioma: ${movie.original_language}", color = Color.White)
                    Text("Puntaje: ${movie.vote_average}", color = Color.Yellow)
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Película no encontrada", color = Color.White)
        }
    }
}