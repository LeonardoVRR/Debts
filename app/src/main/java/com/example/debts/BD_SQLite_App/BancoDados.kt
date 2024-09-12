package com.example.debts.BD_SQLite_App

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.ListAdapter
import com.example.debts.Conexao_BD.DadosMetasFinanceiras_Usuario_BD_Debts
import com.example.debts.CustomToast
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.layout_Item_lista.MyData
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.example.debts.lista_DebtMap.dados_listaMeta_Item_DebtMap
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
        if (File(dbPath).exists()) {

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
    fun gastosDiariosMes(mesGasto: String, IdUsuario: Int): List<Float> {
        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        var listaGastos = mutableListOf<Float>()

        try {
            // Consulta para obter os dados do usuário com base no id
            val gastos = bancoDados.rawQuery(
                "SELECT SUM(valor_gasto) AS total_gasto FROM Dados_Financeiros WHERE id_user_gasto = ? AND mes = ? GROUP BY dt_gasto ORDER BY dt_gasto ASC",
                arrayOf(IdUsuario.toString(), mesGasto)
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

    fun listaRendimentosMes(IdUsuario: Int): List<MyData> {

        var listaRendimentosMes: MutableList<MyData> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val regatarGastos: Cursor = bancoDados.rawQuery(" SELECT * FROM Rendimentos WHERE id_user_rendimento = ? ORDER BY dt_rendimento ASC", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (regatarGastos.moveToFirst()) {
                do {
                    //val idMeta = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("id_meta")).toString()
                    val nomeRendimento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("tp_movimento")).toString()
                    //val forma_pagamento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("tp_transacao"))
                    val dataRendimento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("dt_rendimento")).toString()
                    val valorRendimento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("valor_rendimento"))

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

                    val valorRendimentoFormatado = (formatacaoReal.format(valorRendimento.toFloat())).toString()


                    val itemGasto = MyData(nomeRendimentoFormatado, forma_pagamento_formatada, valorRendimentoFormatado, dataFormatada)

                    // Adiciona o item à lista de itens
                    listaRendimentosMes += itemGasto
                } while (regatarGastos.moveToNext()) // Continua para o próximo item
            }

            regatarGastos.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper metas: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaRendimentosMes.toList()
    }

    fun rendimentosMes(IdUsuario: Int, mesRendimento: String): List<Float> {

        var listaRendimentosMes: MutableList<Float> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val regatarGastos: Cursor = bancoDados.rawQuery(" SELECT SUM(valor_rendimento) AS total_rendimento FROM Rendimentos WHERE id_user_rendimento = ? AND mes = ? GROUP BY dt_rendimento ORDER BY dt_rendimento ASC", arrayOf(IdUsuario.toString(), mesRendimento))

            // Verifica se há resultados e processa todos
            if (regatarGastos.moveToFirst()) {
                do {
                    val valorRendimento = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("total_rendimento"))

                    // Adiciona o item à lista de itens
                    listaRendimentosMes += valorRendimento.toFloat()
                } while (regatarGastos.moveToNext()) // Continua para o próximo item
            }

            regatarGastos.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper metas: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaRendimentosMes.toList()
    }

    fun listaGastosMes(IdUsuario: Int): List<MyData> {

        var listaGastosMes: MutableList<MyData> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val regatarGastos: Cursor = bancoDados.rawQuery("SELECT descricao_gasto, tp_transacao, dt_gasto, valor_gasto FROM Dados_Financeiros WHERE id_user_gasto = ? ORDER BY dt_gasto DESC", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (regatarGastos.moveToFirst()) {
                do {
                    //val idMeta = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("id_meta")).toString()
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


                    val itemGasto = MyData(nomeGastoFormatado, forma_pagamento_formatada, valorGastoFormatado, dataFormatada)

                    // Adiciona o item à lista de itens
                    listaGastosMes += itemGasto
                } while (regatarGastos.moveToNext()) // Continua para o próximo item
            }

            regatarGastos.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper metas: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaGastosMes.toList()
    }

    fun listaGastosRecentes(IdUsuario: Int): List<MyData> {

        var listaGastosMes: MutableList<MyData> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val regatarGastos: Cursor = bancoDados.rawQuery("SELECT descricao_gasto, tp_transacao, dt_gasto, valor_gasto FROM Dados_Financeiros WHERE id_user_gasto = ? ORDER BY dt_gasto DESC LIMIT 7", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (regatarGastos.moveToFirst()) {
                do {
                    //val idMeta = regatarGastos.getString(regatarGastos.getColumnIndexOrThrow("id_meta")).toString()
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


                    val itemGasto = MyData(nomeGastoFormatado, forma_pagamento_formatada, valorGastoFormatado, dataFormatada)

                    // Adiciona o item à lista de itens
                    listaGastosMes += itemGasto
                } while (regatarGastos.moveToNext()) // Continua para o próximo item
            }

            regatarGastos.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper metas: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return listaGastosMes.toList()
    }

    //função para salvar uma nova meta
    fun salvarMeta(nomeMeta: String, dataMeta: String, listaMetas:List<String>, listaMetasEstados: List<Boolean>, progressoMeta: Float, IDusuario: Int) {

        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            //convertendo a lista de metas para JSON para poder salvar no banco de dados
            val listaMetasJSON = Gson().toJson(listaMetas)

            //convertendo a lista de estados para JSON para poder salvar no banco de dados
            val listaMetasEstadosJSON = Gson().toJson(listaMetasEstados)

            val nomeMetaFormatado = nomeMeta.lowercase()

            // query para salvar uma nova meta do usuário
            val query = "INSERT INTO Metas_Financeiras (nome_meta, dt_meta, lista_metas, metas_concluidas, progresso_meta, id_user_meta) VALUES ('$nomeMetaFormatado', '$dataMeta', '$listaMetasJSON', '$listaMetasEstadosJSON', $progressoMeta, $IDusuario)"

            //executa a query
            bancoDados.execSQL(query)

            CustomToast().showCustomToast(context, "Meta salva com sucesso!")

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
            val query = "DELETE FROM Metas_Financeiras WHERE id_user_meta = $IDusuario AND id_meta = $IdMeta"

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
            val regatarMetas: Cursor = bancoDados.rawQuery("SELECT * FROM Metas_Financeiras WHERE id_user_meta = ?", arrayOf(IdUsuario.toString()))

            // Verifica se há resultados e processa todos
            if (regatarMetas.moveToFirst()) {
                do {
                    val idMeta = regatarMetas.getString(regatarMetas.getColumnIndexOrThrow("id_meta")).toString()
                    val nomeMeta = regatarMetas.getString(regatarMetas.getColumnIndexOrThrow("nome_meta")).toString()
                    val dataMeta = regatarMetas.getString(regatarMetas.getColumnIndexOrThrow("dt_meta")).toString()
                    val listaMetasJSON = regatarMetas.getString(regatarMetas.getColumnIndexOrThrow("lista_metas"))

                    //formatando o nome da meta
                    val nomeFormatado = FormatarNome().formatar(nomeMeta)

                    // Especifica o tipo da lista para deserialização
                    val tipoLista = object : TypeToken<List<String>>() {}.type

                    // Converte a lista JSON resgatada do BD para o tipo "List<String>"
                    val listaMetas: List<String> = Gson().fromJson(listaMetasJSON, tipoLista)

                    // Converte a lista recuperada
                    val listaConvertida = DadosMetasFinanceiras_Usuario_BD_Debts().converter_Lista_MetasFinanceiras(listaMetas)

                    //faz o fatiamento da data
                    val dia = dataMeta.substring(8,10)
                    val mes = (dataMeta.substring(5,7)).toInt()
                    val ano = dataMeta.substring(0,4)

                    // Obtém o nome do mês atual para exibição
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.MONTH, mes)
                    val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))

                    val dataFormatada = "$dia de $nomeMes de $ano"

                    // Cria o item DebtMap
                    val itemDebtMap = DadosMetasFinanceiras_Usuario_BD_Debts().criarItemDebtMap(idMeta, nomeFormatado, dataFormatada, listaConvertida)

                    // Adiciona o item à lista de itens
                    listasItemsMetas += itemDebtMap
                } while (regatarMetas.moveToNext()) // Continua para o próximo item
            }

            regatarMetas.close()
        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro recuper metas: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
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
    fun MetasConcluidas(IdUsuario: Int, IdMeta: String): MutableList<Boolean> {
        var estadosMetas: MutableList<Boolean> = mutableListOf()

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter as metas do usuário
            val listaEstadoMetas: Cursor = bancoDados.rawQuery("SELECT metas_concluidas FROM Metas_Financeiras WHERE id_user_meta = ? AND id_meta = ?", arrayOf(IdUsuario.toString(), IdMeta))

            if (listaEstadoMetas.moveToFirst()) {
                val estadosMetasJSON = listaEstadoMetas.getString(listaEstadoMetas.getColumnIndexOrThrow("metas_concluidas")).toString()

                // Especifica o tipo da lista para deserialização
                val tipoLista = object : TypeToken<List<Boolean>>() {}.type

                // Converte a lista JSON resgatada do BD para o tipo "List<String>"
                estadosMetas = Gson().fromJson(estadosMetasJSON, tipoLista)
            }

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return estadosMetas
    }

    fun pegarProgressoAtualMeta(IDusuario: Int, idMeta:String): Float {
        var progressoMetaAtual: Float = 0f

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter o progresso da meta do usuário
            val progressoMetaSalvo = bancoDados.rawQuery(
                "SELECT progresso_meta FROM Metas_Financeiras WHERE id_user_meta = ? AND id_meta = ?",
                arrayOf(IDusuario.toString(), idMeta)
            )

            // Verifica se há resultados e extrai o valor do progresso_meta
            if (progressoMetaSalvo.moveToFirst()) {
                progressoMetaAtual = progressoMetaSalvo.getFloat(progressoMetaSalvo.getColumnIndexOrThrow("progresso_meta"))
            }

            // Fecha o cursor após o uso
            progressoMetaSalvo.close()

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return progressoMetaAtual
    }

    fun salvarEstadoMetas(IDusuario: Int, listaEstadoMetas: List<Boolean>, idMeta:String, progressoMeta: Float){
        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            //convertendo a lista de estados para JSON para poder salvar no banco de dados
            val listaJSON = Gson().toJson(listaEstadoMetas)

            // Cria um objeto ContentValues para usar parâmetros seguros
            val salvarEstados = ContentValues().apply {
                put("metas_concluidas", listaJSON)
                put("progresso_meta", progressoMeta)
            }

            // Consulta para obter as metas do usuário
            val query = "id_user_meta = ? AND id_meta = ?"
            val values = arrayOf(IDusuario.toString(), idMeta)

            bancoDados.update("Metas_Financeiras", salvarEstados, query, values)

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    fun salvarRendimento(descricaoRendimento: String, tipoTransacao: String, dataRendimento: String, valorRendimento: Float, IDusuario: Int) {
        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            val descricaoRendimentoFormatado = descricaoRendimento.lowercase()
            val tipoTransacaoFormatado = tipoTransacao.lowercase()

            //formatando a data
            //faz o fatiamento da data
            val dia = dataRendimento.substring(0, 2)
            val mes = dataRendimento.substring(3, 5)
            val ano = dataRendimento.substring(6, 10)

            val dataFormatada = "$ano-$mes-$dia"

            // query para salvar uma nova meta do usuário
            val query = "INSERT INTO Rendimentos (descricao_rendimento, tp_transacao, dt_rendimento, valor_redimento, id_user_rendimento) VALUES ('$descricaoRendimentoFormatado', '$tipoTransacaoFormatado', '$dataFormatada', $valorRendimento, $IDusuario)"

            //executa a query
            bancoDados.execSQL(query)

            CustomToast().showCustomToast(context, "Rendimento salvo com sucesso!")

        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta: ${e.message}")
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

    fun deletarUsuario(IDusuario: Int) {
        try {

            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // query para excluir um usuário
            val query = " DELETE FROM Usuarios_Debts WHERE id_usuario = $IDusuario"

            //executa a query
            bancoDados.execSQL(query)

            //CustomToast().showCustomToast(context, "Meta deletada com sucesso!")

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
            Log.e("Erro Consulta:", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }

}