package com.bubul.outofbed.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.bubul.outofbed.core.Constants
import com.bubul.outofbed.core.service.IAlarmService
import com.bubul.outofbed.core.service.LocalServiceBinder
import com.bubul.outofbed.core.service.MainService
import com.bubul.outofbed.ui.composables.AlarmEditionScreen
import com.bubul.outofbed.ui.composables.AlarmListScreen
import com.bubul.outofbed.ui.composables.LoadingScreen
import com.bubul.outofbed.ui.theme.OutOfBedTheme
import com.bubul.outofbed.ui.viewmodels.ActivityViewModel
import com.bubul.outofbed.ui.viewmodels.AlarmEditionViewModel
import com.bubul.outofbed.ui.viewmodels.AlarmListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.system.exitProcess


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
    private var service: IAlarmService? = null

    private val activityViewModel: ActivityViewModel by viewModels()
    private val alarmListViewModel: AlarmListViewModel by viewModels()
    private val alarmEditionViewModel: AlarmEditionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startup()
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
                            this
                        )

                        ActivityViewModel.ActivityScreen.CREATE_ALARM, ActivityViewModel.ActivityScreen.MODIFY_ALARM -> AlarmEditionScreen(
                            alarmEditionViewModel
                        )
                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            activityViewModel.registerBus()
            alarmListViewModel.registerBus()
            alarmEditionViewModel.registerBus()
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        try {
            unbindService(serviceConnection)
        } catch (e: Exception) {
            Timber.e("Service was not connected, strange...")
        }
        activityViewModel.unregisterBus()
        alarmListViewModel.unregisterBus()
        alarmEditionViewModel.unregisterBus()
        super.onStop()
    }

    private fun startup() {
        activityViewModel.registerBus()
        alarmListViewModel.registerBus()
        alarmEditionViewModel.registerBus()
        checkNotificationPermission()
        checkOverlayPermission()
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
            if (!bindToForegroundService()) {
                Timber.e("Enable to bind to service")
                finish()
            }
        }
        activityViewModel.linkService(service)
        alarmEditionViewModel.linkService(service)
        alarmListViewModel.linkService(service)
        service?.loadAlarms()
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

    private fun checkNotificationPermission() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.contains(false)) {
                Timber.e("Notification permission is missing")
                exitProcess(1)
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    startForegroundServiceIfNecessary()
                }
            }
        }.launch(listOf("android.permission.POST_NOTIFICATIONS").toTypedArray())
    }

    private fun checkOverlayPermission() {
        //TODO: make a screen to tell user he need to accept those dawn permissions
        if (!Settings.canDrawOverlays(this)) {
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(myIntent)
        }
    }
}
