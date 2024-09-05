package com.example.debts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosMetasFinanceiras_Usuario_BD_Debts
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.layout_Item_lista.ItemSpacingDecoration
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.lista_DebtMap.adapter_DebtMap
import com.example.debts.lista_DebtMap.dados_listaMeta_DebtMap
import com.example.debts.lista_DebtMap.dados_listaMeta_Item_DebtMap
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class tela_DebtMap : AppCompatActivity() {

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
    val listaMetas = DadosMetasFinanceiras_Usuario_BD_Debts().converter_Lista_MetasFinanceiras(listaMetas_STR)

    //chama a função para converter uma lista de metas do tipo "String" para o tipo "dados_listaMeta_Item_DebtMap"
    val listaMetas2 = DadosMetasFinanceiras_Usuario_BD_Debts().converter_Lista_MetasFinanceiras(listaMetas2_STR)

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

        val lista_Items_DebtMap: RecyclerView = findViewById(R.id.lista_Items_DebtMap)

        //configurando o layout manager
        lista_Items_DebtMap.layoutManager = LinearLayoutManager(this)
        lista_Items_DebtMap.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        lista_Items_DebtMap.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        val adapterItem = adapter_DebtMap(listaDados, this)

        // Crie o adaptador para o RecyclerView
        lista_Items_DebtMap.adapter = adapterItem

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
}