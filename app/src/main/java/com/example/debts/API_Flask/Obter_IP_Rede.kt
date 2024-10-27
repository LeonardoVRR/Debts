package com.example.debts.API_Flask

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.NetworkInterface

suspend fun getLocalIPv4Address(): String? {
    return withContext(Dispatchers.IO) {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    // Verifica se é um endereço IPv4 e não é loopback
                    if (!inetAddress.isLoopbackAddress && inetAddress is InetAddress && inetAddress.hostAddress.indexOf(":") == -1) {
                        return@withContext inetAddress.hostAddress // Retorna o primeiro IPv4 encontrado
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }
}


class Obter_IP_Rede {
    fun fetchLocalIPAddress() {
        CoroutineScope(Dispatchers.Main).launch {
            val ipAddress = getLocalIPv4Address()
            if (ipAddress != null) {
                Log.d("LocalIPAddress", "Endereço IP local: $ipAddress")

                IP_Server_Flask.ip_number = ipAddress

                Log.d("LocalIPAddress", "Endereço IP Flask local: ${IP_Server_Flask.ip_number}")
            } else {
                Log.d("LocalIPAddress", "Não foi possível obter o endereço IP local.")
            }
        }
    }
}