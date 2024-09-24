package com.example.rmas18577.data.`object`

import android.net.Uri
import com.example.rmasprojekat18723.data.SignupUIEvent


sealed class ObjectUIEvent {

    data class LocationNameChanged(val locationName: String): ObjectUIEvent()
    data class LatitudeChanged(val latitude:Double) : ObjectUIEvent()
    data class LongitudeChanged(val longitude:Double) : ObjectUIEvent()
    data class TimeStampChanged(val timeStamp: Long): ObjectUIEvent()
    data class DetailsChanged(val details: String) : ObjectUIEvent()
    data class PointsChanged(val points: Double) : ObjectUIEvent()


    object AddObjectClicked : ObjectUIEvent()


}