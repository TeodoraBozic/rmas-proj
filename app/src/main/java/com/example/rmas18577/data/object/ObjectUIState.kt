package com.example.rmas18577.data.`object`

import android.net.Uri

data class ObjectUIState(
    var objectId: String = "",         // Identifikator objekta
    val userId: String = " ",               // Identifikator korisnika
    val locationName: String = "",      // Naziv lokacije
    val latitude: Double = 0.0,         // Geografska širina
    val longitude: Double = 0.0,        // Geografska dužina
    val timestamp: Long = 0L,           // Vreme označavanja (u milisekundama)
    val details: String? = null,        // Dodatni detalji
    val points: Double = 0.0,           // Poeni
    var userRatings: MutableMap<String, Int> = mutableMapOf(),
    var postedByUsername: String = "",
    var objects: List<ObjectUIState> = emptyList(),


)
