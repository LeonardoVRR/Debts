package com.example.debts.BD_MySQL_App

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.debts.CustomToast
import com.google.gson.Gson
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class Metodos_BD_MySQL {

    //função para verificar se existe uma conta para fazer o login do usuario
    fun validarLogin(nome: String, senha: String): Boolean {
        // Inicialize a conexão com BD
        val con = ConnectionClass().CONN()

        var loginValido: Boolean = false

        // Verifica se a conexão foi estabelecida
        if (con != null) {
            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            val sql = " SELECT * FROM usuarios_debts WHERE nome_usuario = '$nome' AND senha_usuario = '$senha'"

            try {
                // Execute a consulta e obtenha o resultado
                val resultSet: ResultSet = statement.executeQuery(sql)

                // Processar o resultado
                if (resultSet.next()) {
                    // Exemplo de processamento de dados
                    val id = resultSet.getInt("id_usuario") // Substitua pelo nome da coluna
                    val nome = resultSet.getString("nome_usuario") // Substitua pelo nome da coluna
                    Log.d("ConsultaResult", "ID: $id, Nome: $nome")

                    loginValido = true
                }

                else {
                    loginValido = false
                }

                resultSet?.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro ao realizar a consulta: ${e.message}")
            } finally {
                // Feche os recursos
                try {
                    statement?.close()
                    con?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.e("ErroConexao", "Conexão não estabelecida")
        }

        return loginValido
    }

    fun salvarQuestionario(nvl_conhecimeto_financ: Int, tps_investimentos: List<String>, tx_uso_ecommerce: Int, tx_uso_app_transporte: Int, tx_uso_app_entrega: Int, IDusuario: Int): String {
        // Inicialize a conexão com BD
        val con = ConnectionClass().CONN()

        //convertendo a lista para JSON para poder salvar no banco de dados
        val listaTps_investimentosJSON = Gson().toJson(tps_investimentos)

        //resultado da operação
        var resultado = ""

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            val sql = "SELECT * FROM questionario_usuario WHERE id_user_quest = $IDusuario"

            val update = "update questionario_usuario set nvl_conhecimeto_financ = ?, tps_investimentos = ?, tx_uso_ecommerce = ?, tx_uso_app_transporte = ?, tx_uso_app_entrega = ? where id_user_quest = ?"

            try {

                // Execute a consulta e obtenha o resultado
                val resultSet: ResultSet = statement.executeQuery(sql)

                // Verifica se já existe um questionario salvo se existir ele só atualiza as informações
                if (resultSet.next()) {

                    // Preparar a instrução SQL
                    val atualizarQuest = con.prepareStatement(update)

                    // Definir os parâmetros da consulta
                    atualizarQuest.setInt(1, nvl_conhecimeto_financ)
                    atualizarQuest.setString(2, listaTps_investimentosJSON)
                    atualizarQuest.setInt(3, tx_uso_ecommerce)
                    atualizarQuest.setInt(4, tx_uso_app_transporte)
                    atualizarQuest.setInt(5, tx_uso_app_entrega)
                    atualizarQuest.setInt(6, IDusuario)

                    // Executar a instrução de update
                    val rowsUpdated = atualizarQuest.executeUpdate()

                    // Verificar se alguma linha foi atualizada
                    if (rowsUpdated > 0) {
                        resultado = "Informações atualizadas com sucesso!"
                    }

                    atualizarQuest.close()
                }

                else {
                    // query para salvar uma nova meta do usuário
                    val insert = "INSERT INTO questionario_usuario (nvl_conhecimeto_financ, tps_investimentos, tx_uso_ecommerce, tx_uso_app_transporte, tx_uso_app_entrega, id_user_quest) VALUES (?, ?, ?, ?, ?, ?);"

                    // Preparar a instrução SQL
                    val inserirQuest = con.prepareStatement(insert)

                    // Definir os parâmetros da consulta
                    inserirQuest.setInt(1, nvl_conhecimeto_financ)
                    inserirQuest.setString(2, listaTps_investimentosJSON)
                    inserirQuest.setInt(3, tx_uso_ecommerce)
                    inserirQuest.setInt(4, tx_uso_app_transporte)
                    inserirQuest.setInt(5, tx_uso_app_entrega)
                    inserirQuest.setInt(6, IDusuario)

                    // Executa a consulta
                    inserirQuest.executeUpdate()

                    resultado = "Informações salvas com sucesso!"

                    inserirQuest.close()
                }

                resultSet.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro ao realizar a consulta: ${e.message}")

                resultado = "Erro ao realizar a consulta: ${e.message}"
            } finally {
                // Feche os recursos
                try {
                    statement?.close()
                    con?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }

        } else {
            Log.e("ErroConexao", "Conexão não estabelecida")

            resultado = "Conexão não estabelecida"
        }

        return resultado

    }

}