package com.example.rmas18577.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rmas18577.navigation.SystemBackButtonHandler
import com.example.rmas18577.ui.theme.Pink80
import com.example.rmas18577.ui.theme.Purple80
import com.example.rmasprojekat18723.data.RegistrationUIState
import com.example.rmasprojekat18723.data.SignupUIEvent
import com.example.rmasprojekat18723.data.SignupViewModel
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen



@Composable
fun UsersInfo(signUpViewModel: SignupViewModel) {

    val users by signUpViewModel.usersState

    LaunchedEffect(Unit) {
        signUpViewModel.onEvent(SignupUIEvent.LoadUsers)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Leaderboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            for (index in users.indices) {
                UserCard(user = users[index], position = index + 1)
            }

            SystemBackButtonHandler {
                Navigator.navigateTo(Screen.MainPage)
            }

        }
    }
}

@Composable
fun UserCard(user: RegistrationUIState, position: Int) {



    val medalColor = when (position) {
        1 -> Color.Yellow
        2 -> Color.Gray
        3 -> Pink80
        else -> Color.Black
    }

    val size = if (position <= 3) 50.dp else 24.dp

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$position",
                    fontSize = size.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = medalColor,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Image(
                    painter = rememberAsyncImagePainter(user.imageUri),
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
                Log.d("UserCard", "User image URL: ${user.imageUri ?: "URL is null"}")


                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = user.username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple80,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Text(
                text = "${user.points}",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
