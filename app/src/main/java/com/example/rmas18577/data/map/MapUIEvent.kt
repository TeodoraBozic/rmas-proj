package com.example.rmas18577.data.map

import android.net.Uri
import com.example.rmas18577.Filters
import com.example.rmasprojekat18723.data.SignupUIEvent
import com.google.android.gms.maps.model.LatLng

sealed class MapUIEvent {
    data class LocationUpdate(val location: LatLng) : MapUIEvent()
    object LoadMarkers : MapUIEvent()
    data class ShowError(val error: String) : MapUIEvent()
   data class ApplyFilters(val filters: Filters) : MapUIEvent()

}
