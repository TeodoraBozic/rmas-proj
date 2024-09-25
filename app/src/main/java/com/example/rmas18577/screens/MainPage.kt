import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.components.Heading
import com.example.rmas18577.components.NormalTextComponent
import com.example.rmas18577.data.home.HomeViewModel

import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmas18577.ui.theme.Pink80
import com.example.rmas18577.ui.theme.Purple80
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.material3.Button
import androidx.compose.material3.Text


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(homeViewModel: HomeViewModel = viewModel(), mapClick: () -> Unit) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser


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
                Heading(value = "Hajde da trcimo zajedno!")
                Spacer(modifier = Modifier.height(20.dp))
                NormalTextComponent(value = "Vidi dostupne rute ili dodaj svoju!")
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { mapClick() },
                    modifier = Modifier.fillMaxWidth().heightIn(48.dp),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().heightIn(48.dp).fillMaxWidth()
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
                    onClick = { Navigator.navigateTo(Screen.ViewObjects)  },
                    modifier = Modifier.fillMaxWidth().heightIn(48.dp)
                ) {
                    Text(text = "Vidi sve objekte", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { Navigator.navigateTo(Screen.UsersInfo)  },
                    modifier = Modifier.fillMaxWidth().heightIn(48.dp)
                ) {
                    Text(text = "Korisnici", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }



            }
        }

        SystemBackButtonHandler {
            Navigator.navigateTo(Screen.HomePage)
        }
    }
}
