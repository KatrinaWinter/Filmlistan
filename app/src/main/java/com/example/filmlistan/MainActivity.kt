package com.example.filmlistan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.filmlistan.ui.theme.FilmlistanTheme
import androidx.compose.material3.TextField
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TextFieldDefaults

val CinemaBackground = Color(0xFF3A2F3A)
val CinemaCard = Color(0xFF413440)
val CinemaGold = Color(0xFFE8BE75)
val CinemaText = Color(0xFFF5F1E9)
val CinemaBurgundy = Color(0x9F644464)

/*
 * APPENS STARTPUNKT
 *
 * MainActivity startar appen och kopplar samman Compose
 * med appens tema och huvudsakliga skärm.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allows the app content to extend behind system bars.
        enableEdgeToEdge()

        // Sets up the Jetpack Compose UI using the app's theme.
        setContent {
            FilmlistanTheme {
                SerielistanScreen()
            }
        }
    }
}

/*
 * DATA MODEL
 *
 * Describes the information stored for each TV series.
 * Each series has an ID, title, release year, genre,
 * watched status and an optional image.
 */
data class TvSerie(
    val id: Int,
    val titel: String,
    val ar: Int,
    val genre: String,
    val sedd: Boolean = false,
    val bild: Int? = null
)

/*
 * INITIAL DATA
 *
 * Contains the TV series displayed when the app starts.
 * Each series is assigned a unique ID and an image resource.
 */
val startSerier = listOf(
    TvSerie(1,
        "Downton Abbey",
        2010,
        "Historiskt drama, romantik",
        bild = R.drawable.downton_abbey),
    TvSerie(2,
        "Grey's Anatomy",
        2005,
        "Sjukhusdrama, romantik",
        bild = R.drawable.grays_anatomy),
    TvSerie(3,
        "The Big Bang Theory",
        2007,
        "Komedi, sitcom",
        bild = R.drawable.the_big_bang_theory),
    TvSerie(4,
        "The Wheel of Time",
        2021,
        "Fantasy, äventyr, drama",
        bild = R.drawable.the_wheel_of_time),
    TvSerie(5,
        "Gilmore Girls",
        2000,
        "Komedi, drama",
        bild = R.drawable.gilmore_girls),
    TvSerie(6,
        "A Discovery of Witches",
        2018,
        "Fantasy, romantik, drama",
        bild = R.drawable.a_discovery_of_witches)
)

/*
 * MAIN SCREEN
 *
 * Displays the input field, add button and TV series list.
 * Reads the current UI state from the ViewModel and sends
 * user interactions to the appropriate ViewModel functions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerielistanScreen(viewModel: SerielistanViewModel = viewModel()
) {
    // Reads the current state managed by the ViewModel.
    val uiState = viewModel.uiState

    // Separates the series into watched and unwatched lists.
    val attSe = uiState.serier.filter { !it.sedd }
    val sedda = uiState.serier.filter { it.sedd }
    Scaffold(
        containerColor = CinemaBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "\uD83C\uDFAC Min TV-serier Lista",
                        color = CinemaGold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CinemaBackground
                )
            )
        }
    ) { innerPadding ->

        // Main layout containing the input field and series list.
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Input field where the user enters a new series title.
            TextField(
                value = uiState.nyTitel,
                onValueChange = { viewModel.onNewTitleChange(it)
                },
                label = { Text("Titel") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CinemaCard,
                    unfocusedContainerColor = CinemaCard,
                    focusedTextColor = CinemaText,
                    unfocusedTextColor = CinemaText,
                    focusedLabelColor = CinemaGold,
                    unfocusedLabelColor = CinemaGold,
                    cursorColor = CinemaGold,
                    focusedIndicatorColor = CinemaGold,
                    unfocusedIndicatorColor = CinemaGold
                )
            )

            // Adds a series. The button is disabled if the input is blank.
            Button(
                onClick = { viewModel.addSerie() },
                enabled = uiState.nyTitel.isNotBlank(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CinemaGold,
                    contentColor = CinemaBackground,
                    disabledContainerColor = CinemaBurgundy,
                    disabledContentColor = CinemaText
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🎬 Lägg till TV-serie")
            }

            /*
            * SERIES LIST
            *
            * LazyColumn displays only the items needed for the
            * visible list area and supports scrolling.
            */
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                // Section for series the user has not watched yet.
                item {
                    Text(
                        text = "🎬 Vill se",
                        style = MaterialTheme.typography.titleLarge,
                        color = CinemaGold
                    )
                }
                items(attSe, key = { it.id }) { film ->
                    SerieKort(tvSerie = film,
                        onToggle = { viewModel.toggleSeen(film.id) },
                        onRemove = { viewModel.removeSerie(film.id) }
                    )
                }

                // Section for series marked as watched.
                item {
                    Text(
                        text = "🎞️ Sett",
                        style = MaterialTheme.typography.titleLarge,
                        color = CinemaGold
                    )
                }
                items(sedda, key = { it.id }) { film ->
                    SerieKort(
                        tvSerie = film,
                        onToggle = { viewModel.toggleSeen(film.id) },
                        onRemove = { viewModel.removeSerie(film.id) }
                    )
                }
            }
        }
    }
}


/*
 * SERIES CARD
 *
 * Displays an individual TV series with its image,
 * title, release year, genre and available actions.
 */
@Composable
fun SerieKort(
    tvSerie: TvSerie,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
)  {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CinemaCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Displays the series image or a fallback placeholder.
            SerieBild(tvSerie = tvSerie)

            Column(
                modifier = Modifier
                    .padding(start = 12.dp, top = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = tvSerie.titel,
                    style = MaterialTheme.typography.titleSmall,
                    color = CinemaText
                )
                Text(
                    text = "${tvSerie.ar} ${tvSerie.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CinemaGold
                )

                // Removes this series from the list.
                TextButton(onClick = onRemove) {
                    Text("Ta bort",
                        color = CinemaGold)
                }
            }

            // Updates the watched status when the checkbox changes.
            Checkbox(
                checked = tvSerie.sedd,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = CinemaGold,
                    uncheckedColor = CinemaGold,
                    checkmarkColor = CinemaBackground
                )
            )
        }
    }
}


/*
 * SERIES IMAGE
 *
 * Displays the image assigned to a series.
 * If no image is available, the first letter of the title
 * is displayed as a simple placeholder instead.
 */
    @Composable
    fun SerieBild(
        tvSerie: TvSerie,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .size(width = 80.dp, height = 120.dp)
                .background(CinemaCard),
            contentAlignment = Alignment.Center,
        ) {
            if (tvSerie.bild != null) {
                Image(
                    painter = painterResource(tvSerie.bild),
                    contentDescription = tvSerie.titel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            } else {
                // Fallback when the series does not have an image.
                Text(
                    text = tvSerie.titel.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = CinemaGold,
                )
            }
        }
    }


/*
 * PREVIEW
 *
 * Allows the screen to be previewed in Android Studio's
 * design editor.
 */
    @Preview(showBackground = true)
    @Composable
    fun SerielistanScreenPreview() {
        FilmlistanTheme {
            SerielistanScreen(viewModel = viewModel())
        }
    }


