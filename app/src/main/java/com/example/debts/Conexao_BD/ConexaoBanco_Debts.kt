package com.example.debts.Conexao_BD

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.util.Objects


class conexaoBanco_Debts() {
    @SuppressLint("NewApi")
    public fun conectar(): Connection? {
        var conn: Connection? = null

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var ip: String = "192.168.0.20:1433"
        var db: String = "AdventureWorks2017"

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            var connectionURL: String = "jdbc:jtds:sqlserver://"+ ip +";databasename="+ db +";" + "integratedSecurity=true"
            conn = DriverManager.getConnection(connectionURL)
        }

        catch (e: Exception){
            Log.e("Erro Conexão ao Banco", "${e.message}")
            Log.e("Conexão Falha", "${Objects.requireNonNull(e.message)}")
        }

        return conn
    }

    // Função suspensa para conectar ao banco de dados
//    suspend fun conectar(): Connection? = withContext(Dispatchers.IO) {
//        var conn: Connection? = null
//
//        //val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        //StrictMode.setThreadPolicy(policy)
//
//        //Toast.makeText(context, "Iniciando Conexão ao Banco", Toast.LENGTH_SHORT).show()
//
//        try {
//            // Configuração da URL de conexão
//            val ip: String = "192.168.0.20:1433"
//            val db: String = "AdventureWorks2017"
//            val connectionURL = "jdbc:jtds:sqlserver://$ip;databasename=$db;integratedSecurity=true"
//
//            // Carregar o driver JDBC
//            Class.forName("net.sourceforge.jtds.jdbc.Driver")
//            conn = DriverManager.getConnection(connectionURL)
//
//        } catch (e: Exception) {
//            // Substitua Log.e por println ou outro mecanismo de log conforme o ambiente
//            Log.e("Conexão", "Erro Conexão ao Banco: ${e.message}")
//            //println("Conexão Falha: ${e.message}")
//        }
//
//        return@withContext conn
//    }
}