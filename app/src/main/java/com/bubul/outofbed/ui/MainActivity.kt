package com.bubul.outofbed.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.bubul.outofbed.core.Constants
import com.bubul.outofbed.core.service.LocalServiceBinder
import com.bubul.outofbed.core.service.MainService
import com.bubul.outofbed.ui.composables.AlarmListScreen
import com.bubul.outofbed.ui.composables.LoadingScreen
import com.bubul.outofbed.ui.theme.OutOfBedTheme
import com.bubul.outofbed.ui.viewmodels.ActivityViewModel
import com.bubul.outofbed.ui.viewmodels.AlarmListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {

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
    private var service: MainService? = null

    private val activityViewModel: ActivityViewModel by viewModels()
    private val alarmListViewModel: AlarmListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OutOfBedTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val screenState by activityViewModel.screen.collectAsState()
                    when (screenState) {
                        ActivityViewModel.ActivityScreen.LOADING -> LoadingScreen()
                        ActivityViewModel.ActivityScreen.ALARM_LIST -> AlarmListScreen(
                            alarmListViewModel,
                            service
                        )

                        ActivityViewModel.ActivityScreen.CREATE_ALARM -> LoadingScreen()
                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            startForegroundServiceIfNecessary()
        }
        activityViewModel.registerBus()
    }

    override fun onStop() {
        unbindService(serviceConnection)
        activityViewModel.unregisterBus()
        super.onStop()
    }

    private fun startForegroundServiceIfNecessary() {
        if (!bindToForegroundService()) {
            Timber.i("Service not started, ask it to start")
            sendBroadcast(Intent().also {
                it.action = Constants.INTENT_START_SERVICE
                it.`package` = "com.bubul.outofbed"
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                it.addCategory(Intent.CATEGORY_DEFAULT)
            })
            Thread.sleep(1000)
            if (!bindToForegroundService())
                Timber.e("Enable to bind to service")
            else
                activityViewModel.loadAlarms(service)
            alarmListViewModel.loadAlarms(service)
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
