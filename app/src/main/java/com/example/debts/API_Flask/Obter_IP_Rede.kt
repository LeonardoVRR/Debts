package com.example.debts.API_Flask

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

class Obter_IP_Rede {

    // Função para obter o endereço IP local de forma robusta
    fun fetchLocalIPAddress() {
        // Lançando a coroutine em uma thread de IO, pois obter o IP pode ser uma operação de rede ou E/S
        CoroutineScope(Dispatchers.IO).launch {
            // Tenta obter o IP local
            val result = runCatching { getLocalIpAddress() }

            // Processa o resultado
            withContext(Dispatchers.Main) {
                result.onSuccess { ipAddress ->
                    // Se o IP foi obtido com sucesso
                    if (ipAddress != null) {
                        Log.d("LocalIPAddress", "Endereço IP local: $ipAddress")

                        // Atualiza o IP no servidor Flask
                        IP_Server_Flask.ip_number = ipAddress
                        Log.d("LocalIPAddress", "Endereço IP Flask local: ${IP_Server_Flask.ip_number}")
                    } else {
                        // Caso o IP seja nulo
                        Log.d("LocalIPAddress", "Não foi possível obter o endereço IP local.")
                    }
                }.onFailure { exception ->
                    // Caso ocorra um erro
                    Log.e("LocalIPAddress", "Erro ao tentar obter o endereço IP: ${exception.message}")
                }
            }
        }
    }

    // Função para obter o endereço IP local, retornando somente IPv4 ou null
    private fun getLocalIpAddress(): String? {
        return try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val intf = networkInterfaces.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()

                    // Verifica se é um IPv4
                    if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress // Retorna o IPv4
                    }
                }
            }
            null // Retorna null se não encontrar um IPv4
        } catch (ex: Exception) {
            Log.e("IP Address", "Erro ao obter o endereço IP: ${ex.message}")
            null // Retorna null em caso de erro
        }
    }
}