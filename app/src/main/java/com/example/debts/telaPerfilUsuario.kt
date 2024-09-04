package com.example.debts

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.Conexao_BD.DadosFinanceiros_Usuario_BD_Debts
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.layoutExpandivel.criarListaItems
import com.example.debts.layout_Item_lista.ItemSpacingDecoration
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.layout_Item_lista.MyData
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.text.NumberFormat
import java.util.Locale

class telaPerfilUsuario : AppCompatActivity() {

    private val listaEntradas = DadosFinanceiros_Usuario_BD_Debts().pegarListaEntradasMes()

    private val listaGastos = DadosFinanceiros_Usuario_BD_Debts().pegarListaGastosMes() + DadosFinanceiros_Usuario_BD_Debts().pegarListaDespesasMes()

    private val listaCoresPieChart = listOf(
        android.graphics.Color.rgb(109, 251, 114),
        android.graphics.Color.rgb(255, 50, 50),
        android.graphics.Color.rgb(255, 226, 11)
    )

    //função para formatar numeros float para o formato Real(R$)
    fun formatToCurrency(value: Float): String =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_perfil_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //-------------- config. lista de items Despesas Recentes --------------------------------//

        val listaDespesasRecentes: RecyclerView = findViewById(R.id.listaDespesasRecentes)

        //configurando o layout manager
        listaDespesasRecentes.layoutManager = LinearLayoutManager(this)
        listaDespesasRecentes.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        listaDespesasRecentes.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        val adapter = MyConstraintAdapter(pegarDados(listaGastos))

        //adicionando os items na lista
        listaDespesasRecentes.adapter = adapter

        //--------------------- config. indicador de progresso circular --------------------------//
//        val indicadorProgressoCircular: CircularProgressBar = findViewById(R.id.circularProgressBar_TelaPerfilUsuario)
//        val txt_IndicadorProgresso: TextView = findViewById(R.id.txt_IndicadorProgresso)
//
//        var progressoAtual_IndicadorProgresso: Float = 50f
//
//        txt_IndicadorProgresso.text = "${String.format("%.0f", progressoAtual_IndicadorProgresso)}%" //formatado o texto do indicador de progresso
//
//        indicadorProgressoCircular.apply {
//            progressMax = 100f //define o tamanho max do indicador de progresso
//            setProgressWithAnimation(progressoAtual_IndicadorProgresso, 1000) //indica o progresso
//        }

        //--------------------- config. Texto Relatoiro Resumo Mes -------------------------------//
        val somarItemsListaEntradas = somarValoresCampo(pegarDados(listaEntradas))
        val somarItemsListaGastos = somarValoresCampo(pegarDados(listaGastos))
        val valorTotal = somarItemsListaEntradas - somarItemsListaGastos

        val txtValorOrçamento: TextView = findViewById(R.id.txtValor_Orcamento)
        val txtValorDespesasMes: TextView = findViewById(R.id.txt_valorDespesasMes)
        val txtValorTotal: TextView = findViewById(R.id.txtValorTotal)

        txtValorOrçamento.text = "${formatToCurrency(somarItemsListaEntradas)}"
        txtValorDespesasMes.text = "${formatToCurrency(somarItemsListaGastos)}"
        txtValorTotal.text = "${formatToCurrency(valorTotal)}"

        //-------------------------- config. grafico pizza ---------------------------------------//

        val graficoPizza: PieChart = findViewById(R.id.pieChart)

        var listaDados = mutableListOf(
            PieEntry(somarItemsListaEntradas, ""),
            PieEntry(somarItemsListaGastos, ""),
            PieEntry(valorTotal, "")
        )

        val pieDataSet: PieDataSet = PieDataSet(listaDados, "")
        pieDataSet.setColors(listaCoresPieChart) //definindo as cores do grafico de pizza

        graficoPizza.legend.isEnabled = false // Desativar a legenda
        graficoPizza.description.isEnabled = false // Desativar a descrição que aparece no canto inferior direito do grafico

        graficoPizza.setUsePercentValues(true) //tornar os valores do grafico em porcentagem

        val pieData: PieData = PieData(pieDataSet)
        pieData.setValueTextSize(12f) // Define o tamanho do texto
        pieData.setValueFormatter(PercentFormatter(graficoPizza)) // Formata os valores como porcentagem

        graficoPizza.data = pieData
        graficoPizza.invalidate()

        //--------------------------- config. navegação da pagina --------------------------------//
        val btn_Configuracoes: Button = findViewById(R.id.btn_Configuracoes)

        btn_Configuracoes.setOnClickListener{
            val navegarConfiguracoesConta = Intent(this, configConta_Usuario::class.java)
            startActivity(navegarConfiguracoesConta)
            finish()
        }

        //------------------ config. texto do nome e email do usuario ----------------------------//

        val nomeUsuario: String = FormatarNome().formatar(DadosUsuario_BD_Debts(this).pegarNomeUsuario())
        val emailUsuario: String = DadosUsuario_BD_Debts(this).pegarEmailUsuario()

        val txt_NomeUsuario: TextView = findViewById(R.id.txt_NomeUsuario_Perfil)
        val txt_EmailUsuario: TextView = findViewById(R.id.txt_emailUsuario_Perfil)

        txt_NomeUsuario.text = nomeUsuario
        txt_EmailUsuario.text = emailUsuario

        //-------------------- config. botão de voltar do celular --------------------------------//

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela principal
        val voltarTelaPrincial = Intent(this, telaPrincipal::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaPrincial)
                finish()
            }
        })
    }

    //função que pega os dados do BD para colocar nas listas de items
    private fun pegarDados(listaItems: List<MyData> = emptyList()): List<MyData> {

        // Lista que será preenchida com os itens formatados
        val items: MutableList<MyData> = mutableListOf()

        // Itera sobre cada item da lista de entrada e cria novos itens formatados
        listaItems.forEach { myData ->
            // Adiciona um novo item à lista 'items' com os valores formatados
            items.add(
                MyData(
                    myData.Descr_Compra,  // Descrição da compra
                    myData.forma_pagamento,  // Forma de pagamento
                    formatToCurrency(myData.valor_compra.toFloat()),  // Valor formatado
                    myData.data_compra  // Data formatada
                )
            )
        }

        return items
    }

    //função para somar o valor total dos gastos ou custos do campo
    private fun somarValoresCampo(listaItems: List<MyData> = emptyList()): Float {

        // Mapeia cada item para o valor numérico após remover caracteres de formatação
        val valorTotal = listaItems
            .map {
                it.valor_compra
                    .replace("R$", "")  // Remove símbolo de moeda
                    .replace(".", "")   // Remove separador de milhares
                    .replace(",", ".") // Substitui vírgula por ponto decimal
                    .trim()
                    .toFloat()
            }
            .sum()  // Soma todos os valores

        return valorTotal
    }

    //função para voltar a tela inicial do aplicativo
    fun voltarTelaInicial(v: View){
        val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
        startActivity(navegarTelaPrincipal)
        finish()
    }
}