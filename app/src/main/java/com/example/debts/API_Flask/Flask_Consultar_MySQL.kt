package com.example.debts.API_Flask

import android.content.Context
import android.util.Log
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.Conexao_BD.DadosMetasFinanceiras_Usuario_BD_Debts
import com.example.debts.CustomToast
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.example.debts.models.LoginResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import kotlin.jvm.Throws
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import org.json.JSONObject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.NumberFormat
import java.util.Locale

class Flask_Consultar_MySQL(private val context: Context) {

    object dadosUsuario {
        var listaDados: MutableList<String> = mutableListOf()
    }

    @Throws(IOException::class) // Indica que esta função pode lançar uma IOException
    private fun consultarMySQL(json: String, rota: String, metodo_requisicao: String): String { // Função que realiza a consulta ao MySQL, recebendo um JSON como String

        // Cria um cliente HTTP usando a biblioteca OkHttp
        val client = OkHttpClient()

        // Define a URL para a requisição, utilizando o IP do servidor Flask
        val url = "http://${IP_Server_Flask.ip_number}:36366/$rota"

        val builder: Request.Builder = Request.Builder() // Cria um construtor de requisição
        builder.url(url) // Define a URL da requisição

        // Define o tipo de mídia como JSON
        val mediaType: MediaType? = MediaType.parse("application/json; charset=utf-8")

        // Cria o corpo da requisição com o JSON
        val body: RequestBody = RequestBody.create(mediaType, json)

        // Configura a requisição de acordo com o método
        when(metodo_requisicao) {
            "GET" -> builder.get()
            "POST" -> builder.post(body)
            "PUT" -> builder.put(body)
            "DELETE" -> builder.delete(body)
            else -> throw IllegalArgumentException("Operação inválida: $metodo_requisicao")
        }

        // Constrói a requisição a partir do builder
        val request: Request = builder.build()

        try {
            // Executa a requisição e obtém a resposta
            val response: okhttp3.Response = client.newCall(request).execute()

            // Log da resposta para depuração
            Log.d("CONSULTAR_MYSQL - $rota", "Response code: ${response.code()}")

            // Verifica se a resposta é bem-sucedida e retorna o corpo da resposta como string
            if (response.isSuccessful) {
                return response.body()?.string() ?: throw IOException("Corpo da resposta nulo")
            } else {
                Log.e("ERRO CONSULTAR_MYSQL - $rota", "Erro na requisição: ${response.message()}")
                throw IOException("Erro na requisição - $rota: ${response.message()} - Código: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e("ERRO CONSULTAR_MYSQL - $rota", "Erro de IO: ${e.message}")
            throw e // Re-lança a exceção para tratamento posterior
        } catch (e: Exception) {
            Log.e("ERRO CONSULTAR_MYSQL - $rota", "Erro inesperado: ${e.message}")
            throw IOException("Erro inesperado - $rota: ${e.message}") // Lança uma exceção genérica para qualquer outro erro
        }
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

//--------------------------------------------------------------------------------------------------------------------------------//

    fun salvarBalanco(tipoMovimento: String, data: String, valor: Float, IDusuario: Int, tp_OpFinanc: String, nomeBalanco:String = ""): String {
        var resultado = ""

        var jsonRequest = ""

        var rota = when (tp_OpFinanc) {
            "rendimento" -> "salvar_rendimento"
            "gasto" -> "salvar_gasto"
            else -> "operacao_invalida"
        }

        if (tp_OpFinanc == "rendimento"){
            jsonRequest = """
                {
                    "tipo_movimento": "$tipoMovimento",
                    "data_rendimento": "$data",
                    "valor_rendimento": "$valor",
                    "id": "$IDusuario"
                }
            """.trimIndent()
        }

        else {
            jsonRequest = """
            {
                    "descricao_gasto": "$nomeBalanco",
                    "tipo_movimento":"$tipoMovimento",
                    "data_gasto":"$data",
                    "valor_gasto": $valor,
                    "id": $IDusuario
            }
        """.trimIndent()
        }

        try {
            val jsonResponse = consultarMySQL(jsonRequest, rota, "POST")
            Log.d("RESPOSTA BRUTA salvarBalanço", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val salvarBalancoResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (tp_OpFinanc == "rendimento"){
                if (salvarBalancoResponse == "Rendimento salvo com sucesso.") {
                    resultado = "Rendimento salvo com sucesso."
                } else {
                    resultado = "Rendimento não salvo. Verifique o ID do usuário."
                }
            }

            else {
                if (salvarBalancoResponse == "Gasto salvo com sucesso.") {
                    resultado = "Gasto salvo com sucesso."
                } else {
                    resultado = "Gasto não salvo. Verifique o ID do usuário."
                }
            }

            Log.d("RESPOSTA FLASK", "$salvarBalancoResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO salvarBalanço", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO salvarBalanço", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO salvarBalanço", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

//    fun salvarRendimento(tipoMovimento: String, dataRendimento: String, valorRendimento: Float, IDusuario: Int): String {
//        var resultado = ""
//
//        val jsonRequest = """
//        {
//            "tipo_movimento": "$tipoMovimento",
//            "data_rendimento": "$dataRendimento",
//            "valor_rendimento": "$valorRendimento",
//            "id": "$IDusuario"
//        }
//    """.trimIndent()
//
//        try {
//            val jsonResponse = consultarMySQL(jsonRequest, "salvar_rendimento", "POST")
//            Log.d("RESPOSTA BRUTA salvarRendimento", jsonResponse)  // Log da resposta bruta
//
//            // Cria um objeto JSONObject a partir da string JSON
//            val jsonObject = JSONObject(jsonResponse)
//
//            // Extraindo o valor da chave "message"
//            val salvarRendimentoResponse = jsonObject.getString("message")
//
//            // Verificando a mensagem da resposta
//            if (salvarRendimentoResponse == "Rendimento salvo com sucesso.") {
//                resultado = "Rendimento salvo com sucesso."
//            } else {
//                resultado = "Rendimento não salvo. Verifique o ID do usuário."
//            }
//
//            Log.d("RESPOSTA FLASK", "$salvarRendimentoResponse - $resultado")
//
//        } catch (e: IOException) {
//            Log.e("ERRO salvarRendimento", "IOException: ${e.message}")
//        } catch (e: JsonSyntaxException) {
//            Log.e("ERRO salvarRendimento", "Erro ao analisar o JSON: ${e.message}")
//        } catch (e: Exception) {
//            Log.e("ERRO salvarRendimento", "Erro inesperado: ${e.message}")
//        }
//
//        return resultado
//    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun salvarQuestionario(nvl_conhecimeto_financ: Int, tps_investimentos: List<String>, tx_uso_ecommerce: Int, tx_uso_app_transporte: Int, tx_uso_app_entrega: Int, IDusuario: Int): String {
        var resultado = ""

        var metodo_requisicao = ""

        val jsonRequest = """
        {
            "nvl_conhecimeto_financ": "$nvl_conhecimeto_financ",
            "tps_investimentos": "$tps_investimentos",
            "tx_uso_ecommerce": "$tx_uso_ecommerce",
            "tx_uso_app_transporte":"$tx_uso_app_transporte",
            "tx_uso_app_entrega":"$tx_uso_app_entrega",
            "id": "$IDusuario"
        }
    """.trimIndent()

        try {

            if (verificarQuestionario(IDusuario)) {
                metodo_requisicao = "PUT"
            } else {
                metodo_requisicao = "POST"
            }

            val jsonResponse = consultarMySQL(jsonRequest, "salvar_questionario", metodo_requisicao)
            Log.d("RESPOSTA BRUTA salvarQuestionario", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val salvarQuestionarioResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (salvarQuestionarioResponse == "Questionario atualizado com sucesso") {
                resultado = "Questionario atualizado com sucesso."
            } else {
                resultado = "Questionario salvo com sucesso."
            }

            Log.d("RESPOSTA FLASK", "$salvarQuestionarioResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO salvarQuestionario", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO salvarQuestionario", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO salvarQuestionario", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun atualizarMeta(IDusuario: Int, IdMeta: Int, metas_concluidas: List<Boolean>, progresso_meta: Float): String {
        var resultado = ""

        val metas_concluidasJSON = Gson().toJson(metas_concluidas)

        val jsonRequest = """
        {
            "id_meta": $IdMeta,
            "metas_concluidas": $metas_concluidasJSON,
            "progresso_meta": $progresso_meta,
            "id": $IDusuario
        }
    """.trimIndent()

        Log.d("lista JSON", jsonRequest)

        try {

            val jsonResponse = consultarMySQL(jsonRequest, "atualizar_meta", "PUT")
            Log.d("RESPOSTA BRUTA atualizarMeta", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val atualizarMetaResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (atualizarMetaResponse == "Meta MySQL atualizada") {
                resultado = "Metas atualizadas com sucesso."
            } else {
                resultado = "Não existem metas para atualizar."
            }

            Log.d("RESPOSTA FLASK", "$atualizarMetaResponse - $resultado")

        } catch (e: IOException) {
            Log.e("ERRO atualizarMeta", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO atualizarMeta", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO atualizarMeta", "Erro inesperado: ${e.message}")
        }

        return resultado
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun deletarMeta(IDusuario: Int, IdMeta: Int): Boolean {
        var metaExcluida: Boolean = false

        val jsonRequest = """
        {
            "id": $IDusuario,
            "id_meta": $IdMeta
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "deletar_meta", "DELETE")
            Log.d("RESPOSTA BRUTA deletarMeta", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val deletarMetaResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (deletarMetaResponse == "Meta excluida com sucesso.") {
                metaExcluida = true
            } else {
                metaExcluida = false
            }

            Log.d("RESPOSTA FLASK", "$deletarMetaResponse - $metaExcluida")

        } catch (e: IOException) {
            Log.e("ERRO deletarMeta", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO deletarMeta", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO deletarMeta", "Erro inesperado: ${e.message}")
        }

        return metaExcluida
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun listarMetas(IDusuario: Int): List<dados_listaMeta_DebtMap> {
        val listasItemsMetas = mutableListOf<dados_listaMeta_DebtMap>()

        val jsonRequest = """
        {
            "id": $IDusuario
        }
    """.trimIndent()

        Log.d("lista Metas", jsonRequest)

        try {

            val jsonResponse = consultarMySQL(jsonRequest, "listar_metas", "POST")
            Log.d("RESPOSTA BRUTA listarMetas", jsonResponse)  // Log da resposta bruta

            val json = object : TypeToken<List<Meta>>() {}.type
            val metaList: List<Meta> = Gson().fromJson(jsonResponse, json)

            //Log.d("RESPOSTA BRUTA listarMetas", "${metaList.size}")  // Log da resposta bruta

            metaList.forEach { meta ->
                val idMeta: Int = meta.id_meta
                val nomeMeta: String = meta.nome_meta
                val dataMeta: String = meta.data_meta
                val listaMetas = meta.lista_metas
                val listaMetasConcluidas = meta.metas_concluidas

                val progressoMeta: Float = meta.progresso_meta

                //formatando o nome da meta
                val nomeFormatado = FormatarNome().formatar(nomeMeta)

                // Converte a lista recuperada
                val listaConvertida = DadosMetasFinanceiras_Usuario_BD_Debts().converter_Lista_MetasFinanceiras(listaMetas, listaMetasConcluidas)

                // Cria o item DebtMap
                val itemDebtMap = DadosMetasFinanceiras_Usuario_BD_Debts().criarItemDebtMap(idMeta.toString(), nomeFormatado, progressoMeta, dataMeta, listaConvertida)

                // Adiciona o item à lista de itens
                listasItemsMetas += itemDebtMap
            }

        } catch (e: IOException) {
            Log.e("ERRO listarMetas", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO listarMetas", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO listarMetas", "Erro inesperado: ${e.message}")
        }

        return listasItemsMetas.toList()
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun listOpFinanceiras(IDusuario: Int, tipo_Opfinanc: String): List<OperacaoFinanceira> {
        val listaOpFinanc = mutableListOf<OperacaoFinanceira>()

        var rota = when (tipo_Opfinanc) {
            "rendimentos" -> "listar_rendimentos"
            "gastos" -> "listar_gastos"
            else -> "operacao_invalida"
        }

        val jsonRequest = """
        {
            "id": $IDusuario
        }
    """.trimIndent()

        Log.d("lista listaOpFinanc", jsonRequest)

        try {

            val jsonResponse = consultarMySQL(jsonRequest, rota, "POST")
            Log.d("RESPOSTA BRUTA listOpFinanceiras", jsonResponse)  // Log da resposta bruta

            val json = object : TypeToken<List<OpFinanc>>() {}.type
            val OpFinancList: List<OpFinanc> = Gson().fromJson(jsonResponse, json)

            Log.d("RESPOSTA BRUTA listOpFinanceiras", "${OpFinancList.size}")  // Log da resposta bruta

            OpFinancList.forEach { item ->
                val id: Int = item.id
                val descricao: String = item.descricao
                val tipoMovimento: String = item.tipo_movimento
                val valor = item.valor
                val data = item.data

                //formatando o nome do gasto
                val nomeGastoFormatado = FormatarNome().formatar(descricao)

                //formatando o a forma de pagamento
                val forma_pagamento_formatada = FormatarNome().formatar(tipoMovimento)

                // formatando o valor da Operação Financeira
                val formatacaoReal = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                val valorOpFinanc_Formatado = (formatacaoReal.format(valor)).toString()

                val itemOpFinanc = OperacaoFinanceira(id, descricao, tipoMovimento, valorOpFinanc_Formatado, data)

                listaOpFinanc += itemOpFinanc

            }

        } catch (e: IOException) {
            Log.e("ERRO listOpFinanceiras", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO listOpFinanceiras", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO listOpFinanceiras", "Erro inesperado: ${e.message}")
        }

        listaOpFinanc.forEach { item ->
            Log.d("listOpFinanceiras", "${item}")
        }

        return listaOpFinanc.toList()
    }

//--------------------------------------------------------------------------------------------------------------------------------//

    fun getUltimaAtualizacaoListas_MySQL(IDusuario: Int, consultarLista: String): LocalDateTime {
        var timesTamp: LocalDateTime = LocalDateTime.MIN

        val jsonRequest = """
        {
            "id": $IDusuario,
            "nome_tabela": "$consultarLista"
        }
    """.trimIndent()

        try {
            val jsonResponse = consultarMySQL(jsonRequest, "verificar_atualizacao_tabela", "POST")
            Log.d("RESPOSTA BRUTA UltimaAtualizacaoListas", jsonResponse)  // Log da resposta bruta

            // Cria um objeto JSONObject a partir da string JSON
            val jsonObject = JSONObject(jsonResponse)

            // Extraindo o valor da chave "message"
            val ultimaAtualizacaoListasResponse = jsonObject.getString("message")

            // Verificando a mensagem da resposta
            if (ultimaAtualizacaoListasResponse != "Busca nao realizada. Verifique se o ID do usuario esta correto.") {
                // Convertendo a string da data para LocalDateTime
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                timesTamp = LocalDateTime.parse(ultimaAtualizacaoListasResponse, formatter)
            }

            Log.d("RESPOSTA FLASK", "$ultimaAtualizacaoListasResponse - $timesTamp")

        } catch (e: IOException) {
            Log.e("ERRO UltimaAtualizacaoListas", "IOException: ${e.message}")
        } catch (e: JsonSyntaxException) {
            Log.e("ERRO UltimaAtualizacaoListas", "Erro ao analisar o JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("ERRO UltimaAtualizacaoListas", "Erro inesperado: ${e.message}")
        }

        return timesTamp
    }


}