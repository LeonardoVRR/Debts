package com.example.debts.ConsultaBD_MySQL

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

class AgendarConsulta_MySQL(private val context: Context) {

    fun agendarAlarmeConsultaLista(nomeAlarme: String, requestCode: Int, intervalo: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BroadcastReceiver_ConsultarLista::class.java)
        intent.putExtra("nomeAlarme", nomeAlarme)
        intent.action = "com.example.debts.ACTION_CONSULTAR_LISTA"

        Log.d("AgendarConsulta_MySQL", "Criando Intent com nome: $nomeAlarme, action: ${intent.action}")

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("AgendarConsulta_MySQL", "Agendando alarme: $nomeAlarme, requestCode: $requestCode, intervalo: $intervalo")

        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + intervalo * 1000,
            intervalo * 1000,
            pendingIntent
        )
    }

    fun cancelarAlarme(nomeAlarme: String, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Criando um Intent com o nome do alarme como extra para identificar
        val intent = Intent(context, BroadcastReceiver_ConsultarLista::class.java)
        intent.putExtra("nomeAlarme", nomeAlarme)

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun alarmeAtivo(nomeAlarme: String, requestCode: Int): Boolean {
        val intent = Intent(context, BroadcastReceiver_ConsultarLista::class.java)
        intent.putExtra("nomeAlarme", nomeAlarme)

        // Criar o PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        // Se o PendingIntent for nulo, o alarme não está ativo
        return pendingIntent != null
    }

    // Método para verificar e logar o estado do PendingIntent
    fun verificarPendingIntent(nomeAlarme: String, requestCode: Int) {
        val intent = Intent(context, BroadcastReceiver_ConsultarLista::class.java)
        intent.putExtra("nomeAlarme", nomeAlarme)

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent == null) {
            Log.d("Verificação PendingIntent", "PendingIntent não foi criado ou não existe.")
        } else {
            Log.d("Verificação PendingIntent", "PendingIntent foi criado corretamente.")
            Log.d("PendingIntent Detalhes", "Ação: ${intent.action}, Código de Solicitação: $requestCode")
        }
    }

}