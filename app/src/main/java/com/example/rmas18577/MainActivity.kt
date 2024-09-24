package com.example.rmas18577

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.rmas18577.app.RunTogetherApp
import com.example.rmas18577.ui.theme.RMAS18577Theme
import android.provider.MediaStore
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import com.example.rmas18577.data.map.MapViewModel
import com.example.rmas18577.screens.MapScreen
import com.example.rmas18577.services.LocationService
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity(), ToastNotifier {

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var locationPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Korisnik je prijavljen, preusmeri ga na glavnu stranicu
            Navigator.navigateTo(Screen.MainPage)
        } else {
            // Korisnik nije prijavljen, prikaži ekran za prijavu
            Navigator.navigateTo(Screen.LogInScreen)
        }
        // Enable edge-to-edge if needed

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        setContent {
            MapScreen(mapViewModel)
        }

        // Initialize the ActivityResultLauncher for camera and gallery
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Handle image capture result here
                Toast.makeText(this, "Image captured!", Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Handle image selection result here
                Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize the permissions launcher
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] == true
            val readStoragePermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true

            if (cameraPermissionGranted && readStoragePermissionGranted) {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }

        locationPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseLocationPermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationPermissionGranted || coarseLocationPermissionGranted) {
                Toast.makeText(this, "Location permissions granted!", Toast.LENGTH_SHORT).show()
                startLocationService()
            } else {
                Toast.makeText(this, "Location permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }


        requestPermissionsAtStart()

        setContent {
            RMAS18577Theme {
                RunTogetherApp(
                    onOpenCamera = { openCamera() },
                    onOpenGallery = { openGallery() }
                )
            }
        }
    }


    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun requestPermissionsAtStart() {
        permissionsLauncher.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ))

        locationPermissionsLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}

@Preview
@Composable
fun DefaultPreview() {
    RunTogetherApp(
        onOpenCamera = {},
        onOpenGallery = {}
    )
}
