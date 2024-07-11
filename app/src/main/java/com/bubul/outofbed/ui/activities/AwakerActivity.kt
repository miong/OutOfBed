package com.bubul.outofbed.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bubul.outofbed.core.service.IAlarmService
import com.bubul.outofbed.core.service.LocalServiceBinder
import com.bubul.outofbed.core.service.MainService
import com.bubul.outofbed.ui.theme.OutOfBedTheme
import timber.log.Timber
import java.util.UUID

class AwakerActivity : ComponentActivity() {

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
            serviceBinder?.let {
                Timber.d("Service bounded")
                service = (serviceBinder as LocalServiceBinder).getService()

            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Service unbounded")
            service = null
        }

    }
    private var service: IAlarmService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindToForegroundService()
        setContent {
            OutOfBedTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxHeight(0.8f)
                        ) {
                            Button(onClick = {
                                service?.tmp_Awake(UUID.fromString(intent.getStringExtra("wakeupitem_id")))
                                unbindService(serviceConnection)
                                this@AwakerActivity.finish()
                            }) {
                                Text(text = "Stop it now")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bindToForegroundService(): Boolean {
        bindService(
            Intent(this, MainService::class.java),
            serviceConnection,
            0
        )
        Thread.sleep(500)
        return service != null
    }
}