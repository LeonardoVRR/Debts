package com.example.debts

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class tela_RelatorioGastos : AppCompatActivity() {
    private lateinit var grafico: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_relatorio_gastos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //manipulando data
        val calendar = Calendar.getInstance() // Cria uma instância de Calendar
        val anoAtual = calendar.get(Calendar.YEAR) //pegando o ano atual
        val mesAtual = calendar.get(Calendar.MONTH) //pegando o mes atual
        calendar.set(anoAtual, mesAtual, 1) // Configura o Calendar para o primeiro dia do mês atual
        var dataInicial = calendar.time

        // Obtém o nome do mês atual para exibição
        val nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())

        //configurando para mostra o nome do mes atual
        val viewNomeMes: TextView = findViewById(R.id.graf_MesAtual)
        viewNomeMes.text = nomeMes

        // Obtém o número de dias no mês atual
        val qtdDiasMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        grafico = findViewById(R.id.bar_chart)

        var entries = mutableListOf<BarEntry>()

        //criando colunas do grafico
        for (i in 0..qtdDiasMes-1) {
            val valorAleatorio = randomFloat(1.0f, 1001.0f)

            entries.add(BarEntry(i.toFloat(), valorAleatorio))
        }

//        val entries = listOf(
//            BarEntry(0f, 10f),
//            BarEntry(1f, 20f),
//            BarEntry(2f, 30f),
//            BarEntry(3f, 15f),
//            BarEntry(4f, 25f),
//            BarEntry(5f, 35f),
//            BarEntry(6f, 40f),
//            BarEntry(7f, 50f),
//            BarEntry(8f, 22f),
//            BarEntry(9f, 33f),
//            BarEntry(10f, 45f),
//            BarEntry(11f, 55f),
//            BarEntry(12f, 60f),
//            BarEntry(13f, 12f),
//            BarEntry(14f, 18f),
//            BarEntry(15f, 23f),
//            BarEntry(16f, 28f),
//            BarEntry(17f, 32f),
//            BarEntry(18f, 47f),
//            BarEntry(19f, 52f),
//            BarEntry(20f, 65f),
//            BarEntry(21f, 70f),
//            BarEntry(22f, 38f),
//            BarEntry(23f, 44f),
//            BarEntry(24f, 57f),
//            BarEntry(25f, 63f),
//            BarEntry(26f, 72f),
//            BarEntry(27f, 68f),
//            BarEntry(28f, 1000f),
//            BarEntry(29f, 1200f),
//            BarEntry(30f, 1800f)
//        )

        val colorColumnsGraf = intArrayOf(
            android.graphics.Color.RED,
            android.graphics.Color.YELLOW,
            android.graphics.Color.GREEN
        )

        //Estilizando o grafico
        var barDataSet1 = BarDataSet(entries, "")

        //colorindo as colunas do grafico
        barDataSet1.color = android.graphics.Color.rgb(255, 50, 50) // colorindo as colunas da tabela de vermelho

        // Desativando o fundo de grade e rotulos que aparecem nos eixos X e Y
        val xAxis = grafico.xAxis

        xAxis.isEnabled = true // Desativa o eixo X
        xAxis.setDrawLabels(true) // Esconde os rótulos do eixo X

        val yAxisLeft = grafico.axisLeft
        yAxisLeft.isEnabled = false // Desativa o eixo Y esquerdo
        yAxisLeft.setDrawLabels(false) // Esconde os rótulos do eixo Y esquerdo

        val yAxisRight = grafico.axisRight
        yAxisRight.isEnabled = false // Desativa o eixo Y direito
        yAxisRight.setDrawLabels(false) // Esconde os rótulos do eixo Y direito

        // Configurar o tamanho do texto dos valores nas colunas
        barDataSet1.valueTextSize = 14f // Exemplo: Tamanho 14sp

        // Configurando mensagem caso o grafico ainda não tenha nenhuma entrada
        grafico.setNoDataText("Mês sem entradas.")

        //estilizando a mensagem caso o grafico ainda não tenha nenhuma entrada
        val paint = grafico.getPaint(BarChart.PAINT_INFO)
        paint.textSize = 70f
        paint.color = android.graphics.Color.RED

        //definindo o espaçamento das colunad da tabela
        //barDataSet1.barBorderWidth

        //config
        //xAxis.granularity = 1f
        //xAxis.labelCount = entries.size
        //xAxis.isGranularityEnabled = true

        //definindo o zoom inicial do grafico no eixo X
        grafico.zoom(5f, 1f, 0f, 0f)

        grafico.viewPortHandler.setMaximumScaleX(9f) // Define o zoom máximo no eixo X
        grafico.viewPortHandler.setMinimumScaleX(5f) // Define o zoom minimo no eixo X
        xAxis.setDrawGridLines(false)

        //barDataSet1.setDrawValues(true)
        //grafico.setDrawValueAboveBar(true)

        Toast.makeText(
            this,
            "Colunas: ${barDataSet1.entryCount}",
            Toast.LENGTH_SHORT
        ).show()

        val formataData = SimpleDateFormat("dd/MM", Locale("pt", "BR")) // Define uma formação para a data
        val data = Date() // pega a data atual
        val dataFormatada = formataData.format(data); //formata a data conforme a formatação do "formataData"



        var legendaColunas: Array<String> = arrayOf() //array que vai conter todas as legendas das colunas

        val qtd_colunasGraf = barDataSet1.entryCount //conta quantas colunas tem no grafico

        //adiciona uma legenda para cada coluna do grafico
        for (i in 1..qtdDiasMes) {
            legendaColunas += formataData.format(dataInicial)

            // Avança para o próximo dia
            calendar.time = dataInicial
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            dataInicial = calendar.time
        }

        //configurando as legendas das colunas do grafico
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 45f
        xAxis.valueFormatter = IndexAxisValueFormatter(legendaColunas)

        //formatando o texto que aparece em cima das colunas da tabela
        val valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                val value = barEntry.y //recebe os valores que ficam acima de cada coluna
                val valueFormatter = "R$ ${String.format("%.2f", value)}" // formata os valores acima das colunas

                return when {
                    value < 20 -> {
                        "$valueFormatter"
                    }

                    else -> {
                        "$valueFormatter"
                    }
                }
            }
        }

        //aplicando a formatação criada em "valueFormatter" nos textos que ficam acima de cada coluna
        barDataSet1.valueFormatter = valueFormatter

        // Configurar o eixo X com labels personalizadas
        val dataSet = BarDataSet(dataValue(), "")
        val barData2 = BarData(dataSet)
        //grafico.data = barData2

        //grafico.setMaxVisibleValueCount(2)
        //grafico.setDrawValueAboveBar(true)

        grafico.legend.isEnabled = false // Desativar a legenda
        grafico.description.isEnabled = false // Desativar a descrição que aparece no canto inferior direito do grafico
        //grafico.isScaleXEnabled = false //bloqueando o zoom no eixo x no grafico
        grafico.isScaleYEnabled = false //bloqueando o zoom no eixo y no grafico
        grafico.isDragEnabled = true

        // Cria um Bardata com o barDataSet1
        val barData = BarData()
        barData.addDataSet(barDataSet1)

        grafico.data = barData // Define os dados no gráfico
        grafico.notifyDataSetChanged() // Atualiza o grafico quando recebe novos dados
        grafico.invalidate() // Atualiza o gráfico

    }

    private fun obterDataAtual() {

    }

    private fun dataValue(): ArrayList<BarEntry> {
        val dataVals = ArrayList<BarEntry>()

        dataVals.add(BarEntry(0f, 3f))
        dataVals.add(BarEntry(1f, 4f))
        dataVals.add(BarEntry(3f, 6f))
        dataVals.add(BarEntry(4f, 10f))

        return dataVals
    }

    fun randomFloat(min: Float, max: Float): Float {
        return Random.nextFloat() * (max - min) + min
    }

    fun create30Columns(): List<BarEntry> {
        val entries = mutableListOf<BarEntry>() // Inicializa a lista mutável de BarEntry

        // Cria 30 colunas para o gráfico
        for (i in 0 until 30) {
            // Adiciona uma nova entrada (BarEntry) para cada coluna
            // i.toFloat() define a posição da coluna no eixo X
            // (i * 10).toFloat() é um exemplo de valor no eixo Y, você pode substituir com seus valores
            entries.add(BarEntry(i.toFloat(), (i * 10).toFloat()))
        }

        return entries
    }
}