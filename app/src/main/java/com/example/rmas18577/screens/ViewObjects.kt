package com.example.rmas18577.screens

import ObjectViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.data.`object`.ObjectUIEvent
import com.example.rmas18577.data.`object`.ObjectUIState
import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ViewObjects(viewModel: ObjectViewModel = viewModel()) {

    val objectState by viewModel.objectUIState.collectAsState()


    Log.d("ViewObjects", "Current objects count: ${objectState.objects.size}")


    LaunchedEffect(Unit) {
        viewModel.handleEvent(ObjectUIEvent.LoadAllObjects)

    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Use rememberScrollState to enable vertical scrolling
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Enable vertical scrolling
                .background(Color.White) // Ensure the background is visible
        ) {
            Text(
                text = "All Registered Objects",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Check if there are no objects
            if (objectState.objects.isEmpty()) {
                Text(
                    text = "No registered objects available.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Iterate through the objects and display each in a Card
                for (obj in objectState.objects) {
                    ObjectCard(obj)
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom

            Button(
                onClick = {
                    Navigator.navigateTo(Screen.MainPage)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Go Back to Home Screen")
            }
        }
    }
    SystemBackButtonHandler {
        Navigator.navigateTo(Screen.MainPage)
    }
}


@Composable
fun ObjectCard(obj: ObjectUIState) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val locationName = if (obj.locationName.isNotEmpty()) obj.locationName else "Nema podataka"
            val details = if (obj.details.isNotEmpty()) obj.details else "Nema podataka za opis"

            Text(
                text = "Ime lokacije: $locationName",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Log.e("ObjectViewModel", "Ime lokacije: $locationName")
            Text(
                text = "Opis: $details",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Log.e("ObjectViewModel", "Detalji: $details")
            Text(
                text = "Vreme: ${formatTimestamp(obj.timestamp)}",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Ocena rute: ${obj.points}",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}


fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
