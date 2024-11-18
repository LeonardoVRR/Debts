package com.example.debts.API_Flask

import android.util.Log
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

class Obter_IP_Rede {

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in interfaces) {
                if (!networkInterface.isUp || networkInterface.isLoopback || networkInterface.isVirtual) continue

                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun logNetworkInterfaces() {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in interfaces) {
                Log.d("Interface:", networkInterface.name)
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    Log.d("IP:", address.hostAddress)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}