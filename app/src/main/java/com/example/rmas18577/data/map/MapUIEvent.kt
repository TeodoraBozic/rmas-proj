package com.example.rmas18577.data.map

import com.google.android.gms.maps.model.LatLng

sealed class MapUIEvent {
    data class LocationUpdate(val location: LatLng) : MapUIEvent()
    data class LoadMarkers(val markers: List<LatLng>) : MapUIEvent()
    data class ShowError(val error: String) : MapUIEvent()

}