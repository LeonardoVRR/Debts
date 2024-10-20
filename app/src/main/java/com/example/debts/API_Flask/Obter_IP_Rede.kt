package com.example.debts.API_Flask

import android.util.Log
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class Obter_IP_Rede {

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            Log.d("IP_Rede", "Obtendo interfaces de rede: $interfaces")

            for (networkInterface in Collections.list(interfaces)) {
                // Verifica se a interface está ativa e não é loopback
                if (networkInterface.isUp && !networkInterface.isLoopback) {
                    Log.d("IP_Rede", "Interface ativa: ${networkInterface.name}")

                    val addresses = networkInterface.inetAddresses
                    for (address in Collections.list(addresses)) {
                        if (!address.isLoopbackAddress && address is InetAddress) {
                            val ipAddress = address.hostAddress
                            Log.d("IP_Rede", "Endereço encontrado: $ipAddress")

                            if (ipAddress.indexOf(':') < 0) { // Ignora endereços IPv6
                                Log.d("IP_Rede", "Endereço IPv4 válido: $ipAddress")

                                val ipAddressEditado = ipAddress.split(".")
                                // Aqui é feito o ajuste do IP
                                val ipAddressReal = "${ipAddressEditado[0]}.${ipAddressEditado[1]}.${ipAddressEditado[2]}.${ipAddressEditado[3].trim().toInt() - 4}"

                                Log.d("IP_Rede", "Endereço IP editado: $ipAddressReal")

                                return ipAddressReal
                            } else {
                                Log.d("IP_Rede", "Endereço IPv6 ignorado: $ipAddress")
                            }
                        } else {
                            Log.d("IP_Rede", "Endereço é loopback ou não é InetAddress: $address")
                        }
                    }
                } else {
                    Log.d("IP_Rede", "Interface não está ativa ou é loopback: ${networkInterface.name}")
                }
            }
        } catch (e: Exception) {
            Log.e("IP_Rede", "Erro ao obter o endereço IP: ${e.message}", e)
        }
        Log.d("IP_Rede", "Nenhum endereço IP válido encontrado.")
        return null
    }
}
