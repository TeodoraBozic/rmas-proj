package com.example.rmas18577.data.map

import android.net.Uri
import com.example.rmasprojekat18723.data.SignupUIEvent
import com.google.android.gms.maps.model.LatLng

sealed class MapUIEvent {
    data class LocationUpdate(val location: LatLng) : MapUIEvent()
    object LoadMarkers : MapUIEvent()
    data class ShowError(val error: String) : MapUIEvent()
    data class ImageSelected(val imageUri: String) : MapUIEvent()
    data class AddMapObject(val name: String, val location: LatLng) : MapUIEvent() // Uklonjen ID
}
