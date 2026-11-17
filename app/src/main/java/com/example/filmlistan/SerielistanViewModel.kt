package com.example.filmlistan

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * Represents the current UI state of the series list.
 *
 * Contains the list of series and the text currently entered
 * in the input field.
 */
data class SerielistanUiState(
    val serier: List<TvSerie> = startSerier,
    val nyTitel: String = ""
)

/**
 * Manages the data and user interactions for the series list.
 *
 * Keeps track of the UI state and provides functions for
 * adding, removing and updating series.
 */
class SerielistanViewModel : ViewModel() {

    // Observable UI state. Only this ViewModel can modify the state.
    var uiState by mutableStateOf(SerielistanUiState())
        private set

    // Keeps track of the next available ID for a newly added series.
    private var nextId = 7

    //Updates the title entered by the user.
    fun onNewTitleChange(text: String) {
        uiState = uiState.copy(nyTitel = text)
    }

    /**
     * Adds a new series to the list.
     * Trims the entered title and prevents empty titles.
     * Resets the input field after adding the series.
     */
    fun addSerie() {
        val title = uiState.nyTitel.trim()

        // Stop if the title is empty.
        if (title.isEmpty()) return

        // Create a new series with default information.
        val tvSerie = TvSerie(
            id = nextId,
            titel = title,
            ar=0,
            genre = "Okänd")

        // Prepare a unique ID for the next series.
        nextId = nextId + 1

        // Update the UI state with the new series and clear the input field.
        uiState = uiState.copy(nyTitel = "", serier = uiState.serier + tvSerie,)
    }

    /**
     * Toggles the watched status of a series.
     *
     * Uses the series ID to identify which item should be updated.
     */
    fun toggleSeen(id: Int) {
        uiState = uiState.copy(
            serier = uiState.serier.map {
                if (it.id == id) it.copy(sedd = !it.sedd) else it
            }
        )
    }

    //Removes a series from the list using its ID.
    fun removeSerie(id: Int) {
        uiState = uiState.copy(
            serier = uiState.serier.filter { it.id != id }
        )
    }
}

