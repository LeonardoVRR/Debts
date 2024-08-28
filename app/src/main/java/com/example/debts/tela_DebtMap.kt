package com.example.debts

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    val listaMetas = listOf(
        dados_listaMeta_Item_DebtMap("economizar R$ 1000,00 para uma viagem"),
        dados_listaMeta_Item_DebtMap("quitar uma dívida de R$ 500,00"),
        dados_listaMeta_Item_DebtMap("acumular R$ 2000,00 em fundo de emergência"),
        dados_listaMeta_Item_DebtMap("investir R$ 300,00 por mês em ações"),
        dados_listaMeta_Item_DebtMap("economizar R$ 1500,00 para a compra de um novo eletrônico")
    )

    val listaMetas2 = listOf(
        dados_listaMeta_Item_DebtMap("pagar R$ 800,00 de despesas médicas"),
        dados_listaMeta_Item_DebtMap("construir uma reserva de R$ 1200,00 para o futuro"),
        dados_listaMeta_Item_DebtMap("reduzir o saldo da dívida do cartão de crédito em R$ 600,00"),
        dados_listaMeta_Item_DebtMap("aumentar a poupança em R$ 1000,00 até o fim do ano"),
        dados_listaMeta_Item_DebtMap("economizar R$ 700,00 para um curso de aprimoramento profissional")
    )

    val listaDados = listOf(
        dados_listaMeta_DebtMap("Meta Cartão A", dataFormatada, listaMetas),
        dados_listaMeta_DebtMap("Meta Cartão B", dataFormatada, listaMetas2)
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_debt_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //----------------------- config. lista de items DebtMap ---------------------------------//
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
    }
}