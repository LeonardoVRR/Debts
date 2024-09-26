package com.example.debts.Config_Notificacoes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.debts.R
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class NotificationHelper(private val context: Context) {

    private val DEBTS_CHANNEL_ID: String = "debts_channel"

    fun criarCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val gerenciadorNotificacao: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (gerenciadorNotificacao.getNotificationChannel(DEBTS_CHANNEL_ID) == null) {
                val canal: NotificationChannel = NotificationChannel(DEBTS_CHANNEL_ID, "Alerta Gasto", NotificationManager.IMPORTANCE_DEFAULT)

                canal.setDescription("Este é um canal de notifição para alertas caso o valor total dos gastos supere o valor total dos rendimentos")
                canal.enableLights(true)
                canal.setLightColor(android.graphics.Color.rgb(255, 50, 50))
                canal.enableVibration(true)

                gerenciadorNotificacao.createNotificationChannel(canal)
            }
        }
    }

    fun enviarNotificacao(tituloNotificacao: String, msgNotificacao: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verifica se a permissão para postar notificações foi concedida
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Se a permissão não foi concedida, ela deve ser solicitada
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1234
                )
                return
            }
        }

        val construtor: NotificationCompat.Builder = NotificationCompat.Builder(context, DEBTS_CHANNEL_ID)
            .setContentTitle(tituloNotificacao)
            .setContentText(msgNotificacao)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val gerenciadorNotificacaoCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
        gerenciadorNotificacaoCompat.notify(1455, construtor.build())
    }
}