@file:OptIn(ExperimentalMaterial3Api::class)

package com.bubul.outofbed.ui.composables

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubul.outofbed.R
import com.bubul.outofbed.core.data.alarm.Days
import com.bubul.outofbed.ui.viewmodels.AlarmListViewModel
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlarmListScreen(alarmListViewModel: AlarmListViewModel, context: Context) {
    val theme = context.theme
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
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
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        AlarmList(alarmListViewModel, innerPadding)
    }
}

@Composable
fun AlarmList(
    alarmListViewModel: AlarmListViewModel,
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
            Text(text = "No alarm created yet...", color = MaterialTheme.colorScheme.onBackground)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            alarms.forEach {
                item {
                    AlarmItemView(it, alarmListViewModel)
                }
            }
        }
    }
}

@Composable
fun AlarmItemView(id: UUID, alarmListViewModel: AlarmListViewModel) {
    val wakeUpItem by alarmListViewModel.getAlarm(id)!!.collectAsState()
    val openDeleteConfirmationDialog = remember { mutableStateOf(false) }
    val nextAlarm = wakeUpItem.nextAlarm()
    Column(
        modifier = Modifier.clickable {
            alarmListViewModel.switchToAlarmModification(wakeUpItem.id)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = if (nextAlarm != null) R.drawable.ic_alarm else R.drawable.ic_alarm_never),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        text = wakeUpItem.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = wakeUpItem.active,
                        onCheckedChange = { alarmListViewModel.activateAlarm(wakeUpItem.id, it) })
                    Spacer(modifier = Modifier.size(20.dp))
                    Image(painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                openDeleteConfirmationDialog.value = true
                            })
                }

            }

        }
        Row {
            if (wakeUpItem.repeats.on.isEmpty()) {
                Text(text = "One-shot", fontSize = 20.sp)
            } else {
                for (ent in Days.entries) {
                    Button(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = if (wakeUpItem.repeats.on.contains(ent)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = ent.name.uppercase()[0].toString(), fontSize = 5.sp)
                    }
                }
            }
        }
        Row {
            val next = nextAlarm?.toString() ?: "Never"
            Text(text = "Next: $next")
        }
    }
    if (openDeleteConfirmationDialog.value) {
        AlertDialog(title = { Text("Are you sure ?") },
            text = { Text("Are you sure you want to delete ${wakeUpItem.name} ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        alarmListViewModel.deleteAlarm(id)
                        openDeleteConfirmationDialog.value = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDeleteConfirmationDialog.value = false
                    }
                ) {
                    Text("Abort")
                }
            },
            onDismissRequest = { openDeleteConfirmationDialog.value = false })
    }
}