package com.example.sirincontroller

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        statusText = TextView(this).apply {
            textSize = 16f
            setPadding(0, 0, 0, 32)
        }

        val permissionButton = Button(this).apply {
            text = "Abilita Accesso alle Notifiche"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }

        layout.addView(statusText)
        layout.addView(permissionButton)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        val enabled = isNotificationServiceEnabled()
        statusText.text = if (enabled) {
            "Stato: ATTIVO.\nIl ponte per OnePlus Watch 3 e pronto a controllare Sirin."
        } else {
            "Stato: PERMESSO MANCANTE.\nTocca il pulsante qui sotto e attiva 'Sirin Phone Bridge' per permettere il controllo di Sirin (+15s / -15s)."
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val cn = ComponentName(this, MediaNotificationListener::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(cn.flattenToString())
    }
}
