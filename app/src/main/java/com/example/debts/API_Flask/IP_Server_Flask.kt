package com.example.debts.API_Flask

object IP_Server_Flask {
    val ip_number = Obter_IP_Rede().getLocalIpAddress().toString()
}