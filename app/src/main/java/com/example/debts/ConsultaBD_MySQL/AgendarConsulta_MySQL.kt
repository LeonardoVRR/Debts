package com.example.debts.ConsultaBD_MySQL

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class AgendarConsulta_MySQL(private val context: Context) {

    fun agendarAlarmeConsultaLista(nomeAlarme: String, requestCode: Int, intervalo: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Criando um Intent com o nome do alarme como extra para identificar
        val intent = Intent(context, BroadcastReceiver_ConsultarLista::class.java)
        intent.putExtra("nomeAlarme", nomeAlarme)

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Define o alarme para repetir com o intervalo fornecido
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + intervalo * 1000, // Primeiro disparo
            intervalo * 1000, // Repetição a cada X minutos
            pendingIntent
        )
    }

    fun cancelarAlarme() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BroadcastReceiver_ConsultarLista::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

}