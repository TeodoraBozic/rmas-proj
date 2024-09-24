package com.example.rmas18577.data.`object`


sealed class ObjectUIEvent {



    data class LocationNameChanged(val locationName: String): ObjectUIEvent()
    data class LatitudeChanged(val latitude:Double) : ObjectUIEvent()
    data class LongitudeChanged(val longitude:Double) : ObjectUIEvent()
    data class TimeStampChanged(val timeStamp: Long): ObjectUIEvent()
    data class DetailsChanged(val details: String) : ObjectUIEvent()
    data class PointsChanged(val points: Double) : ObjectUIEvent()

   // data class AddObjectClicked(val onSuccess: () -> Unit, val currentLocation: LatLng?, val timestamp: Long) : ObjectUIEvent()
    object LoadAllObjects : ObjectUIEvent()
    data class RateObject(val objectId: String, val rating: Int, val onSuccess: () -> Unit) : ObjectUIEvent()
    data class AddObjectClicked(
        val onSuccess: () -> Unit,
        val currentLocation: com.google.android.gms.maps.model.LatLng?,
        val timestamp: Long
    ) : ObjectUIEvent()



}