package com.example.rmas18577.screens


import ObjectViewModel
import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.RatingBar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.rmas18577.Filters
import com.example.rmas18577.ToastNotifierImpl
import com.example.rmas18577.components.MyTextFieldComponent
import com.example.rmas18577.components.rememberMapViewWithLifecycle
import com.example.rmas18577.data.map.MapUIEvent
import com.example.rmas18577.data.map.MapViewModel
import com.example.rmas18577.data.`object`.ObjectUIEvent
import com.example.rmas18577.data.`object`.ObjectUIState
import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmasprojekat18723.data.SignupUIEvent
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel(),
              objectViewModel: ObjectViewModel = viewModel(),
              onSuccess: () -> Unit) {
    val context = LocalContext.current
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val mapState = mapViewModel.mapUIState.value
    var isCameraMovedManually by remember { mutableStateOf(false) }
    var showAddObjectDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedObject by remember { mutableStateOf<ObjectUIState?>(null) }





    LaunchedEffect(Unit) {
        mapViewModel.onEvent(MapUIEvent.LoadMarkers)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        if (hasLocationPermission) {
            val mapView = rememberMapViewWithLifecycle()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize(),
                    update = { map ->
                        map.getMapAsync { googleMap ->
                            googleMap.isMyLocationEnabled = true

                            googleMap.clear()

                            googleMap.setOnMyLocationChangeListener { location ->
                                val latLng = LatLng(location.latitude, location.longitude)
                                mapViewModel.onEvent(MapUIEvent.LocationUpdate(latLng))
                                if (!isCameraMovedManually) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                                }
                            }

                            googleMap.setOnCameraMoveStartedListener {
                                if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                                    isCameraMovedManually = true
                                }
                            }

                            googleMap.setOnMarkerClickListener { marker ->
                                val clickedObject = mapState.objects.find { obj ->
                                    obj.latitude == marker.position.latitude && obj.longitude == marker.position.longitude
                                }
                                if (clickedObject != null) {
                                    selectedObject = clickedObject
                                }
                                false
                            }


                            if (mapState.mapMarkers.isNotEmpty()) {
                                googleMap.clear()
                                mapState.mapMarkers.forEach { markerLocation ->
                                    Log.d("MapScreen", "Marker location: ${markerLocation.latitude}, ${markerLocation.longitude}")
                                    googleMap.addMarker(
                                        MarkerOptions()
                                            .position(markerLocation)
                                            .title("Marker at: ${markerLocation.latitude}, ${markerLocation.longitude}")
                                    )
                                    Log.d("MapScreen", "Marker added at: ${markerLocation.latitude}, ${markerLocation.longitude}")
                                }
                            } else {
                                Log.e("MapScreen", "No markers to add on the map.")
                            }
                        }
                    }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Location permission is not granted.")
            }
        }

        mapState.mapError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        selectedObject?.let { obj ->
            ObjectDetailDialog(
                obj = obj,
                objectViewModel = objectViewModel,
                mapViewModel = mapViewModel,
                onDismiss = { selectedObject = null }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Button(
                onClick = { showFilterDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(text = "Filter")
            }

            Button(
                onClick = { showAddObjectDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(text = "Add Object")
            }
        }

        if (showAddObjectDialog) {
            AddObjectDialog(
                currentLocation = mapViewModel.mapUIState.value.currentLocation,
                onDismiss = { showAddObjectDialog = false },
                onSuccess = {
                    showAddObjectDialog = false
                    mapViewModel.onEvent(MapUIEvent.LoadMarkers)
                    onSuccess()
                }
            )
        }
        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onApply = { filters ->
                    mapViewModel.onEvent(MapUIEvent.ApplyFilters(filters))
                    showFilterDialog = false
                }
            )
        }

        SystemBackButtonHandler {
            Navigator.navigateTo(Screen.MainPage)
        }


    }


}




@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (Filters) -> Unit
) {
    var user by remember { mutableStateOf("") }
    var ratingFrom by remember { mutableStateOf("") }
    var ratingTo by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var radius by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    val context = LocalContext.current

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    startDate = calendar.timeInMillis
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    endDate = calendar.timeInMillis
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Filter Options") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Username") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radius (meters)") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    TextField(
                        value = ratingFrom,
                        onValueChange = { ratingFrom = it },
                        label = { Text("Rating From") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = ratingTo,
                        onValueChange = { ratingTo = it },
                        label = { Text("Rating To") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { startDatePickerDialog.show() }) {
                    Text(text = "Pick Start Date & Time")
                }
                if (startDate != null) {
                    Text(
                        text = "Start Date & Time: ${Calendar.getInstance().apply { timeInMillis = startDate!! }.time}",
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { endDatePickerDialog.show() }) {
                    Text(text = "Pick End Date & Time")
                }
                if (endDate != null) {
                    Text(
                        text = "End Date & Time: ${Calendar.getInstance().apply { timeInMillis = endDate!! }.time}",
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))


            }
        },
        confirmButton = {
            Button(onClick = {
                val filters = Filters(
                    user = user,
                    ratingFrom = ratingFrom.toIntOrNull(),
                    ratingTo = ratingTo.toIntOrNull(),
                    startDate = startDate,
                    endDate = endDate,
                    radius = radius.toFloatOrNull()
                )
                onApply(filters)
            }) {
                Text(text = "Apply")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}



@Composable
fun ObjectDetailDialog(
    obj: ObjectUIState,
    objectViewModel: ObjectViewModel,
    mapViewModel: MapViewModel,
    onDismiss: () -> Unit
) {
    var userRating by remember { mutableStateOf(5) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isOwner = obj.userId == currentUser?.uid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = obj.locationName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Text(text = "Description: ${obj.details}")
                //Text(text = "Start Time: ${formatTimestamp(obj.startTime)}")
                Text(text = "Average Grade: ${obj.points}")
                Text(text = "Posted by: ${obj.postedByUsername}")

                Spacer(modifier = Modifier.height(16.dp))

                if (!isOwner) {
                    Text(text = "Rate this object:", modifier = Modifier.padding(vertical = 8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidView(factory = { context ->
                            RatingBar(context).apply {
                                numStars = 10
                                stepSize = 1f
                                rating = userRating.toFloat()
                                setIsIndicator(false)
                                scaleX = 0.8f
                                scaleY = 0.8f
                                setOnRatingBarChangeListener { _, rating, _ ->
                                    userRating = rating.toInt()
                                }
                            }
                        })
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Text(text = "You cannot rate your own object.", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    objectViewModel.handleEvent(ObjectUIEvent.RateObject(obj.objectId, userRating) {
                        mapViewModel.onEvent(MapUIEvent.LoadMarkers)
                        onDismiss()
                    })
                }
            ) {
                Text(text = "Submit Rating")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}



@Composable
fun AddObjectDialog(
    currentLocation: LatLng?,
    objectViewModel: ObjectViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel(),
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val objectState by objectViewModel.objectUIState.collectAsState()

    val calendar = Calendar.getInstance()

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    val toastNotifier = ToastNotifierImpl(context)

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        { _, hourOfDay, minute ->
            selectedTime = "$hourOfDay:$minute"
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Dodajte svoju rutu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                MyTextFieldComponent(
                    labelValue = "Ime lokacije",
                    Icons.Default.LocationOn,
                    onTextChanged = {
                        objectViewModel.handleEvent(
                            ObjectUIEvent.LocationNameChanged(it)
                        )
                    },
                )
                MyTextFieldComponent(
                    labelValue = "Detaljni opis",
                    Icons.Default.Note,
                    onTextChanged = {
                        objectViewModel.handleEvent(
                            ObjectUIEvent.DetailsChanged(it)
                        )
                    },
                )

                Button(onClick = { datePickerDialog.show() }) {
                    Text(text = "Izaberi datum")
                }

                if (selectedDate.isNotEmpty()) {
                    Text(
                        text = "Izabrani datum: $selectedDate",
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Button(onClick = { timePickerDialog.show() }) {
                    Text(text = "Izaberi vreme")
                }

                if (selectedTime.isNotEmpty()) {
                    Text(
                        text = "Izabrano vreme: $selectedTime",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Uzimamo trenutnu lokaciju iz mapViewModel
                val currentLocation = mapViewModel.mapUIState.value.currentLocation

                // Ako su datum i vreme izabrani, postavi timestamp
                if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                    // Parse izabrani datum i vreme u Calendar
                    val timeParts = selectedTime.split(":")
                    calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    calendar.set(Calendar.MINUTE, timeParts[1].toInt())

                    // Uzimamo timestamp iz Calendar objekta
                    val timestamp = calendar.timeInMillis

                    // Triggerovanje događaja za dodavanje objekta
                    objectViewModel.handleEvent(
                        ObjectUIEvent.AddObjectClicked(
                            onSuccess = {
                                Log.d("TAG", "Objekat je uspešno dodat!")
                                toastNotifier.showToast("Objekat je uspešno dodat!")
                                onSuccess() // Pozivamo onSuccess callback
                            },
                            currentLocation = currentLocation,
                            timestamp = timestamp
                        )
                    )
                } else {
                    toastNotifier.showToast("Molimo izaberite datum i vreme.")
                }
            }) {
                Text(text = "Dodaj objekat")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Otkaži")
            }
        }
    )
}
