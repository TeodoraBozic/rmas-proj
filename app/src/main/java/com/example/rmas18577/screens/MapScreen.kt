package com.example.rmas18577.screens


import ObjectViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.ToastNotifier
import com.example.rmas18577.ToastNotifierImpl
import com.example.rmas18577.components.MyTextFieldComponent
import com.example.rmas18577.components.rememberMapViewWithLifecycle
import com.example.rmas18577.data.home.HomeViewModel
import com.example.rmas18577.data.map.MapUIEvent
import com.example.rmas18577.data.map.MapViewModel
import com.example.rmas18577.data.`object`.ObjectUIEvent
import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth





@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel(),
              objectViewModel: ObjectViewModel = viewModel(),
              homeViewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current

    var hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val showAddRouteDialog = remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val mapState = mapViewModel.mapUIState.value

    val toastNotifier = ToastNotifierImpl(context) // Inicijalizuj ToastNotifier

    if (currentUser == null) {
        Navigator.navigateTo(Screen.LogInScreen)
    } else {
        LaunchedEffect(Unit) {
            homeViewModel.getUserData()
        }

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                hasLocationPermission = true
            } else {
                Log.d("MapScreen", "Dozvola za lokaciju nije dodeljena.")
            }
        }

        LaunchedEffect(Unit) {
            if (!hasLocationPermission) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if (showAddRouteDialog.value) {
            AddRouteDialog(
                onDismiss = { showAddRouteDialog.value = false },
                currentLocation = currentLocation,
                objectViewModel = objectViewModel,
                mapViewModel = mapViewModel,
                toastNotifier = toastNotifier

            )
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            if (hasLocationPermission) {
                val mapView = rememberMapViewWithLifecycle()

                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { mapView },
                        modifier = Modifier.fillMaxSize(),
                        update = { map ->
                            map.getMapAsync { googleMap ->
                                googleMap.isMyLocationEnabled = true

                                googleMap.setOnMyLocationChangeListener { location ->
                                    val latLng = LatLng(location.latitude, location.longitude)
                                    currentLocation = latLng
                                    mapViewModel.onEvent(MapUIEvent.LocationUpdate(latLng))
                                    googleMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            latLng,
                                            15f
                                        )
                                    )
                                }


                                loadMarkers(googleMap, mapState.mapMarkers)
                            }
                        }
                    )

                    Button(
                        onClick = { showAddRouteDialog.value = true },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(120.dp, 48.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Text("Dodaj Rutu")
                    }

                    mapState.mapError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Dozvola za lokaciju nije dodeljena.")
                }
            }
        }
        SystemBackButtonHandler {
            Navigator.navigateTo(Screen.MainPage)
        }

    }
}
private fun loadMarkers(googleMap: GoogleMap, markers: List<LatLng>) {
    googleMap.clear() // Clear existing markers
    markers.forEach { markerLocation ->
        googleMap.addMarker(MarkerOptions().position(markerLocation))
    }
}



    @Composable
    fun AddRouteDialog(
        onDismiss: () -> Unit,
        currentLocation: LatLng?,
        objectViewModel: ObjectViewModel,
        mapViewModel: MapViewModel,
        toastNotifier: ToastNotifier
    ) {
        var routeName by remember { mutableStateOf("") }
        var routeDescription by remember { mutableStateOf("") }
        val timestamp = System.currentTimeMillis()

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Dodaj Rutu") },
            text = {
                Column {
                    currentLocation?.let {
                        Text(text = "Trenutna Lokacija: ${it.latitude}, ${it.longitude}")
                    } ?: Text(text = "Trenutna Lokacija: Nepoznato")

                    MyTextFieldComponent(
                        labelValue = "Ime lokacije",
                        Icons.Default.LocationOn,
                        onTextChanged = {
                            routeName = it // Postavi ime lokacije
                            objectViewModel.handleEvent(ObjectUIEvent.LocationNameChanged(it))
                        }
                    )
                    MyTextFieldComponent(
                        labelValue = "Opis lokacije",
                        Icons.Default.Note,
                        onTextChanged = {
                            routeDescription = it // Postavi opis lokacije
                            objectViewModel.handleEvent(ObjectUIEvent.DetailsChanged(it))
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (routeName.isEmpty() || routeDescription.isEmpty()) {
                            Log.d("AddRouteDialog", "Polja za ime i opis moraju biti popunjena.")
                        } else {
                            currentLocation?.let { location ->
                                val latitude = location.latitude
                                val longitude = location.longitude

                                objectViewModel.addObject(
                                    locationName = routeName,
                                    latitude = latitude,
                                    longitude = longitude,
                                    timestamp = timestamp,
                                    details = routeDescription,
                                    points = 0.0, // Po potrebi koristi null ili specijalnu vrednost
                                ) { success, message ->
                                    if (success) {
                                        Log.d("AddRouteDialog", "Ruta je uspešno dodata!")

                                        // Dodaj marker na mapu bez ID-a
                                        mapViewModel.onEvent(
                                            MapUIEvent.AddMapObject(
                                                routeName,
                                                LatLng(latitude, longitude)
                                            )
                                        )
                                        toastNotifier.showToast("Ruta uspešno dodata!")

                                    } else {
                                        Log.d(
                                            "AddRouteDialog",
                                            message ?: "Greška prilikom dodavanja rute."
                                        )
                                    }
                                }
                            } ?: run {
                                Log.d(
                                    "AddRouteDialog",
                                    "Lokacija nije dostupna, ne može se dodati ruta."
                                )
                            }
                            onDismiss()
                        }
                    }
                ) {
                    Text("Dodaj")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Otkaži")
                }
            }
        )
    }

