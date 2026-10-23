package com.example.websocketserver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.concurrent.CopyOnWriteArraySet

class WebSocketService : Service() {

    private var server: SimpleWebSocketServer? = null
    private val CHANNEL_ID = "websocket_server_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        startWebSocketServer()
    }

    private fun startWebSocketServer() {
        server = SimpleWebSocketServer(InetSocketAddress("0.0.0.0", 8765))
        server?.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        server?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WebSocket Server",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WebSocket szerver fut")
            .setContentText("Port: 8765")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    // Belső WebSocket szerver osztály
    inner class SimpleWebSocketServer(address: InetSocketAddress) : WebSocketServer(address) {
        private val clients = CopyOnWriteArraySet<WebSocket>()

        override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
            clients.add(conn)
            conn.send("Szerver csatlakozott")
        }

        override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
            clients.remove(conn)
        }

        override fun onMessage(conn: WebSocket, message: String) {
            // Broadcast minden kliensnek
            val entry = "[${System.currentTimeMillis()}] $message"
            clients.forEach { client ->
                if (client != conn && client.isOpen) {
                    client.send(entry)
                }
            }
            // Opcionálisan küldjük vissza a küldőnek is
            conn.send("Fogadva: $message")
        }

        override fun onError(conn: WebSocket?, ex: Exception?) {
            ex?.printStackTrace()
        }

        override fun onStart() {
            // Szerver elindult
        }
    }
}
