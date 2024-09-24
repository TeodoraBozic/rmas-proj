package com.example.rmas18577.data.map

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {

    var mapUIState = mutableStateOf(MapUIState())

    fun onEvent(event: MapUIEvent) {
        when (event) {
            is MapUIEvent.LocationUpdate -> {
                mapUIState.value = mapUIState.value.copy(
                    currentLocation = event.location
                )
            }
            is MapUIEvent.LoadMarkers -> {
                loadMarkers()
            }
            is MapUIEvent.ShowError -> {
                mapUIState.value = mapUIState.value.copy(
                    mapError = event.error
                )
            }
            is MapUIEvent.ImageSelected -> TODO()

            is MapUIEvent.AddMapObject -> {
                addMapObject(event.name, event.location) // Izmenjen poziv metode
            }
        }
    }

    private fun loadMarkers() {
        val markers = mapUIState.value.mapObjects.map { it.location } // Učitaj lokacije iz objekata
        mapUIState.value = mapUIState.value.copy(
            mapMarkers = markers // Ažuriraj markere
        )
    }

    private fun addMapObject(name: String, location: LatLng) {
        val id = System.currentTimeMillis().toString() // Generiši jedinstveni ID
        val newObject = MapObject(id, name, location)
        val updatedObjects = mapUIState.value.mapObjects + newObject // Dodaj novi objekat u listu

        mapUIState.value = mapUIState.value.copy(
            mapObjects = updatedObjects
        )

        loadMarkers() // Ponovo učitaj markere
    }
}

