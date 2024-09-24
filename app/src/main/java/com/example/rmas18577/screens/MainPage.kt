import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.components.Heading
import com.example.rmas18577.components.NormalTextComponent
import com.example.rmas18577.data.home.HomeViewModel
import com.example.rmas18577.data.`object`.ObjectUIState
import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmas18577.ui.theme.Pink80
import com.example.rmas18577.ui.theme.Purple80
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.appcheck.internal.util.Logger.TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(homeViewModel: HomeViewModel = viewModel(), mapClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    var showDialog by remember { mutableStateOf(false) }

    if (currentUser == null) {
        Navigator.navigateTo(Screen.LogInScreen)
    } else {
        LaunchedEffect(Unit) {
            homeViewModel.getUserData()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Main Page", color = Color.White) },
                    actions = {
                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(context, "Uspešno ste se odjavili", Toast.LENGTH_SHORT).show()
                            Navigator.navigateTo(Screen.LogInScreen)
                        }) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple80)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFFAFAFA)) // svetla pozadina
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                NormalTextComponent(value = "Zvanicno dobrodosao na app")
                Spacer(modifier = Modifier.height(20.dp))
                Heading(value = "Vidi sve nove info")
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { mapClick() },
                    modifier = Modifier.fillMaxWidth().heightIn(48.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(listOf(Purple80, Pink80)),
                                shape = RoundedCornerShape(50.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Show Map", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth().heightIn(48.dp)
                ) {
                    Text(text = "Vidi sve svoje rute", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                // Prikaz dijaloga ako je showDialog true
                if (showDialog) {
                    UserObjectsDialog(onDismiss = { showDialog = false }, homeViewModel = homeViewModel)
                }

               // UserObjectsDialog(homeViewModel = homeViewModel)
            }
        }

        SystemBackButtonHandler {
            Navigator.navigateTo(Screen.HomePage)
        }
    }
}

@Composable
fun UserObjectsDialog(onDismiss: () -> Unit, homeViewModel: HomeViewModel) {
    // Fetch user objects
    LaunchedEffect(Unit) {
        homeViewModel.fetchUserObjects()
    }

    // Observe changes in user objects
    val userObjects by homeViewModel.userObjects.observeAsState(emptyList())
    Log.d(TAG, "User Objects: $userObjects") // Log user objects

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Vaše rute") },
        text = {
            // Koristimo Column sa verticalScroll
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (userObjects.isEmpty()) {
                    Text(text = "Nemate kreirane objekte.")
                } else {
                    userObjects.forEach { obj ->
                        ObjectItem(obj)
                        Spacer(modifier = Modifier.height(8.dp)) // Razmak između objekata
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Zatvori")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
fun ObjectItem(obj: ObjectUIState) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(obj.timestamp))

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Text(text = "Location: ${obj.locationName}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Details: ${obj.details ?: "No details available"}", color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Points: ${obj.points}", color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Timestamp: $formattedDate", color = Color.DarkGray)
    }
}

