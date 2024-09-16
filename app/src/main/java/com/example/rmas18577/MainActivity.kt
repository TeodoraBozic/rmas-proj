package com.example.rmas18577

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.rmas18577.app.RunTogetherApp
import com.example.rmas18577.ui.theme.RMAS18577Theme
import android.provider.MediaStore
import android.widget.Toast
import com.google.android.gms.maps.OnMapReadyCallback

class MainActivity : ComponentActivity() {

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

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

    private fun requestPermissionsAtStart() {
        permissionsLauncher.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
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
}

@Preview
@Composable
fun DefaultPreview() {
    RunTogetherApp(
        onOpenCamera = {},
        onOpenGallery = {}
    )
}
