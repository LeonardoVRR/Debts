package com.example.debts.API_Flask

import android.content.Context
import android.util.Log
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.CustomToast
import com.example.debts.models.LoginResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException
import kotlin.jvm.Throws
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import org.json.JSONObject

class Flask_Consultar_MySQL(private val context: Context) {

    object dadosUsuario {
        var listaDados: MutableList<String> = mutableListOf()
    }

    @Throws(IOException::class) // Indica que esta função pode lançar uma IOException
    private fun consultarMySQL(json: String, rota: String, metodo_requisicao: String): String { // Função que realiza o login, recebendo um JSON como String

        // Cria um cliente HTTP usando a biblioteca OkHttp
        val client = OkHttpClient()

        // Define a URL para a requisição de login, utilizando o IP do servidor Flask
        val url = "http://${IP_Server_Flask.ip_number}:5000/$rota"

        val builder: Request.Builder = Request.Builder() // Cria um construtor de requisição
        builder.url(url) // Define a URL da requisição

        // Define o tipo de mídia como JSON e cria o corpo da requisição
        val mediaType: MediaType? = MediaType.parse("application/json; charset=utf-8")

        // Cria o corpo da requisição com o JSON
        val body: RequestBody = RequestBody.create(mediaType, json)

        if (metodo_requisicao == "POST"){
            // Define o método da requisição como POST e adiciona o corpo
            builder.post(body)
        }

        when(metodo_requisicao) {
            "GET" -> builder.get()
            "POST" -> builder.post(body)
            "PUT" -> builder.put(body)
            "DELETE" -> builder.delete(body)
            else -> throw IllegalArgumentException("Operação inválida")
        }

        // Constrói a requisição a partir do builder
        val request: Request = builder.build()

        // Executa a requisição e obtém a resposta
        val response: okhttp3.Response = client.newCall(request).execute()

        // Verifica se a resposta não é nula e retorna o corpo da resposta como string
        return response.body()?.string() ?: throw IOException("Corpo da resposta nulo") // Lança uma exceção se o corpo da resposta for nulo
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun validarLogin(nome: String, senha: String): Boolean {
        var resultado = false

        val jsonRequest = """
        {
            "nome": "$nome",
            "senha": "$senha"
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "login", "POST")
            Log.d("RESPOSTA BRUTA Login", jsonResponse)  // Log da resposta bruta

            // Usando Gson para converter a resposta JSON em um objeto
            val gson = Gson()
            val loginResponse = gson.fromJson(jsonResponse, LoginResponse::class.java)

            Log.d("RESPOSTA FLASK", jsonResponse)  // Log da resposta
            Log.d("RESPOSTA FLASK2", "$loginResponse")

            // Verificando a mensagem da resposta
            when (loginResponse.message) {
                "Login valido" -> {
                    val dadosUsuario = loginResponse.dados_usuario
                    if (dadosUsuario != null) {
                        Log.d("Dados do Usuário", "Nome: ${dadosUsuario.nome}, Email: ${dadosUsuario.email}")

                        if (Flask_Consultar_MySQL.dadosUsuario.listaDados.size > 0) {
                            Flask_Consultar_MySQL.dadosUsuario.listaDados[0] = dadosUsuario.nome
                            Flask_Consultar_MySQL.dadosUsuario.listaDados[1] = dadosUsuario.email
                            Flask_Consultar_MySQL.dadosUsuario.listaDados[2] = dadosUsuario.cpf
                            Flask_Consultar_MySQL.dadosUsuario.listaDados[3] = dadosUsuario.senha
                            Flask_Consultar_MySQL.dadosUsuario.listaDados[4] = dadosUsuario.id_usuario.toString()
                        }

                        else {
                            // Armazena os dados do usuário em uma lista
                            Flask_Consultar_MySQL.dadosUsuario.listaDados.add(dadosUsuario.nome)
                            Flask_Consultar_MySQL.dadosUsuario.listaDados.add(dadosUsuario.email)
                            Flask_Consultar_MySQL.dadosUsuario.listaDados.add(dadosUsuario.cpf)
                            Flask_Consultar_MySQL.dadosUsuario.listaDados.add(dadosUsuario.senha)
                            Flask_Consultar_MySQL.dadosUsuario.listaDados.add(dadosUsuario.id_usuario.toString())

                            Log.d("LISTA DADOS FLASK", "${Flask_Consultar_MySQL.dadosUsuario.listaDados}")
                        }

                        resultado = true
                    } else {
                        Log.e("ERRO", "Dados do usuário estão nulos mesmo após login bem-sucedido.")
                    }
                }
                "Login invalido" -> {
                    resultado = false
                }
                else -> {
                    Log.e("ERRO", "Mensagem não reconhecida: ${loginResponse.message}")
                }
            }

        } catch (e: IOException) {
            Log.e("ERRO validarLogin", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO validarLogin", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO validarLogin", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun cadastrarConta(nome: String, email: String, cpf: String, senha: String): Boolean {
        var resultado = false

        val jsonRequest = """
        {
            "nome": "$nome",
            "email": "$email",
            "cpf": "$cpf",
            "senha": "$senha"
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "cadastrar_conta", "POST")
            Log.d("RESPOSTA BRUTA cadastrarConta", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val novaContaResponse = jsonObject.getString("message")

            // Usando Gson para converter a resposta JSON em um objeto
            //val novaContaResponse = Gson().fromJson(jsonResponse, cadastrarResponse::class.java)

            // Verificando a mensagem da resposta
            if (novaContaResponse == "Conta criada com sucesso") {
                 resultado = true
            } else {
                resultado = false
            }

            Log.d("RESPOSTA FLASK", "$novaContaResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO cadastrarConta", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO cadastrarConta", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO cadastrarConta", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun verificarQuestionario(IDusuario: Int): Boolean {
        var resultado = false

        val jsonRequest = """
        {
            "id": $IDusuario
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "verificar_questionario", "POST")
            Log.d("RESPOSTA BRUTA questionario", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val questionarioResponse = jsonObject.getString("message")

            // Usando Gson para converter a resposta JSON em um objeto
            //val novaContaResponse = Gson().fromJson(jsonResponse, cadastrarResponse::class.java)

            // Verificando a mensagem da resposta
            if (questionarioResponse == "Questinario preenchido.") {
                resultado = true
            } else {
                resultado = false
            }

            Log.d("RESPOSTA FLASK", "$questionarioResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO verificarQuestionario", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO verificarQuestionario", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO verificarQuestionario", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun atualizarDados(novoNome: String, novoEmail: String, IDusuario: Int): String {
        var resultado = ""

        val jsonRequest = """
        {
            "nome": "$novoNome",
            "email": "$novoEmail",
            "id": $IDusuario
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "atualizar_dados", "PUT")
            Log.d("RESPOSTA BRUTA atualizarDados", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val dadosResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (dadosResponse == "Dados atualizados com sucesso") {
                resultado = "Dados atualizados com sucesso!"
            } else {
                resultado = "Nenhum dado atualizado. Verifique o ID do usuário."
            }

            Log.d("RESPOSTA FLASK", "$dadosResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO atualizarDados", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO atualizarDados", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO atualizarDados", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun atualizarSenha(novaSenha: String, IDusuario: Int): String {
        var resultado = ""

        val jsonRequest = """
        {
            "senha": "$novaSenha",
            "id": $IDusuario
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "atualizar_senha", "PUT")
            Log.d("RESPOSTA BRUTA atualizarSenha", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val senhaResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (senhaResponse == "Senha atualizada com sucesso") {
                resultado = "Senha atualizada com sucesso!"
            } else {
                resultado = "Senha não atualizada. Verifique o ID do usuário."
            }

            Log.d("RESPOSTA FLASK", "$senhaResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO atualizarSenha", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO atualizarSenha", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO atualizarSenha", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun deletarUsuario(IDusuario: Int) {
        var resultado = ""

        val jsonRequest = """
        {
            "id": $IDusuario
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "deletar_usuario", "DELETE")
            Log.d("RESPOSTA BRUTA deletarUsuario", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val deletarResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (deletarResponse == "Usuario deletado com sucesso.") {
                resultado = "Senha atualizada com sucesso!"
            } else {
                resultado = "Usuario nao encontrado. Verifique o ID do usuario."
            }

            Log.d("RESPOSTA FLASK", "$deletarResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO deletarUsuario", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO deletarUsuario", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO deletarUsuario", "Erro inesperado: ${e.message}")
        }
    }
}