package com.example.rmas18577.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun MainPage(homeViewModel: HomeViewModel = viewModel()) {
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


}

@Preview
@Composable
fun MainPagePreview() {
    MainPage()
}
