package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosMetasFinanceiras_Usuario_BD_Debts
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.ConsultaBD_MySQL.AgendarConsulta_MySQL
import com.example.debts.ConsultaBD_MySQL.CompararListas_MySQL_SQLite
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.layout_Item_lista.ItemSpacingDecoration
import com.example.debts.lista_DebtMap.adapter_DebtMap
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class tela_DebtMap : AppCompatActivity() {

    var resultado = ""

    //manipulando data
    var calendar = Calendar.getInstance() // Cria uma instância de Calendar
    var diaAtual = calendar.get(Calendar.DAY_OF_MONTH) //pegando o dia do mes atual
    val anoAtual = calendar.get(Calendar.YEAR) //pegando o ano atual

    // Obtém o nome do mês atual para exibição
    var nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())

    var dataFormatada = "${diaAtual} de ${nomeMes} de ${anoAtual}" // formata a data

    val listaMetas_STR: List<String> = listOf(
        "economizar R$ 1000,00 para uma viagem",
        "quitar uma dívida de R$ 500,00",
        "acumular R$ 2000,00 em fundo de emergência",
        "investir R$ 300,00 por mês em ações",
        "economizar R$ 1500,00 para a compra de um novo eletrônico"
    )

    val listaMetas2_STR: List<String> = listOf(
        "pagar R$ 800,00 de despesas médicas",
        "construir uma reserva de R$ 1200,00 para o futuro",
        "reduzir o saldo da dívida do cartão de crédito em R$ 600,00",
        "aumentar a poupança em R$ 1000,00 até o fim do ano",
        "economizar R$ 700,00 para um curso de aprimoramento profissional"
    )

    var listasMetas_STR: MutableList<String> = mutableListOf()

    //chama a função para converter uma lista de metas do tipo "String" para o tipo "dados_listaMeta_Item_DebtMap"
    //val listaMetas = DadosMetasFinanceiras_Usuario_BD_Debts().converter_Lista_MetasFinanceiras(listaMetas_STR)

    //chama a função para converter uma lista de metas do tipo "String" para o tipo "dados_listaMeta_Item_DebtMap"
    //val listaMetas2 = DadosMetasFinanceiras_Usuario_BD_Debts().converter_Lista_MetasFinanceiras(listaMetas2_STR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_debt_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listasMetas_STR += listasMetas_STR
        listasMetas_STR += listaMetas2_STR

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        //----------------------- config. lista de items DebtMap ---------------------------------//

        //chama a função para criar uma lista de dados do tipo "dados_listaMeta_DebtMap"
        //val listaDados1 = DadosMetasFinanceiras_Usuario_BD_Debts().criarItemDebtMap("Meta Cartão A", dataFormatada, listaMetas)
        //val listaDados2 = DadosMetasFinanceiras_Usuario_BD_Debts().criarItemDebtMap("Meta Cartão B", dataFormatada, listaMetas2)

        //lista que contem todos os items do DebtMap
        //val listaDados = listaDados1 + listaDados2
        val listaDados = BancoDados(this).listarMetas(IDusuario)

        val listaMetasNovas: RecyclerView = findViewById(R.id.listaMetasNovas)
        val listaMetasEmProgresso: RecyclerView = findViewById(R.id.lista_Items_DebtMap)
        val listaMetasConcluidas: RecyclerView = findViewById(R.id.listaMetasConcluidas)

        //lista de dados dos campos metas
        val listaDadosMetasNovas: MutableList<dados_listaMeta_DebtMap> = mutableListOf()
        val listaDadosMetasEmProgresso: MutableList<dados_listaMeta_DebtMap> = mutableListOf()
        val listaDadosMetasConcluidas: MutableList<dados_listaMeta_DebtMap> = mutableListOf()

        listaDados.mapIndexedNotNull { index, item ->


            if (item.progressoMeta == 0f) {
                Log.d("Lista Metas Novas", "${item}")

                listaDadosMetasNovas.add(item)
            }

            else if (item.progressoMeta > 0f && item.progressoMeta < 100f) {
                Log.d("Lista Metas em progresso", "${item}")

                listaDadosMetasEmProgresso.add(item)
            }

            else {
                Log.d("Lista Metas em concluidas", "${item}")

                listaDadosMetasConcluidas.add(item)
            }
        }

        //------------- configurando o layout manager do listaMetasNovas -------------------------//
        listaMetasNovas.layoutManager = LinearLayoutManager(this)
        listaMetasNovas.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        listaMetasNovas.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        val adapterListaMetasNovas = adapter_DebtMap(listaDadosMetasNovas, this)

        // Crie o adaptador para o RecyclerView
        listaMetasNovas.adapter = adapterListaMetasNovas

        //--------------- configurando o layout manager do listaMetasEmProgresso -----------------//
        listaMetasEmProgresso.layoutManager = LinearLayoutManager(this)
        listaMetasEmProgresso.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        listaMetasEmProgresso.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        val adapterListaMetasEmProgresso = adapter_DebtMap(listaDadosMetasEmProgresso, this)

        // Crie o adaptador para o RecyclerView
        listaMetasEmProgresso.adapter = adapterListaMetasEmProgresso

        //----------- configurando o layout manager do listaMetasConcluidas ----------------------//
        listaMetasConcluidas.layoutManager = LinearLayoutManager(this)
        listaMetasConcluidas.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        listaMetasConcluidas.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        val adapterListaMetasConcluidas = adapter_DebtMap(listaDadosMetasConcluidas, this)

        // Crie o adaptador para o RecyclerView
        listaMetasConcluidas.adapter = adapterListaMetasConcluidas

        //-------------------- config. botão de voltar do celular --------------------------------//

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de login
        val voltarTelaPrincial = Intent(this, telaPrincipal::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaPrincial)
                finish()
            }
        })
    }

    //função para voltar a tela inicial do aplicativo
    fun voltarTelaInicial(v: View){

        val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
        startActivity(navegarTelaPrincipal)
        finish()
    }

    //salvas as alterações feitas nas metas quando a activity for destruida
    override fun onDestroy() {
        super.onDestroy()

        CustomToast().showCustomToast(this@tela_DebtMap, "Atualizando Metas...")

        val IDusuario = DadosUsuario_BD_Debts(this@tela_DebtMap).pegarIdUsuario()

        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {

                val listaAtualizarMetas = CompararListas_MySQL_SQLite(this@tela_DebtMap).atualizarMetas(DadosUsuario_BD_Debts.listas_MySQL.metasUsuario, BancoDados(this@tela_DebtMap).listarMetas(IDusuario))

                if (listaAtualizarMetas) {
                    resultado = "Metas MySQL atualizadas"
                } else {
                    resultado = "Nenhuma meta atualizada"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Erro salvar alteração meta", "${e.message}")

                resultado = "Erro salvar alteração meta: ${e.message}"
            } finally {

                // Atualizar a UI no thread principal
                runOnUiThread {
                    Log.d("lista metas atualizada", resultado)
                    CustomToast().showCustomToast(this@tela_DebtMap, "Metas MySQL Atualizadas")
                }

                executorService.shutdown()
            }
        }
        /*

                        CustomToast().showCustomToast(this@tela_DebtMap, "Atualizando Metas...")

                var resultado = ""

                val IDusuario = DadosUsuario_BD_Debts(this@tela_DebtMap).pegarIdUsuario()

                val msgCarregando = MensagemCarregando(this@tela_DebtMap)

                msgCarregando.mostrarMensagem()

                val executorService: ExecutorService = Executors.newSingleThreadExecutor()
                executorService.execute {
                    try {

                        val listaAtualizarMetas = CompararListas_MySQL_SQLite(this@tela_DebtMap).atualizarMetas(DadosUsuario_BD_Debts.listas_MySQL.metasUsuario, BancoDados(this@tela_DebtMap).listarMetas(IDusuario))

                        listaAtualizarMetas.forEach { meta ->
                            val idMeta = meta.idMeta.toInt()

                            // Converte os estados da lista de metas
                            val listaMetaEstados: MutableList<Boolean> = meta.listaMetas_Item.map { it.isChecked }.toMutableList()

                            val progressoMeta = meta.progressoMeta

                            resultado = Metodos_BD_MySQL().atualizarMeta(IDusuario, idMeta, listaMetaEstados, progressoMeta)

                            Log.d("RESULTADO METAS ATUALIZADAS 2", "$listaAtualizarMetas")

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("Erro salvar alteração meta", "${e.message}")

                        resultado = "Erro salvar alteração meta: ${e.message}"
                    } finally {

                        // Atualizar a UI no thread principal
                        runOnUiThread {
                            msgCarregando.ocultarMensagem()

                            Log.d("lista metas atualizada", resultado)
                            CustomToast().showCustomToast(this@tela_DebtMap, "Metas MySQL Atualizadas")


                        }

                        executorService.shutdown()
                    }
                }

        */

    }
}