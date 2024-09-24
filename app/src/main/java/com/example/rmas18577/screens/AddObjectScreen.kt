package com.example.rmas18577.screens

import ObjectViewModel
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.rmas18577.components.ButtonComponent
import com.example.rmas18577.components.ClickableTextLogin
import com.example.rmas18577.components.MyTextFieldComponent
import com.example.rmas18577.components.NormalTextComponent
import com.example.rmas18577.components.PasswordTextFieldComponent
import com.example.rmas18577.data.`object`.ObjectUIEvent
import com.example.rmas18577.data.`object`.ObjectUIState
import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmasprojekat18723.data.SignupUIEvent
import com.example.rmasprojekat18723.data.SignupViewModel
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveBitmapToFileObject(context: Context, bitmap: Bitmap): Uri? {
    val filename = "IMG_${System.currentTimeMillis()}.png"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(storageDir, filename)

    return try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
        }
        Uri.fromFile(file)
    } catch (e: IOException) {
        Log.e("AddObjectScreen", "Failed to save bitmap to file", e)
        null
    }
}

fun uploadImageToFirebaseStorageObject(imageUri: Uri, objectId: String) {
    Log.d("AddObjectScreen", "AddObjectScreen se poziva")
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("object_images/$objectId/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")

    storageRef.putFile(imageUri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                FirebaseFirestore.getInstance().collection("objects").document(objectId)
                    .update("photoUrl", downloadUrl.toString())
                    .addOnSuccessListener {
                        Log.d("AddObjectScreen", "Object photo URL updated successfully")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("AddObjectScreen", "Failed to update photo URL", exception)
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e("AddObjectScreen", "Failed to upload image", e)
        }
}

@Composable
fun AddObjectScreen(addobjectViewModel: ObjectViewModel = viewModel()) {
    val context = LocalContext.current
    val selectedImageUri by addobjectViewModel.selectedImageUri.observeAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Dodaj objekat",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text = "Odaberi Sliku", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        imageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))



        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth() // Changed to fillMaxWidth for better alignment
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                NormalTextComponent(value = "Dodaj rutu trcanja")

                Spacer(modifier = Modifier.height(8.dp)) // Increased space for better separation

                // Username
                MyTextFieldComponent(
                    labelValue = "Ime lokacije",
                    Icons.Default.LocationOn,
                    onTextChanged = { addobjectViewModel.handleEvent(ObjectUIEvent.LocationNameChanged(it)) },
                    //errorStatus = objectUIState.locationName
                )
                MyTextFieldComponent(
                    labelValue = "Latitude",
                    Icons.Default.LocationOn,
                    onTextChanged = { text ->
                        // Pokušaj da parsiraš unos kao Double
                        val latitude = text.toDoubleOrNull()
                        if (latitude != null) {
                            addobjectViewModel.handleEvent(ObjectUIEvent.LatitudeChanged(latitude))
                        } else {
                            // Opcionalno: Prikazivanje greške ako unos nije validan
                            Log.e("MyTextFieldComponent", "Invalid latitude value: $text")
                        }
                    }
                )

// Isto važi za Longitude
                MyTextFieldComponent(
                    labelValue = "Longitude",
                    Icons.Default.LocationOn,
                    onTextChanged = { text ->
                        val longitude = text.toDoubleOrNull()
                        if (longitude != null) {
                            addobjectViewModel.handleEvent(ObjectUIEvent.LongitudeChanged(longitude))
                        } else {
                            Log.e("MyTextFieldComponent", "Invalid longitude value: $text")
                        }
                    }
                )
                MyTextFieldComponent(
                    labelValue = "Timestamp",
                    Icons.Default.AccessTime,
                    onTextChanged = { text ->
                        // Pokušaj da parsiraš unos kao Long
                        val timestamp = text.toLongOrNull()
                        if (timestamp != null) {
                            addobjectViewModel.handleEvent(ObjectUIEvent.TimeStampChanged(timestamp))
                        } else {
                            // Opcionalno: Prikazivanje greške ako unos nije validan
                            Log.e("MyTextFieldComponent", "Invalid timestamp value: $text")
                        }
                    }
                )

                MyTextFieldComponent(
                    labelValue = "Details",
                    Icons.Default.Note,
                    onTextChanged = { addobjectViewModel.handleEvent(ObjectUIEvent.DetailsChanged(it)) },
                    //errorStatus = objectUIState.locationName
                )

                Spacer(modifier = Modifier.height(8.dp))


                // Register Button
                ButtonComponent(
                    value = "Dodaj rutu",
                    onButtonClicked = { addobjectViewModel.handleEvent(ObjectUIEvent.AddObjectClicked) }
                )

                Spacer(modifier = Modifier.height(16.dp)) // Increased space for better separation


            }
        }

        // Handling back button
        SystemBackButtonHandler {
            Navigator.navigateTo(Screen.MainPage)
        }
    }
}


