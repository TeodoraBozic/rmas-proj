package com.example.rmas18577.data.map

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.rmas18577.Filters
import com.example.rmas18577.data.`object`.ObjectUIState
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore

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
                loadMarkers() //ovo treba da azurira valjda
            }
            is MapUIEvent.ShowError -> {
                mapUIState.value = mapUIState.value.copy(
                    mapError = event.error
                )
            }
            is MapUIEvent.ApplyFilters -> {
                applyFilters(event.filters)
            }
        }
    }

    private fun loadMarkers() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("objects")
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    mapUIState.value = mapUIState.value.copy(
                        mapError = "Error loading markers: ${exception.message}"
                    )
                    return@addSnapshotListener
                }

                val markers = mutableListOf<LatLng>()
                val objects = mutableListOf<ObjectUIState>()
                result?.documents?.forEach { document ->
                    val lat = document.getDouble("latitude")
                    val lon = document.getDouble("longitude")

                    if (lat != null && lon != null) {
                        markers.add(LatLng(lat, lon))


                        val obj = ObjectUIState(
                            objectId = document.id,
                            userId = document.getString("userId") ?: "Unknown User",
                            locationName = document.getString("locationName") ?: "",
                            latitude = lat,
                            longitude = lon,
                            timestamp = document.getLong("timestamp") ?: 0L,
                            details = document.getString("details") ?: "",
                            points = document.getDouble("points") ?: 0.0

                        )
                        objects.add(obj)
                    }
                }

                mapUIState.value = mapUIState.value.copy(
                    mapMarkers = markers,
                    objects = objects
                )
                Log.d("MapViewModel", "Total markers loaded: ${markers.size}")
            }

    }

    private fun applyFilters(filters: Filters) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("objects").get()
            .addOnSuccessListener { result ->
                val filteredObjects = result.documents.mapNotNull { document ->
                    val user = document.getString("postedByUsername") ?: ""
                    val points = document.getDouble("points")?.toInt() ?: 0
                    val timestamp = document.getLong("startTime") ?: 0L
                    val objectLat = document.getDouble("latitude") ?: 0.0
                    val objectLng = document.getDouble("longitude") ?: 0.0
                    val objectLocation = LatLng(objectLat, objectLng)

                    val isWithinRadius = filters.radius?.let { radius ->
                        val userLocation = mapUIState.value.currentLocation
                        if (userLocation != null) {
                            val distance = FloatArray(1)
                            Location.distanceBetween(
                                userLocation.latitude,
                                userLocation.longitude,
                                objectLocation.latitude,
                                objectLocation.longitude,
                                distance
                            )
                            distance[0] <= radius
                        } else true
                    } ?: true

                    if (isWithinRadius &&
                        (filters.user.isEmpty() || user.startsWith(filters.user, true)) &&
                        (filters.ratingFrom == null || points >= filters.ratingFrom) &&
                        (filters.ratingTo == null || points <= filters.ratingTo) &&
                        (filters.startDate == null || timestamp >= filters.startDate) &&
                        (filters.endDate == null || timestamp <= filters.endDate)

                    ) {
                        Log.d("FilterCheck", "Object passed filter: ${document.id}, user: $user, points: $points")
                        ObjectUIState(
                            objectId = document.id,
                            locationName = document.getString("locationName") ?: "",
                            details = document.getString("details") ?: "",
                            timestamp = timestamp,
                            points = document.getDouble("points") ?: 0.0,
                            latitude = document.getDouble("latitude") ?: 0.0,
                            longitude = document.getDouble("longitude") ?: 0.0,
                            postedByUsername = user,
                            userId = document.getString("userId") ?: ""
                        )
                    } else null
                }

                mapUIState.value = mapUIState.value.copy(objects = filteredObjects)

                val filteredMarkers = filteredObjects.map { obj -> LatLng(obj.latitude, obj.longitude) }
                mapUIState.value = mapUIState.value.copy(mapMarkers = filteredMarkers)

                loadFilteredMarkers(filteredMarkers)
            }
    }

    private fun loadFilteredMarkers(filteredMarkers: List<LatLng>) {
        val mapMarkers = filteredMarkers.map { markerLocation ->
            LatLng(markerLocation.latitude, markerLocation.longitude)
        }
        mapUIState.value = mapUIState.value.copy(mapMarkers = mapMarkers)
    }

}

