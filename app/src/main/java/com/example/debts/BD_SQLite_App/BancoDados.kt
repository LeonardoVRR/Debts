package com.example.debts.BD_SQLite_App

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.ListAdapter
import com.example.debts.CustomToast
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class BancoDados(private var context: Context) {
    private lateinit var bancoDados: SQLiteDatabase

    //nome do banco de dados
    private val dbName: String = "Debts.db"

    // Obtém o caminho do banco de dados copiado para o armazenamento interno
    private val dbPath = context.getDatabasePath(dbName).absolutePath

    // Função que copia um banco de dados da pasta assets para o armazenamento interno do Android
    fun copyDatabase() {
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

    fun validarLogin(nome: String, senha: String): Boolean {
        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        //bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
        var contaExiste = false

        try {
            // Consulta para verificar se o email já existe
            val verificarNome = bancoDados.rawQuery("SELECT * FROM Usuarios_Debts WHERE nome_usuario = ?", arrayOf(nome))
            val verificarSenha = bancoDados.rawQuery("SELECT * FROM Usuarios_Debts WHERE senha_usuario = ?", arrayOf(senha))

            // Se o cursor retornar algum resultado, significa que o email já existe
            if (verificarNome.moveToFirst() && verificarSenha.moveToFirst()) {
                contaExiste = true
            }

            verificarNome.close()
            verificarSenha.close()
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

    fun salvarDadosUsuario(nome: String): List<String>? {
        val dbPath = context.getDatabasePath("Debts.db").absolutePath
        var bancoDados: SQLiteDatabase? = null
        var dadosUsuario: List<String>? = null

        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            // Consulta para obter os dados do usuário com base no nome
            val cursor = bancoDados.rawQuery("SELECT email_usuario, senha_usuario, cpf_usuario FROM Usuarios_Debts WHERE nome_usuario = ?", arrayOf(nome))

            // Verifica se há resultados e extrai os dados do usuário
            if (cursor.moveToFirst()) {
                val emailUsuario = cursor.getString(cursor.getColumnIndexOrThrow("email_usuario"))
                val senhaUsuario = cursor.getString(cursor.getColumnIndexOrThrow("senha_usuario"))
                val cpfUsuario = cursor.getString(cursor.getColumnIndexOrThrow("cpf_usuario"))

                // Armazena os dados do usuário em uma lista
                dadosUsuario = listOf(
                    nome,
                    emailUsuario,
                    cpfUsuario,
                    senhaUsuario
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


    fun cadastrarConta(nome: String, email: String, cpf: String, senha: String) {

        // Abre o banco de dados existente no caminho especificado
        bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        //bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

        //var contaExistente: Boolean = false

        val cpf_numeric = cpf.toLong()

        try {

            // Mostra uma mensagem de sucesso ao usuário
            //CustomToast().showCustomToast(context, "Usuário adicionado com sucesso")

            // Adiciona um novo usuário na tabela Usuarios_Debts
            val values = ContentValues().apply {
                put("nome_usuario", nome)
                put("email_usuario", email)
                put("cpf_usuario", cpf_numeric)
                put("senha_usuario", senha)
            }
            bancoDados.insert("Usuarios_Debts", null, values)

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

    fun listarMetas() {
        try {
            // Abre o banco de dados existente no caminho especificado
            bancoDados = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

            //vai pegar todas as linhas onde o "id_usuario_meta" seja igual a "id_usuario"
            val meuCursor: Cursor = bancoDados.rawQuery("SELECT m.* FROM Usuarios_Metas m JOIN Usuarios_Debts u ON m.id_usuario_meta = u.id_usuario WHERE m.id_usuario_meta = u.id_usuario", null)
            var listasMetas_STR: MutableList<String> = mutableListOf()

            val adapter= ArrayAdapter<String> (
                context,
                android.R.layout.simple_list_item_1
            )

            bancoDados.close()
        }
        catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro Consulta: ${e.printStackTrace()}")
            Log.e("Erro Consulta:", "${e.printStackTrace()}")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }
}