@file:OptIn(ExperimentalMaterial3Api::class)

package com.bubul.outofbed.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bubul.outofbed.core.service.MainService
import com.bubul.outofbed.ui.viewmodels.AlarmListViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlarmListScreen(alarmListViewModel: AlarmListViewModel, service: MainService?) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Wake up planning")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                alarmListViewModel.switchToAlarmCreation()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Blue)
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        AlarmList(alarmListViewModel, service, innerPadding)
    }
}

@Composable
fun AlarmList(
    alarmListViewModel: AlarmListViewModel,
    service: MainService?,
    innerPadding: PaddingValues
) {
    val alarms by alarmListViewModel.alarms.collectAsState()
    if (alarms.isEmpty()) {
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "No alarm created yet...", color = Color.White)
        }
    } else {
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            alarms.forEach {
                Text(text = "Alarm ${it.id}", color = Color.White)
            }
        }
    }
}