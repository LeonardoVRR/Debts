package com.example.debts.BD_MySQL_App

import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.util.Objects

class ConnectionClass {
    private val dbName: String = "debts"
    private val ip: String = "192.168.0.22"
    private val port: String = "3306"
    private val user: String = "debts_app"
    private val password: String = "debts2024"

    fun CONN(): Connection? {
        var conn: Connection? = null

        try {

            Class.forName("com.mysql.jdbc.Driver")
            val connectionString: String = "jdbc:mysql://$ip:$port/$dbName"
            conn = DriverManager.getConnection(connectionString, user, password)

        } catch (e: Exception) {
            Log.e("Erro de conexão", "${e.message}")
        }

        return conn
    }
}