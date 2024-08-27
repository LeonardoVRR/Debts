package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.layoutExpandivel.criarListaItems
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.layout_Item_lista.MyData
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.NumberFormat
import java.util.Locale

class telaPerfilUsuario : AppCompatActivity() {

    private val listaEntradas = listOf(
        MyData("Salario", "PIX", 1200.toString(), "25/08/2024"),
        MyData("Venda Monitor", "PIX", 2000.toString(), "05/03/2020")
    )

    private val listaGastos = listOf(
        MyData("Shopping ElDorado", "Crédito", 200.5.toString(), "17/02/2022"),
        MyData("McDonalds", "Crédito", 60.5.toString(), "17/02/2021"),
        MyData("McDonalds", "Crédito", 60.5.toString(), "17/02/2021"),
        MyData("McDonalds", "Crédito", 60.5.toString(), "17/02/2021")
    )

    private val listaCoresPieChart = listOf(
        android.graphics.Color.rgb(109, 251, 114),
        android.graphics.Color.rgb(255, 50, 50),
        android.graphics.Color.rgb(255, 226, 11)
        )

    private val resumoMes = listOf(
        10f
    )

    private val listaDespesasRecentes = listOf("")

    //função para formatar numeros float para o formato Real(R$)
    fun formatToCurrency(value: Float): String =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_perfil_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listaDespesasRecentes: RecyclerView = findViewById(R.id.listaDespesasRecentes)

        //configurando o layout manager
        listaDespesasRecentes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Crie o adaptador para o RecyclerView
        val adapter = MyConstraintAdapter(pegarDados(listaGastos))

        //adicionando os items na lista
        listaDespesasRecentes.adapter = adapter

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

        val pieData: PieData = PieData(pieDataSet)
        graficoPizza.data = pieData
        graficoPizza.invalidate()

        //--------------------------- config. navegação da pagina --------------------------------//
        val btn_Configuracoes: Button = findViewById(R.id.btn_Configuracoes)

        btn_Configuracoes.setOnClickListener{
            val navegarConfiguracoesConta = Intent(this, configConta_Usuario::class.java)
            startActivity(navegarConfiguracoesConta)
            finish()
        }
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