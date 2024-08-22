package com.example.debts

import android.util.Log
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class usuarioDAO {
//    public fun selecionarUsuario(usuario: String, senha: String): usuarioBD_Debts? {
//        try {
//            var conn: Connection? = conexaoBanco_Debts().conectar()
//            if (conn != null) {
//                val sql: String = "SELECT * FROM cadastroUsuarios WHERE nomeUsuario = '${usuario}' AND senhaUsuario = '${senha}'"
//                val query: String = "SELECT * FROM Person.Person"
//                var st: Statement? = null
//
//                st = conn.createStatement()
//
//                var rs: ResultSet = st.executeQuery(query)
//
//                while (rs.next()) {
//                    var usuario = usuarioBD_Debts()
//                    usuario.getUsuario(rs.getString(2))
//                    usuario.getSenha(rs.getString(3))
//
//                    conn.close()
//                    return usuario
//                }
//            }
//        }
//        catch (e: ClassNotFoundException) {
//            e.printStackTrace()
//            Log.e("Erro consulta BD", "${e.message}")
//        }
//        catch (throwables: SQLException) {
//            throwables.printStackTrace()
//            Log.e("Erro consulta BD", "${throwables.message}")
//        }
//
//        return null
//    }
}