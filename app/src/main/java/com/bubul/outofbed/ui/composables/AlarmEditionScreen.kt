package com.bubul.outofbed.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubul.outofbed.core.data.alarm.Days
import com.bubul.outofbed.ui.viewmodels.AlarmEditionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlarmEditionScreen(alarmEditionViewModel: AlarmEditionViewModel) {
    val timeState by alarmEditionViewModel.timeState.collectAsState()
    val name by alarmEditionViewModel.nameState.collectAsState()
    val canSave by alarmEditionViewModel.canSave.collectAsState()
    val repeats by alarmEditionViewModel.repeatsState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "New wake up alarm",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 45.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row {
            Spacer(modifier = Modifier.height(15.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Name",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 30.sp
                        )
                        TextField(value = name, onValueChange = { value ->
                            alarmEditionViewModel.updateName(value)
                        })
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TimePicker(state = timeState, layoutType = TimePickerLayoutType.Vertical)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Repeats on",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 30.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { alarmEditionViewModel.toggleRepeats(Days.Monday) },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (repeats.contains(Days.Monday)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "M", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { alarmEditionViewModel.toggleRepeats(Days.Tuesday) },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (repeats.contains(Days.Tuesday)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "T", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { alarmEditionViewModel.toggleRepeats(Days.Wednesday) },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (repeats.contains(Days.Wednesday)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "W", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { alarmEditionViewModel.toggleRepeats(Days.Thursday) },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (repeats.contains(Days.Thursday)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "T", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { alarmEditionViewModel.toggleRepeats(Days.Friday) },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (repeats.contains(Days.Friday)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "F", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { alarmEditionViewModel.toggleRepeats(Days.Saturday) },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (repeats.contains(Days.Saturday)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "S", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { alarmEditionViewModel.toggleRepeats(Days.Sunday) },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (repeats.contains(Days.Sunday)) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "S", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { alarmEditionViewModel.onSave() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxHeight(),
                enabled = canSave
            ) {
                Text(text = "Save", fontSize = 20.sp)
            }
            Button(
                onClick = { alarmEditionViewModel.onCancel() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(text = "Cancel", fontSize = 20.sp)
            }
        }
    }
}