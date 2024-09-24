package com.example.rmas18577.data.map

import com.example.rmas18577.data.`object`.ObjectUIState
import com.google.android.gms.maps.model.LatLng



data class MapUIState(
    var currentLocation: LatLng? = null,
    var mapMarkers: List<LatLng> = emptyList(),
    var objects: List<ObjectUIState> = emptyList(),
    var mapError: String? = null
)

