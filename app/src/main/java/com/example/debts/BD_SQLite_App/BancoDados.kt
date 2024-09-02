package com.example.debts.BD_SQLite_App

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.ListAdapter
import com.example.debts.CustomToast

class BancoDados(private var context: Context) {
    private lateinit var bancoDados: SQLiteDatabase

    fun criarBancoDados() {
        try {
            // Mostra uma mensagem de sucesso ao usuário
            CustomToast().showCustomToast(context, "Conectado ao Banco de Dados")

            // Abre ou cria o banco de dados
            bancoDados = context.openOrCreateDatabase("Debts", Context.MODE_PRIVATE, null)

            // Cria a tabela Usuarios_Debts se ela não existir
            bancoDados.execSQL("""
            CREATE TABLE IF NOT EXISTS Usuarios_Debts (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nome_usuario VARCHAR NOT NULL,
                email_usuario VARCHAR NOT NULL,
                cpf_usuario VARCHAR(11) NOT NULL,
                senha_usuario VARCHAR NOT NULL
            )
        """)

            // Cria a tabela Usuarios_Metas se ela não existir
            bancoDados.execSQL("""
            CREATE TABLE IF NOT EXISTS Usuarios_Metas (
                id_meta INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario_meta INTEGER,
                nome_meta VARCHAR,
                dataCriacao VARCHAR,
                listaMetas TEXT,
                metasConcluidas TEXT,
                FOREIGN KEY (id_usuario_meta) REFERENCES Usuarios_Debts (id_usuario)
            )
        """)

            // Fecha a conexão com o banco de dados
            bancoDados.close()
        } catch (e: Exception) {
            // Mostra uma mensagem de erro ao usuário
            CustomToast().showCustomToast(context, "Erro Conexão: ${e.message}")
            // Loga o erro
            Log.e("Erro Conexão", e.message ?: "Erro desconhecido")
        } finally {
            // Garante que a conexão seja fechada mesmo se ocorrer uma exceção
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }
    }


    fun validarLogin() {

    }

    fun cadastrarConta(nome: String, email: String, cpf: String, senha: String): Boolean {
        var contaExistente = false

        try {
            bancoDados = context.openOrCreateDatabase("Debts", Context.MODE_PRIVATE, null)

            // Verifica se o cpf já existe
            val cursor: Cursor = bancoDados.rawQuery("SELECT * FROM Usuarios_Debts WHERE cpf_usuario = ?", arrayOf(cpf))

            if (cursor.count > 0) {
                // E-mail já existe
                contaExistente = true
                //CustomToast().showCustomToast(context, "E-mail já cadastrado.")
            } else {
                contaExistente = false
                // Insere o novo usuário
                val sql: String = """
                INSERT INTO Usuarios_Debts (nome_usuario, email_usuario, cpf_usuario, senha_usuario)
                VALUES (?, ?, ?, ?)
            """
                val stmt: SQLiteStatement = bancoDados.compileStatement(sql)
                stmt.bindString(1, nome)
                stmt.bindString(2, email)
                stmt.bindString(3, cpf)
                stmt.bindString(4, senha)
                stmt.executeInsert()
                //CustomToast().showCustomToast(context, "Cadastro realizado com sucesso.")
            }

            cursor.close()
            bancoDados.close()
        } catch (e: Exception) {
            CustomToast().showCustomToast(context, "Erro: ${e.message}")
            Log.e("Erro", e.message ?: "Erro desconhecido")
        } finally {
            if (::bancoDados.isInitialized) {
                bancoDados.close()
            }
        }

        return contaExistente
    }

    fun listarMetas() {
        try {
            bancoDados = context.openOrCreateDatabase("Debts", Context.MODE_PRIVATE, null)

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
        }
    }
}