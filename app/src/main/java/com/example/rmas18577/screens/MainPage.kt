package com.example.rmas18577.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas18577.components.AppToolbar
import com.example.rmas18577.components.Heading
import com.example.rmas18577.components.NavigationDrawerBody
import com.example.rmas18577.components.NavigationDrawerHeader
import com.example.rmas18577.components.NormalTextComponent
import com.example.rmas18577.data.home.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(homeViewModel: HomeViewModel = viewModel(), mapClick: () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    homeViewModel.getUserData()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerHeader(homeViewModel.emailId.value)
            NavigationDrawerBody(
                navigationDrawerItems = homeViewModel.navigationItemsList,
                onNavigationItemClicked = { item ->
                    Log.d("Navigation", "Clicked on ${item.title} with ID: ${item.itemId}")
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    AppToolbar(
                        toolbarTitle = "Main Page",
                        logoutButtonClicked = {
                            homeViewModel.logout()
                        },
                        navigationIconClicked = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())

                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Main content goes here
                    }
                }
            }
        }
    )
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        NormalTextComponent(value = "Zvanicno dobrodosao na app")
        Spacer(modifier = Modifier.height(20.dp))
        Heading(value = "Vidi sve nove info")
        Spacer(modifier = Modifier.height(20.dp))
    }

    Button(
        onClick = {
            mapClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(listOf(Color.LightGray, Color.Yellow)),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Show Map",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }



}

@Preview
@Composable
fun MainPagePreview() {
    MainPage(
    homeViewModel = viewModel(), // Možete koristiti fake ViewModel ili dummy podataka za pregled
    mapClick = {} // Prazan lambda za prikaz, može se proširiti kasnije ako je potrebno
)
}
