package com.example.servise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.servise.ui.theme.ServiseTheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private val notificationDelayMillis = 50000L
    private lateinit var notificationJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServiseTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    ButtonColumn()
                }
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))
    }

    @Composable
    fun ButtonColumn() {
        val context = LocalContext.current
        val countState: MutableState<Int> = remember { mutableStateOf(1) }
        val countState1: MutableState<String> = remember { mutableStateOf("cat") }

        Column {
            Text(
                text = countState.value.toString(),
                fontSize = MaterialTheme.typography.h4.fontSize,
                textAlign = TextAlign.Center
            )

            Text(
                text = countState1.value,
                fontSize = MaterialTheme.typography.h4.fontSize,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        while (true) {
                            delay(1000)
                            countState.value++
                            countState1.value = "dog"
                        }
                    }
                }
            ) {
                Text("Opening")
            }

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(notificationDelayMillis)
                        showNotification(context, "HOLA , este es el de 30 segundos")
                    }
                }
            ) {
                Text("Notification")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationJob.cancel()
    }

    inner class AppLifecycleObserver(private val context: Context) : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onAppForegrounded() {
            notificationJob = CoroutineScope(Dispatchers.IO).launch {
                delay(notificationDelayMillis)
                showNotification(context, "Hello ")
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onAppBackgrounded() {
            notificationJob.cancel()
        }
    }

    private fun showNotification(context: Context, message: String) {
        val channelId = "my_channel_id"
        createNotificationChannel(context, channelId)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ide)
            .setContentTitle("My App")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "HOLA"
            val descriptionText = "notificacion automatica"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ServiseTheme {
        MainActivity().ButtonColumn()
    }
}
