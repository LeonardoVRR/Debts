package com.example.debts.BD_SQLite_App

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.debts.Conexao_BD.DadosMetasFinanceiras_Usuario_BD_Debts
import com.example.debts.CustomToast
import com.example.debts.FormatarMoeda.formatarReal
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.ManipularData.ManipularData
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.example.debts.models.MovintoDia
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar
import java.text.NumberFormat
import java.util.Locale

class BancoDados(private var context: Context) {

    private lateinit var bancoDados: SQLiteDatabase

    //nome do banco de dados
    private val dbName: String = "Debts.db"

    // Obtém o caminho do banco de dados copiado para o armazenamento interno
    private val dbPath = context.getDatabasePath(dbName).absolutePath

    // Função que copia um banco de dados da pasta assets para o armazenamento interno do Android
    fun copyDatabase() {

        // Verifica se o arquivo do banco de dados já existe no armazenamento interno
        // Se o arquivo não existir, chama o método para copiar o banco de dados da pasta assets para o armazenamento interno
        if (!File(dbPath).exists()) {

            // Abre o arquivo do banco de dados localizado na pasta assets
            val inputStream: InputStream = context.assets.open(dbName)

            // Obtém o caminho para o banco de dados no diretório de armazenamento interno do aplicativo
            val outputFile = context.getDatabasePath(dbName)

            // Certifica-se de que o diretório do banco de dados existe, criando-o se necessário
            outputFile.parentFile?.mkdirs()

            // Cria um fluxo de saída para escrever o banco de dados no armazenamento interno
            val outputStream: OutputStream = FileOutputStream(outputFile)

            // Cria um buffer para copiar os dados do banco de dados
            val buffer = ByteArray(1024)
            var length: Int

            // Lê o banco de dados em partes (buffer de 1024 bytes) e escreve no arquivo de destino
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            // Garante que todos os dados sejam escritos e fecha os fluxos de entrada e saída
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }
    }

    fun acessarBancoDados() {
        try {
            // Mostra uma mensagem de sucesso ao usuário
            CustomToast().showCustomToast(context, "Conectado ao Banco de Dados")

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

//            // Cria a tabela Usuarios_Debts se ela não existir
//            bancoDados.execSQL("""
//            CREATE TABLE IF NOT EXISTS Usuarios_Debts (
//                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
//                nome_usuario VARCHAR NOT NULL,
//                email_usuario VARCHAR NOT NULL,
//                cpf_usuario NUMERIC(11) NOT NULL,
//                senha_usuario VARCHAR NOT NULL
//            )
//        """)
//
//            // Cria a tabela Usuarios_Metas se ela não existir
//            bancoDados.execSQL("""
//            CREATE TABLE IF NOT EXISTS Usuarios_Metas (
//                id_meta INTEGER PRIMARY KEY AUTOINCREMENT,
//                id_usuario_meta INTEGER,
//                nome_meta VARCHAR,
//                dataCriacao VARCHAR,
//                listaMetas TEXT,
//                metasConcluidas TEXT,
//                FOREIGN KEY (id_usuario_meta) REFERENCES Usuarios_Debts (id_usuario)
//            )
//        """)

        } catch (e: Exception) {
            // Mostra uma mensagem de erro ao usuário
            CustomToast().showCustomToast(context, "Erro Conexão: ${e.message}")
            // Loga o erro
            Log.e("Erro Conexão", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                //bancoDados.close()
            }
        }
    }

    fun limparBancoDados() {
        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            val cursor = bancoDados.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)

            if (cursor.moveToFirst()) {
                do {
                    val tableName = cursor.getString(0)
                    if (tableName != "android_metadata" && tableName != "sqlite_sequence") {
                        bancoDados.execSQL("DELETE FROM $tableName")
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            // Liberar o espaço usado
            bancoDados.execSQL("VACUUM")

            //CustomToast().showCustomToast(context, "BD totalmente Limpo!")

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro resetar o BD: ${e.message}")
            Log.e("Erro resetar o BD:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    //função para verificar se já existe um email salva no BD na hora de criar uma conta
    fun verificarDados(email: String): Boolean {

        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        //bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
        var emailExiste = false

        try {
            // Consulta para verificar se o email já existe
            val cursor = bancoDados.rawQuery("SELECT * FROM Usuarios_Debts WHERE email_usuario = ?", arrayOf(email))

            // Se o cursor retornar algum resultado, significa que o email já existe
            if (cursor.moveToFirst()) {
                emailExiste = true
            }

            cursor.close()
        } catch (e: Exception) {
            Log.e("Erro Verificação", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return emailExiste
    }

    //função para verificar se existe uma conta para fazer o login do usuario
    fun validarLogin(nome: String, senha: String): Boolean {
        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        //bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
        var contaExiste = false

        try {
            val verificarLogin = "SELECT * FROM Usuarios_Debts WHERE nome_usuario = ? AND senha_usuario = ?"
            val cursor = bancoDados.rawQuery(verificarLogin, arrayOf(nome, senha))

            // Verifica se o cursor retornou algum resultado
            val usuarioEncontrado = cursor.moveToFirst()

            // Se o cursor retornar algum resultado, significa que o email já existe
            if (usuarioEncontrado) {
                contaExiste = true
            }

            cursor.close()

        } catch (e: Exception) {
            Log.e("Erro Verificação", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return contaExiste
    }

    //função para salvar os dados do usuario logado
    fun salvarDadosUsuario(nome: String): List<String>? {
        val dbPath = context.getDatabasePath("Debts.db").absolutePath
        var bancoDados: SQLiteDatabase? = null
        var dadosUsuario: List<String>? = null

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter os dados do usuário com base no nome
            val cursor = bancoDados.rawQuery("SELECT email_usuario, senha_usuario, cpf_usuario, id_usuario FROM Usuarios_Debts WHERE nome_usuario = ?", arrayOf(nome))

            // Verifica se há resultados e extrai os dados do usuário
            if (cursor.moveToFirst()) {
                val emailUsuario = cursor.getString(cursor.getColumnIndexOrThrow("email_usuario"))
                val senhaUsuario = cursor.getString(cursor.getColumnIndexOrThrow("senha_usuario"))
                val cpfUsuario = cursor.getString(cursor.getColumnIndexOrThrow("cpf_usuario"))
                val idUsuario = cursor.getString(cursor.getColumnIndexOrThrow("id_usuario"))

                // Armazena os dados do usuário em uma lista
                dadosUsuario = listOf(
                    nome,
                    emailUsuario,
                    cpfUsuario,
                    senhaUsuario,
                    idUsuario
                )
            }

            cursor.close()
        } catch (e: Exception) {
            Log.e("Erro Consulta", e.message ?: "Erro desconhecido")
        } finally {
            // Fecha o banco de dados se ele foi aberto
            bancoDados?.close()
        }

        return dadosUsuario
    }

    // função para criar uma nova conta de usuario
    fun cadastrarConta(nome: String, email: String, cpf: String, senha: String) {

        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        //bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        //var contaExistente: Boolean = false

        val cpf_numeric = cpf.toLong()

        try {

            // Mostra uma mensagem de sucesso ao usuário
            //CustomToast().showCustomToast(context, "Usuário adicionado com sucesso")

            //definindo uma consulta ao banco de dados
            val query = "INSERT INTO Usuarios_Debts (nome_usuario, email_usuario, cpf_usuario, senha_usuario) VALUES ('$nome', '$email', $cpf_numeric, '$senha')"

            // Adiciona um novo usuário na tabela Usuarios_Debts
            bancoDados.execSQL(query)

        } catch (e: Exception) {
            // Mostra uma mensagem de erro ao usuário
            CustomToast().showCustomToast(context, "Erro ao adicionar usuário: ${e.message}")
            // Loga o erro
            Log.e("Erro Inserção", e.message ?: "Erro desconhecido")

        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    //função para atualizar o nome ou email do usuario no BD
    fun atualizarDados(novoNome: String, novoEmail: String, IdUsuario: Int) {
        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        try {
            val query = "UPDATE Usuarios_Debts SET nome_usuario = '$novoNome', email_usuario = '$novoEmail' WHERE id_usuario = $IdUsuario"
            bancoDados.execSQL(query)
        } catch (e: Exception) {
            // Mostra uma mensagem de erro ao usuário
            CustomToast().showCustomToast(context, "Erro ao atualizar os dados: ${e.message}")
            // Loga o erro
            Log.e("Erro Inserção", e.message ?: "Erro desconhecido")

        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    //função para atualizar a senha do usuario no BD
    fun atualizarSenha(novaSenha: String, IdUsuario: Int) {
        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        try {
            val query = "UPDATE Usuarios_Debts SET senha_usuario = '$novaSenha' WHERE id_usuario = $IdUsuario"
            bancoDados.execSQL(query)
        } catch (e: Exception) {
            // Mostra uma mensagem de erro ao usuário
            CustomToast().showCustomToast(context, "Erro ao atualizar a senha: ${e.message}")
            // Loga o erro
            Log.e("Erro Inserção", e.message ?: "Erro desconhecido")

        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    //função que retorna todos os gastos do mes do usuario
    fun gastosDiariosMes(mesGasto: String, IdUsuario: Int, ano: String): List<Float> {
        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        var listaGastos = mutableListOf<Float>()

        try {
            // Consulta para obter os dados do usuário com base no id
            val gastos = bancoDados.rawQuery(
                "SELECT SUM(valor_gasto) AS total_gasto FROM Gastos WHERE id_user_gasto = ? AND mes = ? AND strftime('%Y', dt_gasto) = ? GROUP BY dt_gasto ORDER BY dt_gasto ASC",
                arrayOf(IdUsuario.toString(), mesGasto, ano)
            )

            // Processa todos os resultados da consulta
            while (gastos.moveToNext()) {
                val valorGasto = gastos.getString(gastos.getColumnIndexOrThrow("total_gasto"))
                listaGastos.add(valorGasto.toFloat())
            }

            gastos.close()
        } catch (e: Exception) {
            // Mostra uma mensagem de erro ao usuário
            CustomToast().showCustomToast(context, "Erro ao recuperar os gastos: ${e.message}")
            // Loga o erro
            Log.e("Erro Inserção", e.message ?: "Erro desconhecido")

        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaGastos.toList()
    }

    //função que retorna todos os gastos do mes do usuario para usar no grafico
    fun gastosDiariosMesGraf(mesGasto: String, IdUsuario: Int, ano: String): List<MovintoDia> {
        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        var listaGastos = mutableListOf<MovintoDia>()

        try {
            // Consulta para obter os dados do usuário com base no id
            val gastos = bancoDados.rawQuery(
                "SELECT dt_gasto, SUM(valor_gasto) AS total_gasto FROM Gastos WHERE id_user_gasto = ? AND mes = ? AND strftime('%Y', dt_gasto) = ? GROUP BY dt_gasto ORDER BY dt_gasto ASC",
                arrayOf(IdUsuario.toString(), mesGasto, ano)
            )

            // Processa todos os resultados da consulta
            while (gastos.moveToNext()) {
                val valorGasto = gastos.getString(gastos.getColumnIndexOrThrow("total_gasto"))
                val diaGasto = gastos.getString(gastos.getColumnIndexOrThrow("dt_gasto"))
                listaGastos.add(
                    MovintoDia(
                        diaGasto.split("-")[2].trim().toInt() - 1,
                        valorGasto.toFloat()
                    )
                )
            }

            gastos.close()
        } catch (e: Exception) {
            // Mostra uma mensagem de erro ao usuário
            CustomToast().showCustomToast(context, "Erro ao recuperar os gastos: ${e.message}")
            // Loga o erro
            Log.e("Erro Inserção", e.message ?: "Erro desconhecido")

        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaGastos.toList()
    }

//    //função que retorna todos os rendimentos do mes do usuario para usar no grafico
//    fun rendimentosDiariosMesGraf(mesRendimento: String, IdUsuario: Int, ano: String): List<MovintoDia> {
//        // Abre o banco de dados existente no caminho especificado
//        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
//
//        var listaRendimentos = mutableListOf<MovintoDia>()
//
//        try {
//            // Consulta para obter os dados do usuário com base no id
//            val rendimentos = bancoDados.rawQuery(
//                "SELECT dt_rendimento, SUM(valor_rendimento) AS total_rendimento FROM Rendimentos WHERE id_user_rendimento = ? AND mes = ? AND strftime('%Y', dt_rendimento) = ? GROUP BY dt_rendimento ORDER BY dt_rendimento ASC",
//                arrayOf(IdUsuario.toString(), mesRendimento, ano)
//            )
//
//            // Processa todos os resultados da consulta
//            while (rendimentos.moveToNext()) {
//                val valorRendimento = rendimentos.getString(rendimentos.getColumnIndexOrThrow("total_rendimento"))
//                val diaRendimento = rendimentos.getString(rendimentos.getColumnIndexOrThrow("dt_rendimento"))
//                listaRendimentos.add(
//                    MovintoDia(
//                        diaRendimento.split("-")[2].trim().toInt() - 1,
//                        valorRendimento.toFloat()
//                    )
//                )
//            }
//
//            rendimentos.close()
//        } catch (e: Exception) {
//            // Mostra uma mensagem de erro ao usuário
//            CustomToast().showCustomToast(context, "Erro ao recuperar os gastos: ${e.message}")
//            // Loga o erro
//            Log.e("Erro Inserção", e.message ?: "Erro desconhecido")
//
//        } finally {
//            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
//            if (::bancoDados.isInitialized) {
//                bancoDados.close()
//            }
//        }
//
//        return listaRendimentos.toList()
//    }

    //função que retorna todos os rendimentos do mes do usuario para usar no grafico
    fun rendimentos_n_rastreados(IdUsuario: Int): List<OperacaoFinanceira> {

        var listaRendimentosMes: MutableList<OperacaoFinanceira> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter os rendimentos do usuário
            val regatarRendimento: Cursor = bancoDados.rawQuery(" SELECT * FROM Rendimentos WHERE id_user_rendimento = ? ORDER BY dt_rendimento ASC", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (regatarRendimento.moveToFirst()) {
                do {
                    val idRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("id_rendimento")).toInt()
                    val nomeRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("tp_movimento")).toString()
                    //val forma_pagamento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("tp_transacao"))
                    val dataRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("dt_rendimento")).toString()
                    val valorRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("valor_rendimento"))

                    //formatando o nome do gasto
                    val nomeRendimentoFormatado = FormatarNome().formatar(nomeRendimento)

                    //formatando o a forma de pagamento
                    val forma_pagamento_formatada = ""

                    //faz o fatiamento da data
                    val dia = dataRendimento.split("-")[2].trim()
                    val mes = dataRendimento.split("-")[1].trim().toInt()
                    val ano = dataRendimento.split("-")[0].trim()

                    val nomeMes = ManipularData().pegarNomeMes(mes)

                    val dataFormatada = "$dia de $nomeMes de $ano"

                    //formatando o valor do gasto
                    // Obtém a instância de NumberFormat para a localidade do Brasil
                    val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                    val valorRendimentoFormatado = (formatacaoReal.format(valorRendimento.toFloat())).toString()


                    val itemGasto = OperacaoFinanceira(idRendimento, nomeRendimentoFormatado, forma_pagamento_formatada, valorRendimentoFormatado, dataFormatada)

                    // Adiciona o item à lista de itens
                    listaRendimentosMes += itemGasto
                } while (regatarRendimento.moveToNext()) // Continua para o próximo item
            }

            regatarRendimento.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper listaRendimentosMes: ${e.message}")
            Log.e("Erro Consulta listaRendimentosMes:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaRendimentosMes.toList()
    }

    fun listaRendimentosMes(IdUsuario: Int, mes: String = "", ano: String = ""): List<OperacaoFinanceira> {

        var listaRendimentosMes: MutableList<OperacaoFinanceira> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            if (mes != "" && ano != "") {
                // Consulta para obter os rendimentos do usuário
                val regatarRendimento: Cursor = bancoDados.rawQuery(" SELECT * FROM Rendimentos WHERE id_user_rendimento = ? AND strftime('%m', dt_rendimento) = ? AND strftime('%Y', dt_rendimento) = ? ORDER BY dt_rendimento ASC", arrayOf(IdUsuario.toString(), mes, ano))



                // Verifica se há resultados e processa todos
                if (regatarRendimento.moveToFirst()) {
                    do {
                        val idRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("id_rendimento")).toInt()
                        val nomeRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("tp_movimento")).toString()
                        //val forma_pagamento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("tp_transacao"))
                        val dataRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("dt_rendimento")).toString()
                        val valorRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("valor_rendimento"))

                        //formatando o nome do gasto
                        val nomeRendimentoFormatado = FormatarNome().formatar(nomeRendimento)

                        //formatando o a forma de pagamento
                        val forma_pagamento_formatada = ""

//                    //formatando a data
//                    //faz o fatiamento da data
//                    val dia = dataRendimento.substring(8,10)
//                    val mes = (dataRendimento.substring(5,7)).toInt()
//                    val ano = dataRendimento.substring(0,4)
//
//                    // Obtém o nome do mês atual para exibição
//                    val calendar = Calendar.getInstance()
//                    calendar.set(Calendar.MONTH, mes)
//                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

                        //faz o fatiamento da data
                        val dia = dataRendimento.split("-")[2].trim()
                        val mes = dataRendimento.split("-")[1].trim().toInt()
                        val ano = dataRendimento.split("-")[0].trim()

                        val nomeMes = ManipularData().pegarNomeMes(mes)

                        val dataFormatada = "$dia de $nomeMes de $ano"

                        //formatando o valor do gasto
                        // Obtém a instância de NumberFormat para a localidade do Brasil
                        val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                        val valorRendimentoFormatado = (formatacaoReal.format(valorRendimento.toFloat())).toString()


                        val itemGasto = OperacaoFinanceira(idRendimento, nomeRendimentoFormatado, forma_pagamento_formatada, valorRendimentoFormatado, dataFormatada)

                        // Adiciona o item à lista de itens
                        listaRendimentosMes += itemGasto
                    } while (regatarRendimento.moveToNext()) // Continua para o próximo item
                }

                regatarRendimento.close()
            }

            else {
                // Consulta para obter os rendimentos do usuário
                val regatarRendimento: Cursor = bancoDados.rawQuery(" SELECT * FROM Rendimentos WHERE id_user_rendimento = ? ORDER BY dt_rendimento ASC", arrayOf(IdUsuario.toString()))



                // Verifica se há resultados e processa todos
                if (regatarRendimento.moveToFirst()) {
                    do {
                        val idRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("id_rendimento")).toInt()
                        val nomeRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("tp_movimento")).toString()
                        //val forma_pagamento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("tp_transacao"))
                        val dataRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("dt_rendimento")).toString()
                        val valorRendimento = regatarRendimento.getString(regatarRendimento.getColumnIndexOrThrow("valor_rendimento"))

                        //formatando o nome do gasto
                        val nomeRendimentoFormatado = FormatarNome().formatar(nomeRendimento)

                        //formatando o a forma de pagamento
                        val forma_pagamento_formatada = ""

//                    //formatando a data
//                    //faz o fatiamento da data
//                    val dia = dataRendimento.substring(8,10)
//                    val mes = (dataRendimento.substring(5,7)).toInt()
//                    val ano = dataRendimento.substring(0,4)
//
//                    // Obtém o nome do mês atual para exibição
//                    val calendar = Calendar.getInstance()
//                    calendar.set(Calendar.MONTH, mes)
//                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

                        //faz o fatiamento da data
                        val dia = dataRendimento.split("-")[2].trim()
                        val mes = dataRendimento.split("-")[1].trim().toInt()
                        val ano = dataRendimento.split("-")[0].trim()

                        val nomeMes = ManipularData().pegarNomeMes(mes)

                        val dataFormatada = "$dia de $nomeMes de $ano"

                        //formatando o valor do gasto
                        // Obtém a instância de NumberFormat para a localidade do Brasil
                        val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                        val valorRendimentoFormatado = (formatacaoReal.format(valorRendimento.toFloat())).toString()


                        val itemGasto = OperacaoFinanceira(idRendimento, nomeRendimentoFormatado, forma_pagamento_formatada, valorRendimentoFormatado, dataFormatada)

                        // Adiciona o item à lista de itens
                        listaRendimentosMes += itemGasto
                    } while (regatarRendimento.moveToNext()) // Continua para o próximo item
                }

                regatarRendimento.close()
            }


        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper listaRendimentosMes: ${e.message}")
            Log.e("Erro Consulta listaRendimentosMes:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaRendimentosMes.toList()
    }

//    fun rendimentosMes(IdUsuario: Int, mesRendimento: String): List<Float> {
//
//        var listaRendimentosMes: MutableList<Float> = mutableListOf()
//
//        try {
//            // Abre o banco de dados existente no caminho especificado
//            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
//
//            // Consulta para obter as metas do usuário
//            val regatarGastos: Cursor = bancoDados.rawQuery("SELECT SUM(valor_rendimento) AS total_rendimento FROM Rendimentos WHERE id_user_rendimento = ? AND mes = ? GROUP BY dt_rendimento ORDER BY dt_rendimento ASC", arrayOf(IdUsuario.toString(), mesRendimento))
//
//            // Verifica se há resultados e processa todos
//            if (regatarGastos.moveToFirst()) {
//                do {
//                    val valorRendimento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("total_rendimento"))
//
//                    // Adiciona o item à lista de itens
//                    listaRendimentosMes += valorRendimento.toFloat()
//                } while (regatarGastos.moveToNext()) // Continua para o próximo item
//            }
//
//            regatarGastos.close()
//
//        } catch (e: Exception) {
//            CustomToast().showCustomToast(context, "Erro recuper rendimentos: ${e.message}")
//            Log.e("Erro Consulta rendimentosMes:", e.message ?: "Erro desconhecido")
//        } finally {
//            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
//            if (::bancoDados.isInitialized) {
//                bancoDados.close()
//            }
//        }
//
//        return listaRendimentosMes.toList()
//    }

    fun listaGastosMes(IdUsuario: Int, mes: String = "", ano: String = ""): List<OperacaoFinanceira> {

        var listaGastosMes: MutableList<OperacaoFinanceira> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            if (mes != "" && ano != "") {
                // Consulta para obter as metas do usuário
                val regatarGastos: Cursor = bancoDados.rawQuery("SELECT * FROM Gastos WHERE id_user_gasto = ? AND strftime('%m', dt_gasto) = ? AND strftime('%Y', dt_gasto) = ? ORDER BY dt_gasto DESC", arrayOf(IdUsuario.toString(), mes, ano))

                // Verifica se há resultados e processa todos
                if (regatarGastos.moveToFirst()) {
                    do {
                        val idGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("id_gasto")).toInt()
                        val nomeGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("descricao_gasto")).toString()
                        val forma_pagamento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("tp_transacao"))
                        val dataGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("dt_gasto")).toString()
                        val valor_compra = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("valor_gasto"))

                        //formatando o nome do gasto
                        val nomeGastoFormatado = FormatarNome().formatar(nomeGasto)

                        //formatando o a forma de pagamento
                        val forma_pagamento_formatada = FormatarNome().formatar(forma_pagamento)

//                    //formatando a data
//                    //faz o fatiamento da data
//                    val dia = dataGasto.substring(8,10)
//                    val mes = (dataGasto.substring(5,7)).toInt()
//                    val ano = dataGasto.substring(0,4)
//
//                    // Obtém o nome do mês atual para exibição
//                    val calendar = Calendar.getInstance()
//                    calendar.set(Calendar.MONTH, mes)
//                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))
//


                        //faz o fatiamento da data
                        val dia = dataGasto.split("-")[2].trim()
                        val mes = dataGasto.split("-")[1].trim().toInt()
                        val ano = dataGasto.split("-")[0].trim()

                        val nomeMes = ManipularData().pegarNomeMes(mes)

                        val dataFormatada = "$dia de $nomeMes de $ano"

                        //formatando o valor do gasto
                        // Obtém a instância de NumberFormat para a localidade do Brasil
                        val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                        val valorGastoFormatado = (formatacaoReal.format(valor_compra.toFloat())).toString()


                        val itemGasto = OperacaoFinanceira(idGasto, nomeGastoFormatado, forma_pagamento_formatada, valorGastoFormatado, dataFormatada)

                        // Adiciona o item à lista de itens
                        listaGastosMes += itemGasto
                    } while (regatarGastos.moveToNext()) // Continua para o próximo item
                }

                regatarGastos.close()
            }

            else {
                // Consulta para obter as metas do usuário
                val regatarGastos: Cursor = bancoDados.rawQuery("SELECT * FROM Gastos WHERE id_user_gasto = ? ORDER BY dt_gasto DESC", arrayOf(IdUsuario.toString()))

                // Verifica se há resultados e processa todos
                if (regatarGastos.moveToFirst()) {
                    do {
                        val idGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("id_gasto")).toInt()
                        val nomeGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("descricao_gasto")).toString()
                        val forma_pagamento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("tp_transacao"))
                        val dataGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("dt_gasto")).toString()
                        val valor_compra = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("valor_gasto"))

                        //formatando o nome do gasto
                        val nomeGastoFormatado = FormatarNome().formatar(nomeGasto)

                        //formatando o a forma de pagamento
                        val forma_pagamento_formatada = FormatarNome().formatar(forma_pagamento)

//                    //formatando a data
//                    //faz o fatiamento da data
//                    val dia = dataGasto.substring(8,10)
//                    val mes = (dataGasto.substring(5,7)).toInt()
//                    val ano = dataGasto.substring(0,4)
//
//                    // Obtém o nome do mês atual para exibição
//                    val calendar = Calendar.getInstance()
//                    calendar.set(Calendar.MONTH, mes)
//                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))
//


                        //faz o fatiamento da data
                        val dia = dataGasto.split("-")[2].trim()
                        val mes = dataGasto.split("-")[1].trim().toInt()
                        val ano = dataGasto.split("-")[0].trim()

                        val nomeMes = ManipularData().pegarNomeMes(mes)

                        val dataFormatada = "$dia de $nomeMes de $ano"

                        //formatando o valor do gasto
                        // Obtém a instância de NumberFormat para a localidade do Brasil
                        val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                        val valorGastoFormatado = (formatacaoReal.format(valor_compra.toFloat())).toString()


                        val itemGasto = OperacaoFinanceira(idGasto, nomeGastoFormatado, forma_pagamento_formatada, valorGastoFormatado, dataFormatada)

                        // Adiciona o item à lista de itens
                        listaGastosMes += itemGasto
                    } while (regatarGastos.moveToNext()) // Continua para o próximo item
                }

                regatarGastos.close()
            }

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper gastos: ${e.message}")
            Log.e("Erro Consulta Gasto Mes:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaGastosMes.toList()
    }

    fun listaGastosRecentes(IdUsuario: Int): List<OperacaoFinanceira> {

        var listaGastosMes: MutableList<OperacaoFinanceira> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val regatarGastos: Cursor = bancoDados.rawQuery("SELECT * FROM Gastos WHERE id_user_gasto = ? ORDER BY dt_gasto DESC LIMIT 7", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (regatarGastos.moveToFirst()) {
                do {
                    val idGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("id_gasto")).toInt()
                    val nomeGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("descricao_gasto")).toString()
                    val forma_pagamento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("tp_transacao"))
                    val dataGasto = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("dt_gasto")).toString()
                    val valor_compra = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("valor_gasto"))

                    //formatando o nome do gasto
                    val nomeGastoFormatado = FormatarNome().formatar(nomeGasto)

                    //formatando o a forma de pagamento
                    val forma_pagamento_formatada = FormatarNome().formatar(forma_pagamento)

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

                    val valorGastoFormatado = (formatacaoReal.format(valor_compra.toFloat())).toString()


                    val itemGasto = OperacaoFinanceira(idGasto, nomeGastoFormatado, forma_pagamento_formatada, valorGastoFormatado, dataFormatada)

                    // Adiciona o item à lista de itens
                    listaGastosMes += itemGasto
                } while (regatarGastos.moveToNext()) // Continua para o próximo item
            }

            regatarGastos.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper Gasto Recente: ${e.message}")
            Log.e("Erro Consulta Gasto Recente:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaGastosMes.toList()
    }

    fun listarCartoes(IdUsuario: Int): List<OperacaoFinanceira> {

        var listaCartoes: MutableList<OperacaoFinanceira> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val regatarCartoes: Cursor = bancoDados.rawQuery("SELECT * FROM cartoes WHERE usuario = ?", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (regatarCartoes.moveToFirst()) {
                do {
                    val id_cartao = regatarCartoes.getString(regatarCartoes.getColumnIndexOrThrow("cd_cartao")).toInt()
                    val ds_operadora = regatarCartoes.getString(regatarCartoes.getColumnIndexOrThrow("ds_operadora")).toString()
                    val tp_credito = regatarCartoes.getString(regatarCartoes.getColumnIndexOrThrow("tp_credito")).toFloat()
                    val tp_debito = regatarCartoes.getString(regatarCartoes.getColumnIndexOrThrow("tp_debito")).toFloat()
                    val saldo = regatarCartoes.getString(regatarCartoes.getColumnIndexOrThrow("saldo")).toFloat()
                    val limite = regatarCartoes.getString(regatarCartoes.getColumnIndexOrThrow("limite")).toFloat()

                    //formatando o nome do gasto
                    val nomeFormatado = FormatarNome().formatar(ds_operadora)

                    var tp_cartao = ""
                    var valor = ""

                    if (tp_credito > 0 && tp_debito <= 0){
                        tp_cartao = "Crédito"
                        //valor = formatarReal().formatarParaReal(limite)
                    }

                    else if (tp_debito > 0 && tp_credito <= 0){
                        tp_cartao = "Débito"
                        //valor = formatarReal().formatarParaReal(saldo)
                    }


                    val itemGasto = OperacaoFinanceira(id_cartao, nomeFormatado, tp_cartao, valor, "")

                    // Adiciona o item à lista de itens
                    listaCartoes += itemGasto
                } while (regatarCartoes.moveToNext()) // Continua para o próximo item
            }

            regatarCartoes.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper listar Cartoes: ${e.message}")
            Log.e("Erro Consulta listar Cartoes:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaCartoes.toList()
    }

    //função para salvar uma nova meta
    fun salvarMeta(cartao: Int, vlr_inicial: Float, perc_meta: Float, dt_meta_inicio: String, dt_meta_conclusao: String, IDusuario: Int, ramo_meta: Int, idMeta: Int) {

        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // query para salvar uma nova meta do usuário
            val query = "INSERT INTO Metas (id_metas, usuario, cartao, vlr_inicial, perc_meta, dt_meta_inicio, dt_meta_conclusao, ramo_meta) VALUES ($idMeta, $IDusuario, $cartao, $vlr_inicial, $perc_meta, '$dt_meta_inicio', '$dt_meta_conclusao', $ramo_meta)"

            //executa a query
            bancoDados.execSQL(query)

            //CustomToast().showCustomToast(context, "Meta salva com sucesso!")

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro ao salvar meta: ${e.message}")
            Log.e("Erro ao salvar meta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    //função para excluir uma meta
    fun excluirMeta(IDusuario: Int, IdMeta: String) {
        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // query para excluir uma meta do usuário
            val query = "DELETE FROM Metas WHERE usuario = $IDusuario AND id_metas = $IdMeta"

            //executa a query
            bancoDados.execSQL(query)

            CustomToast().showCustomToast(context, "Meta deletada com sucesso!")

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro ao salvar meta: ${e.message}")
            Log.e("Erro ao salvar meta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    //função que retorna uma lista de itens que seram exibidos na tela DebtMap
    fun listarMetas(IdUsuario: Int): List<dados_listaMeta_DebtMap> {
        val listasItemsMetas = mutableListOf<dados_listaMeta_DebtMap>()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val resgatarMetas: Cursor = bancoDados.rawQuery("SELECT * FROM Metas WHERE usuario = ?", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (resgatarMetas.moveToFirst()) {
                do {
                    val idMeta = resgatarMetas.getString(resgatarMetas.getColumnIndexOrThrow("id_metas")).toString()
                    val vlr_inicial = resgatarMetas.getFloat(resgatarMetas.getColumnIndexOrThrow("vlr_inicial"))
                    val perc_meta = resgatarMetas.getFloat(resgatarMetas.getColumnIndexOrThrow("perc_meta"))
                    val dt_meta_inicio = resgatarMetas.getString(resgatarMetas.getColumnIndexOrThrow("dt_meta_inicio")).toString()
                    val dt_meta_conclusao = resgatarMetas.getString(resgatarMetas.getColumnIndexOrThrow("dt_meta_conclusao")).toString()

                    val dataMeta = dt_meta_inicio.split(" ")[0]

                    //faz o fatiamento da data
                    val dia = dataMeta.split("-")[2].trim()
                    val mes = dataMeta.split("-")[1].trim().toInt()
                    val ano = dataMeta.split("-")[0].trim()

                    val nomeMes = ManipularData().pegarNomeMes(mes)

                    val dataFormatada = "$dia de $nomeMes de $ano"

                    // Cria o item DebtMap
                    val itemDebtMap = DadosMetasFinanceiras_Usuario_BD_Debts().criarItemDebtMap(idMeta, dataFormatada, dt_meta_conclusao, vlr_inicial, perc_meta)

                    // Adiciona o item à lista de itens
                    listasItemsMetas += itemDebtMap
                } while (resgatarMetas.moveToNext()) // Continua para o próximo item
            }

            resgatarMetas.close()
        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper lista metas: ${e.message}")
            Log.e("Erro Consulta Listar Metas:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        // Retorna a lista de itens
        return listasItemsMetas.toList()
    }

    //função que retorna a lista de estados da metas (lista guarda qual meta já foi concluida ou não concluida)
    fun MetasConcluidas(IdUsuario: Int, IdMeta: String, dt_meta_conclusao: String) {
        var bancoDados: SQLiteDatabase? = null

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para atualizar as metas do usuário
            val sql = "UPDATE metas SET dt_meta_conclusao = ? WHERE usuario = ? AND id_metas = ?"

            // Executa o comando de atualização
            bancoDados.execSQL(sql, arrayOf(dt_meta_conclusao, IdUsuario.toString(), IdMeta))

            // Log para sucesso (opcional)
            Log.d("MetasConcluidas", "Meta $IdMeta do usuário $IdUsuario foi atualizada com sucesso.")

        } catch (e: Exception) {
            // Exibe a mensagem de erro
            CustomToast().showCustomToast(context, "Erro Consulta Meta Concluida: ${e.message}")
            Log.e("Erro Consulta Metas Concluidas:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            bancoDados?.close()
        }
    }


//    fun pegarProgressoAtualMeta(IDusuario: Int, idMeta:String): Float {
//        var progressoMetaAtual: Float = 0f
//
//        try {
//            // Abre o banco de dados existente no caminho especificado
//            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
//
//            // Consulta para obter o progresso da meta do usuário
//            val progressoMetaSalvo = bancoDados.rawQuery(
//                "SELECT progresso_meta FROM Metas_Financeiras WHERE id_user_meta = ? AND id_meta = ?",
//                arrayOf(IDusuario.toString(), idMeta)
//            )
//
//            // Verifica se há resultados e extrai o valor do progresso_meta
//            if (progressoMetaSalvo.moveToFirst()) {
//                progressoMetaAtual = progressoMetaSalvo.getFloat(progressoMetaSalvo.getColumnIndexOrThrow("progresso_meta"))
//            }
//
//            // Fecha o cursor após o uso
//            progressoMetaSalvo.close()
//
//        } catch (e: Exception) {
//            CustomToast().showCustomToast(context, "Erro Consulta Prog. Meta: ${e.message}")
//            Log.e("Erro Consulta Progesso Meta:", e.message ?: "Erro desconhecido")
//        } finally {
//            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
//            if (::bancoDados.isInitialized) {
//                bancoDados.close()
//            }
//        }
//
//        return progressoMetaAtual
//    }
//
//    fun salvarEstadoMetas(IDusuario: Int, listaEstadoMetas: List<Boolean>, idMeta:String, progressoMeta: Float){
//        try {
//
//            // Abre o banco de dados existente no caminho especificado
//            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
//
//            //convertendo a lista de estados para JSON para poder salvar no banco de dados
//            val listaJSON = Gson().toJson(listaEstadoMetas)
//
//            // Cria um objeto ContentValues para usar parâmetros seguros
//            val salvarEstados = ContentValues().apply {
//                put("metas_concluidas", listaJSON)
//                put("progresso_meta", progressoMeta)
//            }
//
//            // Consulta para obter as metas do usuário
//            val query = "id_user_meta = ? AND id_meta = ?"
//            val values = arrayOf(IDusuario.toString(), idMeta)
//
//            bancoDados.update("Metas_Financeiras", salvarEstados, query, values)
//
//        } catch (e: Exception) {
//            CustomToast().showCustomToast(context, "Erro Consulta: ${e.message}")
//            Log.e("Erro Consulta Estado Meta:", e.message ?: "Erro desconhecido")
//        } finally {
//            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
//            if (::bancoDados.isInitialized) {
//                bancoDados.close()
//            }
//        }
//    }

    fun salvarRendimento(tipoMovimento: String, dataRendimento: String, valorRendimento: Float, IDusuario: Int, idRendimento: Int): Boolean {
        Log.d("SALVANDO RENDIMENTO", "FUNÇÂO CHAMADA")

        var rendimentoSalvo: Boolean = false

        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            val tipoMovimentoFormatado = tipoMovimento.lowercase()

            //formatando a data
            //faz o fatiamento da data
//            val dia = dataRendimento.substring(0, 2)
//            val mes = dataRendimento.substring(3, 5)
//            val ano = dataRendimento.substring(6, 10)
//
//            val mesBalanco = (dataRendimento.substring(3, 5)).toInt()
//
//            // Obtém o nome do mês atual para exibição
//            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.MONTH, mesBalanco)
//            val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

            val dia = dataRendimento.split("-")[2].trim()
            val mes = dataRendimento.split("-")[1].trim().toInt()
            val ano = dataRendimento.split("-")[0].trim()

            val mesFormatado = String.format("%02d", mes)

            val dataFormatada = "$ano-$mesFormatado-$dia"

            val nomeMes = ManipularData().pegarNomeMes(mes)

            // query para salvar uma nova meta do usuário
            val query = "INSERT INTO Rendimentos (id_rendimento, tp_movimento, dt_rendimento, mes, valor_rendimento, id_user_rendimento) VALUES ($idRendimento, '$tipoMovimentoFormatado', '$dataFormatada', '$nomeMes', $valorRendimento, $IDusuario)"

            //executa a query
            bancoDados.execSQL(query)

            //CustomToast().showCustomToast(context, "Rendimento salvo com sucesso!")

            rendimentoSalvo = true

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta Rendimento: ${e.message}")
            Log.e("Erro Consulta Rendimento:", e.message ?: "Erro desconhecido")

            rendimentoSalvo = false
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return rendimentoSalvo
    }

    fun salvarCartao(id_cartao: Int, ds_operadora: String, tp_credito: Int = 0, tp_debito: Int = 0, saldo: Float = 0f, limite: Float = 0f, IDusuario: Int): Boolean {

        var cartaoSalvo: Boolean = false

        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // query para salvar uma nova meta do usuário
            val query = "INSERT INTO cartoes (cd_cartao, usuario, ds_operadora, tp_credito, tp_debito, saldo, limite) VALUES ($id_cartao, $IDusuario, '$ds_operadora', $tp_credito, $tp_debito, $saldo, $limite)"

            //executa a query
            bancoDados.execSQL(query)

            //CustomToast().showCustomToast(context, "Rendimento salvo com sucesso!")

            cartaoSalvo = true

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta Cartao: ${e.message}")
            Log.e("Erro Consulta Cartao:", e.message ?: "Erro desconhecido")

            cartaoSalvo = false
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return cartaoSalvo
    }

    fun salvarGasto(nomeGasto: String, tipoMovimento: String, valorGasto: Float, dataGasto: String, IDusuario: Int, idGasto: Int): Boolean {
        var gastoSalvo: Boolean = false

        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            val tipoMovimentoFormatado = tipoMovimento.lowercase()

            //formatando a data
            //faz o fatiamento da data
//            val dia = dataGasto.substring(0, 2)
//            val mes = (dataGasto.substring(3, 5))
//            val ano = dataGasto.substring(6, 10)

            val dia = dataGasto.split("-")[2].trim()
            val mes = dataGasto.split("-")[1].trim().toInt()
            val ano = dataGasto.split("-")[0].trim()

            // Obtém o nome do mês atual para exibição
//            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.MONTH, mesBalanco)
            //val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

            val nomeMes = ManipularData().pegarNomeMes(mes)

            val dataFormatada = "$ano-$mes-$dia"

            // query para salvar uma nova meta do usuário
            val query = "INSERT INTO Gastos (id_gasto, descricao_gasto, tp_transacao, valor_gasto, dt_gasto, mes, id_user_gasto) VALUES ($idGasto, '$nomeGasto', '$tipoMovimento', $valorGasto, '$dataGasto', '$nomeMes', $IDusuario)"

            //executa a query
            bancoDados.execSQL(query)

            //CustomToast().showCustomToast(context, "Divida salva com sucesso!")

            gastoSalvo = true

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta Gasto: ${e.message}")
            Log.e("Erro Consulta Salvar Gasto:", e.message ?: "Erro desconhecido")

            gastoSalvo = false
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return gastoSalvo
    }

    fun deletarUsuario(IDusuario: Int) {
        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // query para excluir um usuário
            val query = " DELETE FROM Usuarios_Debts WHERE id_usuario = $IDusuario"

            //executa a query
            bancoDados.execSQL(query)

            //CustomToast().showCustomToast(context, "Usuário deletado com sucesso!")

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro ao salvar meta: ${e.message}")
            Log.e("Erro ao salvar meta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    fun salvarQuestionario(nvl_conhecimeto_financ: Int, tps_investimentos: List<String>, tx_uso_ecommerce: Int, tx_uso_app_transporte: Int, tx_uso_app_entrega: Int, IDusuario: Int) {

        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            //convertendo a lista para JSON para poder salvar no banco de dados
            val listaTps_investimentosJSON = Gson().toJson(tps_investimentos)

            // Consulta para obter o progresso da meta do usuário
            val questionarioSalvo = bancoDados.rawQuery(
                "SELECT * FROM QuestionarioUsuario WHERE id_user_quest = ?",
                arrayOf(IDusuario.toString())
            )

            // Verifica se já existe um questionario salvo se existir ele só atualiza as informações
            if (questionarioSalvo.moveToFirst()) {
                // Cria um objeto ContentValues para usar parâmetros seguros
                val salvarAlteracao = ContentValues().apply {
                    put("nvl_conhecimeto_financ", nvl_conhecimeto_financ)
                    put("tps_investimentos", listaTps_investimentosJSON)
                    put("tx_uso_ecommerce", tx_uso_ecommerce)
                    put("tx_uso_app_transporte", tx_uso_app_transporte)
                    put("tx_uso_app_entrega", tx_uso_app_entrega)
                }

                // Consulta para obter as metas do usuário
                val query = "id_user_quest = ?"
                val values = arrayOf(IDusuario.toString())

                bancoDados.update("QuestionarioUsuario", salvarAlteracao, query, values)

                CustomToast().showCustomToast(context, "Informações atualizadas com sucesso!")
            }

            else {
                // query para salvar uma nova meta do usuário
                val query = "INSERT INTO QuestionarioUsuario (nvl_conhecimeto_financ, tps_investimentos, tx_uso_ecommerce, tx_uso_app_transporte, tx_uso_app_entrega, id_user_quest) VALUES ('$nvl_conhecimeto_financ', '$tps_investimentos', '$tx_uso_ecommerce', '$tx_uso_app_transporte', $tx_uso_app_entrega, $IDusuario)"

                //executa a query
                bancoDados.execSQL(query)

                CustomToast().showCustomToast(context, "Questionario salvo com sucesso!")
            }

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta: ${e.message}")
            Log.e("Erro Consulta Questionario:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

}