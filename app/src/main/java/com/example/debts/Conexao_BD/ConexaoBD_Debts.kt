package com.example.debts.Conexao_BD

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager

class conexaoBD_Debts {
    lateinit var conexaDB: Connection
    lateinit var nomeUsuario: String
    lateinit var senhaUsuario: String
    lateinit var ip: String
    lateinit var port: String
    lateinit var  database: String
    //lateinit var url: String

    @SuppressLint("NewApi")
    public fun connectionClass(): Connection? {
        database = "DEBTS"
        ip = "192.168.0.20"
        port = "1433"
        nomeUsuario = "DESKTOP-NK3AAJR\\Leo"
        senhaUsuario = ""

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var connection: Connection? = null
        var connectionURL: String = ""

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connectionURL =
                "jdbc:jtds:sqlserver://$ip:$port;databasename=$database;user=$nomeUsuario;integratedSecurity=true";
            connection = DriverManager.getConnection(connectionURL)

            //user=$nomeUsuario;password=$senhaUsuario;
        }
        catch (ex: Exception) {
            Log.e("Error ", "${ex.message}")
        }

        return connection
    }
}