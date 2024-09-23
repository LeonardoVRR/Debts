package com.example.debts.BD_MySQL_App

import android.content.Context
import android.util.Log
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosMetasFinanceiras_Usuario_BD_Debts
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.ManipularData.ManipularData
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.text.NumberFormat
//import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import java.sql.Timestamp
//import java.time.format.DateTimeFormatter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Metodos_BD_MySQL {

    object dadosUsuario {
        var listaDados: MutableList<String> = mutableListOf()
    }

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
            val sql = "SELECT * FROM usuarios_debts WHERE nome_usuario = '$nome' AND senha_usuario = '$senha'"

            try {
                // Execute a consulta e obtenha o resultado
                val resultSet: ResultSet = statement.executeQuery(sql)

                // Processar o resultado
                if (resultSet.next()) {
                    // Obtendo os resultados da consulta
                    val idUsuario = resultSet.getInt("id_usuario")
                    val nome = resultSet.getString("nome_usuario")
                    val emailUsuario = resultSet.getString("email_usuario")
                    val cpfUsuario = resultSet.getString("cpf_usuario")
                    val senhaUsuario = resultSet.getString("senha_usuario")

                    Log.d("ConsultaResult", "ID: $idUsuario, Nome: $nome")

                    if (dadosUsuario.listaDados.size > 0) {
                        dadosUsuario.listaDados[0] = nome
                        dadosUsuario.listaDados[1] = emailUsuario
                        dadosUsuario.listaDados[2] = cpfUsuario
                        dadosUsuario.listaDados[3] = senhaUsuario
                        dadosUsuario.listaDados[4] = idUsuario.toString()
                    }

                    else {
                        // Armazena os dados do usuário em uma lista
                        dadosUsuario.listaDados.add(nome)
                        dadosUsuario.listaDados.add(emailUsuario)
                        dadosUsuario.listaDados.add(cpfUsuario)
                        dadosUsuario.listaDados.add(senhaUsuario)
                        dadosUsuario.listaDados.add(idUsuario.toString())

                        Log.d("LISTA DADOS", "${dadosUsuario.listaDados}")
                    }

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

    // função para criar uma nova conta de usuário
    fun cadastrarConta(nome: String, email: String, cpf: String, senha: String): Boolean {
        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        var contaExiste = false
        val nomeFormatado = nome.lowercase()
        val emailFormatado = email.lowercase()

        // Verifica se a conexão foi estabelecida
        if (con != null) {
            try {
                // Verificar se o email já existe
                val emailConsulta = "SELECT * FROM usuarios_debts WHERE email_usuario = ?"
                val verificarEmailStmt = con.prepareStatement(emailConsulta)
                verificarEmailStmt.setString(1, emailFormatado)
                val verificarEmail: ResultSet = verificarEmailStmt.executeQuery()

                // Verificar se o CPF já existe
                val cpfConsulta = "SELECT * FROM usuarios_debts WHERE cpf_usuario = ?"
                val verificarCPFStmt = con.prepareStatement(cpfConsulta)
                verificarCPFStmt.setString(1, cpf) // CPF como string
                val verificarCPF: ResultSet = verificarCPFStmt.executeQuery()

                // Verifica se já existe uma conta com o mesmo email ou CPF
                if (verificarEmail.next() || verificarCPF.next()) {
                    contaExiste = true
                } else {
                    // Caso não exista, cria uma nova conta
                    val insert = "INSERT INTO usuarios_debts (nome_usuario, email_usuario, cpf_usuario, senha_usuario) VALUES (?, ?, ?, ?)"
                    val cadastrarContaStmt = con.prepareStatement(insert)
                    cadastrarContaStmt.setString(1, nomeFormatado)
                    cadastrarContaStmt.setString(2, emailFormatado)
                    cadastrarContaStmt.setString(3, cpf)
                    cadastrarContaStmt.setString(4, senha)

                    // Executa a inserção
                    cadastrarContaStmt.executeUpdate()

                    cadastrarContaStmt.close()
                }

                verificarEmailStmt.close()
                verificarCPFStmt.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro ao realizar a consulta: ${e.message}")
            } finally {
                // Feche os recursos
                try {
                    con.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.e("ErroConexao", "Conexão não estabelecida")
        }

        return contaExiste
    }

    fun verificarQuestionario(IDusuario: Int): Boolean {
        // Inicialize a conexão com BD
        val con = ConnectionClass().CONN()

        var questionarioPreenchido: Boolean = false

        // Verifica se a conexão foi estabelecida
        if (con != null) {
            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            val sql = "SELECT * FROM questionario_usuario WHERE id_user_quest = $IDusuario"

            try {
                // Execute a consulta e obtenha o resultado
                val consultarQuestionario: ResultSet = statement.executeQuery(sql)

                // Processar o resultado
                if (consultarQuestionario.next()) {
                    questionarioPreenchido = true
                }

                else {
                    questionarioPreenchido = false
                }

                consultarQuestionario?.close()

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

        return questionarioPreenchido
    }

    //função para atualizar o nome ou email do usuario no BD
    fun atualizarDados(novoNome: String, novoEmail: String, IDusuario: Int): String {
        // Inicialize a conexão com BD
        val con = ConnectionClass().CONN()

        //resultado da operação
        var resultado = ""

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            val update = "update usuarios_debts set nome_usuario = ?, email_usuario = ? where id_usuario = ?"

            try {

                // Preparar a instrução SQL
                val atualizarDados = con.prepareStatement(update)

                // Definir os parâmetros da consulta
                atualizarDados.setString(1, novoNome)
                atualizarDados.setString(2, novoEmail)
                atualizarDados.setInt(3, IDusuario)

                // Executar a instrução de update
                val rowsUpdated = atualizarDados.executeUpdate()

                // Verificar se alguma linha foi atualizada
                if (rowsUpdated > 0) {
                    resultado = "Dados atualizados com sucesso!"
                }

                atualizarDados.close()

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

    //função para atualizar a senha do usuario no BD MySQL
    fun atualizarSenha(novaSenha: String, IDusuario: Int): String {
        // Inicialize a conexão com BD
        val con = ConnectionClass().CONN()

        //resultado da operação
        var resultado = ""

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            val update = "update usuarios_debts set senha_usuario = ? where id_usuario = ?"

            try {

                // Preparar a instrução SQL
                val atualizarSenha = con.prepareStatement(update)

                // Definir os parâmetros da consulta
                atualizarSenha.setString(1, novaSenha)
                atualizarSenha.setInt(2, IDusuario)

                // Executar a instrução de update
                val rowsUpdated = atualizarSenha.executeUpdate()

                // Verificar se alguma linha foi atualizada
                if (rowsUpdated > 0) {
                    resultado = "Senha atualizada com sucesso!"
                }

                atualizarSenha.close()

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

    fun salvarQuestionario(nvl_conhecimeto_financ: Int, tps_investimentos: List<String>, tx_uso_ecommerce: Int, tx_uso_app_transporte: Int, tx_uso_app_entrega: Int, IDusuario: Int): String {
        // Inicializa a conexão com BD
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

    fun deletarUsuario(IDusuario: Int) {
        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        // query para excluir um usuário
        val query = " DELETE FROM Usuarios_Debts WHERE id_usuario = ?"

        if (con !== null) {
            try {

                // Preparar a instrução SQL
                val deletarUsuario = con.prepareStatement(query)

                // Definir o parâmetro ID do usuário
                deletarUsuario.setInt(1, IDusuario)

                // Executa a consulta
                deletarUsuario.executeUpdate()


                deletarUsuario.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro ao realizar a consulta: ${e.message}")

            } finally {
                // Feche os recursos
                try {
                    con?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.e("ErroConexao", "Conexão não estabelecida")

        }
    }

    fun listarMetas(IDusuario: Int, context: Context): List<dados_listaMeta_DebtMap> {
        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        val listasItemsMetas = mutableListOf<dados_listaMeta_DebtMap>()

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            val sql = "SELECT * FROM metas_financeiras WHERE id_user_meta = $IDusuario"

            try {

                // Executa a consulta e obtém o resultado
                val consultarMetas: ResultSet = statement.executeQuery(sql)

                // Processa os resultados da consulta
                while (consultarMetas.next()) {
                    // Obtendo os resultados da consulta
                    val idMeta: String = consultarMetas.getString("id_meta")
                    val nomeMeta: String = consultarMetas.getString("nome_meta")
                    val dataMeta: String = consultarMetas.getString("dt_meta")

                    val listaMetasJSON = consultarMetas.getString("lista_metas")
                    val listaMetasConcluidasJSON = consultarMetas.getString("metas_concluidas")

                    // Convertendo os dados JSON em listas
                    val listaSTR_type = object : TypeToken<List<String>>() {}.type
                    val listaBool_type = object : TypeToken<List<Boolean>>() {}.type

                    val listaMetas: List<String> = Gson().fromJson(listaMetasJSON, listaSTR_type)
                    val listaMetasConcluidas: List<Boolean> = Gson().fromJson(listaMetasConcluidasJSON, listaBool_type)

                    val progressoMeta: Float = consultarMetas.getFloat("progresso_meta")
                    //val id_user_meta: Int = consultarMetas.getInt("id_user_meta")

                    //formatando o nome da meta
                    val nomeFormatado = FormatarNome().formatar(nomeMeta)

                    // Converte a lista recuperada
                    val listaConvertida = DadosMetasFinanceiras_Usuario_BD_Debts().converter_Lista_MetasFinanceiras(listaMetas, listaMetasConcluidas)

                    //faz o fatiamento da data
//                    val dia = dataMeta.split("-")[2].trim()
//                    val mes = dataMeta.split("-")[1].trim()
//                    val ano = dataMeta.split("-")[0].trim()
//
//                    // Obtém o nome do mês atual para exibição
//                    val calendar = Calendar.getInstance()
//                    calendar.set(Calendar.MONTH, mes.toInt() - 1)
//                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))
//
//                    val dataFormatada = "$dia de $nomeMes de $ano"

                    // Cria o item DebtMap
                    val itemDebtMap = DadosMetasFinanceiras_Usuario_BD_Debts().criarItemDebtMap(idMeta, nomeFormatado, progressoMeta, dataMeta, listaConvertida)

                    // Adiciona o item à lista de itens
                    listasItemsMetas += itemDebtMap
                }

                // Fecha o ResultSet
                consultarMetas.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro consulta MySQL: ${e.message}")

            } finally {
                // Feche os recursos
                try {
                    statement.close()
                    con.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }

        } else {
            Log.e("ErroConexao", "Conexão não estabelecida")

        }

        return listasItemsMetas.toList()
    }

    fun salvarMeta(IDusuario: Int, nome_meta: String, dt_meta: String, lista_metas: List<String>, metas_concluidas: List<Boolean>, progresso_meta: Float): String {
        // Inicialize a conexão com BD
        val con = ConnectionClass().CONN()

        //convertendo a lista para JSON para poder salvar no banco de dados
        val listaMetasJSON = Gson().toJson(lista_metas)
        val listaMetasConcluidasJSON = Gson().toJson(metas_concluidas)

        //resultado da operação
        var resultado = ""

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            val insert = "INSERT INTO metas_financeiras (nome_meta, dt_meta, lista_metas, metas_concluidas, progresso_meta, id_user_meta) VALUES (?, ?, ?, ?, ?, ?)"

            try {

                // Preparar a instrução SQL
                val salvarMeta = con.prepareStatement(insert)

                // Definir os parâmetros da consulta
                salvarMeta.setString(1, nome_meta)
                salvarMeta.setString(2, dt_meta)
                salvarMeta.setString(3, listaMetasJSON)
                salvarMeta.setString(4, listaMetasConcluidasJSON)
                salvarMeta.setFloat(5, progresso_meta)
                salvarMeta.setInt(6, IDusuario)

                // Executar a instrução de update
                val rowsInserted = salvarMeta.executeUpdate()

                // Verificar se a inserção foi bem-sucedida
                if (rowsInserted > 0) {
                    resultado = "Meta salva com sucesso!"
                } else {
                    resultado = "Falha ao salvar a meta."
                }

                salvarMeta.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta Salvar Meta", "Erro ao realizar a consulta: ${e.message}")

                resultado = "Erro ao salvar meta: ${e.message}"
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
            Log.e("ErroConexao Salvar Meta", "Conexão não estabelecida")

            resultado = "Conexão não estabelecida"
        }

        return resultado
    }

    fun atualizarMeta(IDusuario: Int, IdMeta: Int, metas_concluidas: List<Boolean>, progresso_meta: Float): String {
        var metaAtualizada = ""

        //convertendo a lista para JSON para poder salvar no banco de dados
        val listaMetasConcluidasJSON = Gson().toJson(metas_concluidas)

        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        val prog_formatado = String.format("%.1f", progresso_meta).replace(",", ".").toFloat()

        // query para excluir um usuário
        val query = "update metas_financeiras set metas_concluidas = ?, progresso_meta = ? where id_user_meta = ? and id_meta = ?"

        if (con != null) {
            try {

                Log.d("INICIANDO ATUALIZAÇÃO", "Atualizando...")

                // Preparar a instrução SQL
                val atualizarMeta = con.prepareStatement(query)

                // Definir o parâmetro ID do usuário
                atualizarMeta.setString(1, listaMetasConcluidasJSON)
                atualizarMeta.setFloat(2, prog_formatado)
                atualizarMeta.setInt(3, IDusuario)
                atualizarMeta.setInt(4, IdMeta)

                // Executa a consulta
                val rowsUpdate = atualizarMeta.executeUpdate()

                // Verificar se a inserção foi bem-sucedida
                if (rowsUpdate > 0) {
                    Log.d("ATUALIZAÇÃO", "Meta Atualizada")
                } else {
                    Log.d("ATUALIZAÇÃO", "Nada atualizado")
                }

                atualizarMeta.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta atualizar meta", "Erro ao atualizar meta: ${e.message}")

                metaAtualizada = "Erro ao atualizar meta: ${e.message}"
            } finally {
                // Feche os recursos
                try {
                    con?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.e("ErroConexao atualizar meta", "Conexão não estabelecida")

            metaAtualizada = "Conexão não estabelecida atualizar meta"
        }

        return metaAtualizada
    }

    fun deletarMeta(IDusuario: Int, IdMeta: Int): Boolean {
        var metaExcluida: Boolean = false

        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        // query para excluir um usuário
        val query = "DELETE FROM metas_financeiras WHERE id_meta = ? AND id_user_meta = ?"

        if (con !== null) {
            try {

                // Preparar a instrução SQL
                val deletarMeta = con.prepareStatement(query)

                // Definir o parâmetro ID do usuário
                deletarMeta.setInt(1, IdMeta)
                deletarMeta.setInt(2, IDusuario)

                // Executa a consulta
                deletarMeta.executeUpdate()

                deletarMeta.close()

                metaExcluida = true

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta deletar meta", "Erro ao deletar meta: ${e.message}")

                metaExcluida = false
            } finally {
                // Feche os recursos
                try {
                    con?.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.e("ErroConexao deletar meta", "Conexão não estabelecida")
        }

        return metaExcluida
    }

    fun salvarRendimento(tipoMovimento: String, dataRendimento: String, valorRendimento: Float, IDusuario: Int): String {
        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        //resultado da operação
        var resultado = ""

        // Verifica se a conexão foi estabelecida
        if (con != null){

            //formatando a data
            //faz o fatiamento da data
            val dia = dataRendimento.split("/")[0].trim()
            val mes = dataRendimento.split("/")[1].trim().toInt()
            val ano = dataRendimento.split("/")[2].trim()

            val nomeMes = ManipularData().pegarNomeMes(mes)

            // Obtém o nome do mês atual para exibição
//            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.MONTH, mesBalanco)
//            val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

            val mesFormatado = String.format("%02d", mes)

            val dataFormatada = "$ano-$mesFormatado-$dia"

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // query para salvar um novo rendimento do usuário
            val sql = "INSERT INTO rendimentos (tp_movimento, dt_rendimento, mes, valor_rendimento, id_user_rendimento) VALUES (?, ?, ?, ?, ?);"

            try {

                // Preparar a instrução SQL
                val salvarRendimento = con.prepareStatement(sql)

                // Definir os parâmetros da consulta
                salvarRendimento.setString(1, tipoMovimento)
                salvarRendimento.setString(2, dataFormatada)
                salvarRendimento.setString(3, nomeMes)
                salvarRendimento.setFloat(4, valorRendimento)
                salvarRendimento.setInt(5, IDusuario)

                // Executa a consulta
                salvarRendimento.executeUpdate()

                resultado = "Informações salvas com sucesso!"

                salvarRendimento.close()

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

    fun listaRendimentos(IDusuario: Int): List<OperacaoFinanceira> {
        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        val listaRendimentos = mutableListOf<OperacaoFinanceira>()

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            val sql = "SELECT * FROM rendimentos WHERE id_user_rendimento = $IDusuario ORDER BY dt_rendimento ASC"

            try {

                // Executa a consulta e obtém o resultado
                val consultarRendimentos: ResultSet = statement.executeQuery(sql)

                // Processa os resultados da consulta
                while (consultarRendimentos.next()) {
                    // Obtendo os resultados da consulta
                    val idRendimento: Int = consultarRendimentos.getInt("id_rendimento")
                    val nomeRendimento: String = consultarRendimentos.getString("tp_movimento")
                    val dataRendimento: String = consultarRendimentos.getString("dt_rendimento")
                    val valorRendimento: Float = consultarRendimentos.getFloat("valor_rendimento")

                    //formatando o nome do gasto
                    val nomeRendimentoFormatado = FormatarNome().formatar(nomeRendimento)

                    //formatando o a forma de pagamento
                    val forma_pagamento_formatada = ""

                    //formatando a data
                    //faz o fatiamento da data
                    val dia = dataRendimento.substring(8,10)
                    val mes = (dataRendimento.substring(5,7)).toInt()
                    val ano = dataRendimento.substring(0,4)

                    // Obtém o nome do mês atual para exibição
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.MONTH, mes)
                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

                    val dataFormatada = "$dia de $nomeMes de $ano"

                    //formatando o valor do gasto
                    // Obtém a instância de NumberFormat para a localidade do Brasil
                    val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                    val valorRendimentoFormatado = (formatacaoReal.format(valorRendimento)).toString()


                    val itemRendimento = OperacaoFinanceira(idRendimento, nomeRendimentoFormatado, forma_pagamento_formatada, valorRendimentoFormatado, dataRendimento)

                    // Adiciona o item à lista de itens
                    listaRendimentos += itemRendimento
                }

                // Fecha o ResultSet
                consultarRendimentos.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro consulta MySQL: ${e.message}")

            } finally {
                // Feche os recursos
                try {
                    statement.close()
                    con.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }

        } else {
            Log.e("ErroConexao", "Conexão não estabelecida")

        }

        return listaRendimentos.toList()
    }

    fun listaGastos(IDusuario: Int): List<OperacaoFinanceira> {
        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        val listaGastos = mutableListOf<OperacaoFinanceira>()

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            val sql = "SELECT * FROM gastos WHERE id_user_gasto = $IDusuario ORDER BY dt_gasto ASC"

            try {

                // Executa a consulta e obtém o resultado
                val consultarGastos: ResultSet = statement.executeQuery(sql)

                // Processa os resultados da consulta
                while (consultarGastos.next()) {
                    // Obtendo os resultados da consulta
                    val idGasto: Int = consultarGastos.getInt("id_gasto")
                    val nomeGasto: String = consultarGastos.getString("descricao_gasto")
                    val tipoMovimento: String = consultarGastos.getString("tp_transacao")
                    val valorGasto: Float = consultarGastos.getFloat("valor_gasto")
                    val dataGasto: String = consultarGastos.getString("dt_gasto")

                    //formatando o nome do gasto
                    val nomeGastoFormatado = FormatarNome().formatar(nomeGasto)

                    //formatando o a forma de pagamento
                    val forma_pagamento_formatada = FormatarNome().formatar(tipoMovimento)

                    //formatando a data
                    //faz o fatiamento da data
                    val dia = dataGasto.substring(8,10)
                    val mes = (dataGasto.substring(5,7)).toInt()
                    val ano = dataGasto.substring(0,4)

                    // Obtém o nome do mês atual para exibição
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.MONTH, mes)
                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

                    val dataFormatada = "$dia de $nomeMes de $ano"

                    //formatando o valor do gasto
                    // Obtém a instância de NumberFormat para a localidade do Brasil
                    val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                    val valorGastoFormatado = (formatacaoReal.format(valorGasto)).toString()


                    val itemGasto = OperacaoFinanceira(idGasto, nomeGastoFormatado, forma_pagamento_formatada, valorGastoFormatado, dataGasto)

                    // Adiciona o item à lista de itens
                    listaGastos += itemGasto

                }

                // Fecha o ResultSet
                consultarGastos.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro consulta MySQL: ${e.message}")

            } finally {
                // Feche os recursos
                try {
                    statement.close()
                    con.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }

        } else {
            Log.e("ErroConexao", "Conexão não estabelecida")

        }

        return listaGastos.toList()
    }

//------------------------------------ função para verificar atualizações nas listas do BD -----------------------------------------------//

    fun getUltimaAtualizacaoListas_MySQL(IDusuario: Int, consultarLista: String): LocalDateTime {
        // Inicialize a conexão com BD
        val con = ConnectionClass().CONN()

        val id_user_tabela = when (consultarLista.lowercase()) {
            "metas_financeiras" -> "meta"
            "gastos" -> "gasto"
            "rendimentos" -> "rendimento"
            else -> ""
        }


        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")

        var timesTamp: LocalDateTime = LocalDateTime.MIN

        // Verifica se a conexão foi estabelecida
        if (con != null) {
            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            var sql = "select data_criacao from ${consultarLista.lowercase()} where id_user_$id_user_tabela = $IDusuario order by data_criacao desc limit 1"

            try {
                // Execute a consulta e obtenha o resultado
                val consultarUltimaEntrada: ResultSet = statement.executeQuery(sql)

                // Processar o resultado
                if (consultarUltimaEntrada.next()) {
                    // Obtendo os resultados da consulta
                    val dataCriacao: Timestamp = consultarUltimaEntrada.getTimestamp("data_criacao")

                    timesTamp = LocalDateTime.parse(dataCriacao.toString(), formato)
                }

                consultarUltimaEntrada?.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro ao realizar a consulta MySQL: ${e.message}")
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

        return timesTamp
    }

//------------------------------------------------ clonar dados do MySQL p/ SQLite -------------------------------------------------------//

    fun clonarUsuario_MySQL_para_SQLite(IDusuario: Int, context:Context): String {
        // Inicializa a conexão com BD
        val con = ConnectionClass().CONN()

        //resultado da operação
        var resultado = ""

        // Verifica se a conexão foi estabelecida
        if (con != null){

            // Crie um Statement para executar a consulta
            val statement: Statement = con.createStatement()

            // Defina a consulta SQL
            val sql = "SELECT * FROM usuarios_debts WHERE id_usuario = $IDusuario"

            //val update = "update questionario_usuario set nvl_conhecimeto_financ = ?, tps_investimentos = ?, tx_uso_ecommerce = ?, tx_uso_app_transporte = ?, tx_uso_app_entrega = ? where id_user_quest = ?"

            try {

                // Executa a consulta e obtém o resultado
                val consultarUsuario: ResultSet = statement.executeQuery(sql)

                // Processa os resultados da consulta
                if (consultarUsuario.next()) {
                    // Obtendo os resultados da consulta
                    //val idUsuario = consultarUsuario.getInt("id_usuario")
                    val nome = consultarUsuario.getString("nome_usuario")
                    val emailUsuario = consultarUsuario.getString("email_usuario")
                    val cpfUsuario = consultarUsuario.getString("cpf_usuario")
                    val senhaUsuario = consultarUsuario.getString("senha_usuario")

                    // Salva os dados no banco de dados SQLite local
                    BancoDados(context).cadastrarConta(nome, emailUsuario, cpfUsuario, senhaUsuario)
                }

                // Fecha o ResultSet
                consultarUsuario.close()

                // Define a mensagem de sucesso
                resultado = "Dados do usuário clonadas com sucesso!"

            } catch (e: SQLException) {
                e.printStackTrace()
                Log.e("ErroConsulta", "Erro ao realizar a consulta: ${e.message}")

                resultado = "Erro ao realizar a consulta: ${e.message}"
            } finally {
                // Feche os recursos
                try {
                    statement.close()
                    con.close()
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

//    fun clonarListaRendimentos_MySQL_para_SQLite(IDusuario: Int, context: Context) {
//        // Inicializa a conexão com BD
//        val con = ConnectionClass().CONN()
//
//        // Verifica se a conexão foi estabelecida
//        if (con != null){
//
//            // Crie um Statement para executar a consulta
//            val statement: Statement = con.createStatement()
//
//            // Defina a consulta SQL
//            val sql = "SELECT * FROM rendimentos WHERE id_user_rendimento = $IDusuario ORDER BY dt_rendimento ASC"
//
//            try {
//
//                // Executa a consulta e obtém o resultado
//                val consultarRendimentos: ResultSet = statement.executeQuery(sql)
//
//                // Processa os resultados da consulta
//                while (consultarRendimentos.next()) {
//                    // Obtendo os resultados da consulta
//                    val tipoMovimento: String = consultarRendimentos.getString("tp_movimento")
//                    val dataRendimento: String = consultarRendimentos.getString("dt_rendimento")
//                    val valorRendimento: Float = consultarRendimentos.getFloat("valor_rendimento")
//
//                    BancoDados(context).salvarRendimento(tipoMovimento, dataRendimento, valorRendimento, IDusuario)
//                }
//
//                // Fecha o ResultSet
//                consultarRendimentos.close()
//
//            } catch (e: SQLException) {
//                e.printStackTrace()
//                Log.e("ErroConsulta", "Erro consulta MySQL: ${e.message}")
//
//            } finally {
//                // Feche os recursos
//                try {
//                    statement.close()
//                    con.close()
//                } catch (e: SQLException) {
//                    e.printStackTrace()
//                }
//            }
//
//        } else {
//            Log.e("ErroConexao", "Conexão não estabelecida")
//
//        }
//    }
//
//    fun clonarListaGastos_MySQL_para_SQLite(IDusuario: Int, context: Context) {
//        // Inicializa a conexão com BD
//        val con = ConnectionClass().CONN()
//
//        // Verifica se a conexão foi estabelecida
//        if (con != null){
//
//            // Crie um Statement para executar a consulta
//            val statement: Statement = con.createStatement()
//
//            // Defina a consulta SQL
//            val sql = "SELECT * FROM gastos WHERE id_user_gasto = $IDusuario ORDER BY dt_gasto ASC"
//
//            try {
//
//                // Executa a consulta e obtém o resultado
//                val consultarGastos: ResultSet = statement.executeQuery(sql)
//
//                // Processa os resultados da consulta
//                while (consultarGastos.next()) {
//                    // Obtendo os resultados da consulta
//                    val nomeGasto: String = consultarGastos.getString("descricao_gasto")
//                    val tipoMovimento: String = consultarGastos.getString("tp_transacao")
//                    val valorGasto: Float = consultarGastos.getFloat("valor_gasto")
//                    val dataGasto: String = consultarGastos.getString("dt_gasto")
//
//                    BancoDados(context).salvarGasto(nomeGasto, tipoMovimento, valorGasto, dataGasto, IDusuario)
//                }
//
//                // Fecha o ResultSet
//                consultarGastos.close()
//
//            } catch (e: SQLException) {
//                e.printStackTrace()
//                Log.e("ErroConsulta", "Erro consulta MySQL: ${e.message}")
//
//            } finally {
//                // Feche os recursos
//                try {
//                    statement.close()
//                    con.close()
//                } catch (e: SQLException) {
//                    e.printStackTrace()
//                }
//            }
//
//        } else {
//            Log.e("ErroConexao", "Conexão não estabelecida")
//
//        }
//    }

//    fun clonarListaMetas_MySQL_para_SQLite(IDusuario: Int, idMeta: Int, context: Context) {
//        // Inicializa a conexão com BD
//        val con = ConnectionClass().CONN()
//
//        // Verifica se a conexão foi estabelecida
//        if (con != null){
//
//            // Crie um Statement para executar a consulta
//            val statement: Statement = con.createStatement()
//
//            // Defina a consulta SQL
//            val sql = "SELECT * FROM metas_financeiras WHERE id_user_meta = $IDusuario AND id_meta = $idMeta"
//
//            try {
//
//                // Executa a consulta e obtém o resultado
//                val consultarMetas: ResultSet = statement.executeQuery(sql)
//
//                // Processa os resultados da consulta
//                while (consultarMetas.next()) {
//                    // Obtendo os resultados da consulta
//                    val nomeMeta: String = consultarMetas.getString("nome_meta")
//                    val dataMeta: String = consultarMetas.getString("dt_meta")
//
//                    val listaMetasJSON = consultarMetas.getString("lista_metas")
//                    val listaMetasConcluidasJSON = consultarMetas.getString("metas_concluidas")
//
//                    // Convertendo os dados JSON em listas
//                    val listaSTR_type = object : TypeToken<List<String>>() {}.type
//                    val listaBool_type = object : TypeToken<List<Boolean>>() {}.type
//
//                    val listaMetas: List<String> = Gson().fromJson(listaMetasJSON, listaSTR_type)
//                    val listaMetasConcluidas: List<Boolean> = Gson().fromJson(listaMetasConcluidasJSON, listaBool_type)
//
//                    val progressoMeta: Float = consultarMetas.getFloat("progresso_meta")
//                    val id_user_meta: Int = consultarMetas.getInt("id_user_meta")
//
//                    // Salva os dados no banco de dados SQLite local
//                    BancoDados(context).salvarMeta(nomeMeta, dataMeta, listaMetas, listaMetasConcluidas, progressoMeta, id_user_meta)
//                }
//
//                // Fecha o ResultSet
//                consultarMetas.close()
//
//            } catch (e: SQLException) {
//                e.printStackTrace()
//                Log.e("ErroConsulta", "Erro consulta MySQL: ${e.message}")
//
//            } finally {
//                // Feche os recursos
//                try {
//                    statement.close()
//                    con.close()
//                } catch (e: SQLException) {
//                    e.printStackTrace()
//                }
//            }
//
//        } else {
//            Log.e("ErroConexao", "Conexão não estabelecida")
//
//        }
//
//    }

}