package com.example.rmas18577.data.map

import com.google.android.gms.maps.model.LatLng

data class MapObject(
    val id: String,
    val name: String,
    val location: LatLng
)

data class MapUIState(
    val currentLocation: LatLng? = null,
    val mapMarkers: List<LatLng> = emptyList(),
    val mapObjects: List<MapObject> = emptyList(),
    val mapError: String? = null
)

