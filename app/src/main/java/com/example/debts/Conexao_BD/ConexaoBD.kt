package com.example.debts.Conexao_BD

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class ConexaoBD {

    //@SuppressLint("NewApi")
    fun connectToDatabase(): Connection? {
        var connection: Connection? = null
        //var connectionURL: String = ""

        val url = "jdbc:jtds:sqlserver://192.168.0.13:1433/DEBTS;integratedSecurity=true"
        val user = "DESKTOP-NK3AAJR\\Leo"  // Substitua com seu nome de usuário SQL Server
        val password = ""  // Substitua com sua senha SQL Server

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            // Carregar o driver JDBC
            Class.forName("net.sourceforge.jtds.jdbc.Driver")

            connection = DriverManager.getConnection(url)


            //connection: Connection = DriverManager.getConnection(url, user, password)
//            val statement: Statement = connection.createStatement()
//            val resultSet: ResultSet = statement.executeQuery("SELECT * FROM Metas")
//
//            while (resultSet.next()) {
//                val id = resultSet.getLong("id")
//                val nomeMeta = resultSet.getString("nomeMeta")
//                // Processar os resultados
//                println("ID: $id, Nome Meta: $nomeMeta")
//            }
//
//            resultSet.close()
//            statement.close()
            connection.close()
        } catch (e: SQLException) {
            Log.e("Erro Conexão Banco:", "SQLException: ${e.message}")
        } catch (e: ClassNotFoundException) {
            Log.e("Erro Conexão Banco:", "ClassNotFoundException: ${e.message}")
        } catch (e: Exception) {
            Log.e("Erro Conexão Banco:", "Exception: ${e.message}")
        }

        return connection
    }

}