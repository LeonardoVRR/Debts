package com.example.debts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
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

        //array que vai conter todas as colunas
        val entries = mutableListOf<BarEntry>()

        //configurando os botões p/ trocar o mes do grafico
        val btn_PrevMes: ImageButton = findViewById(R.id.btn_PrevMes)
        val btn_ProxMes: ImageButton = findViewById(R.id.btn_ProxMes)

        //manipulando data
        var calendar = Calendar.getInstance() // Cria uma instância de Calendar
        val anoAtual = calendar.get(Calendar.YEAR) //pegando o ano atual
        var mesAtual = calendar.get(Calendar.MONTH) //pegando o mes atual

        // Configura o Calendar para o primeiro dia do mês atual
        calendar.set(anoAtual, mesAtual, 1)

        // Obtém o número de dias no mês atual
        var qtdDiasMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Obtém o nome do mês atual para exibição
        var nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())

        var dataInicial = calendar.time

        // Define uma formação para a data
        val formataData = SimpleDateFormat("dd/MM", Locale("pt", "BR"))
        var dataFormatada = formataData.format(dataInicial) // usa a formatação definida

        //configurando para mostra o nome do mes atual
        val viewNomeMes: TextView = findViewById(R.id.graf_MesAtual)
        viewNomeMes.text = nomeMes.uppercase()

        //array que contem todas as legendas das colunas
        var legendaColunas = criarLegendas(qtdDiasMes, formataData, calendar)

        //quando o btn_ProxMes for clicado vai chamar uma função para avançar o mes
        btn_ProxMes.setOnClickListener {
            // Avança para o próximo mês
            calendar.add(Calendar.MONTH, 1)
            //retroce um mes mês
            calendar.add(Calendar.MONTH, -1)

            // Define o dia do mês para 1
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            //atualizando as informações do calendario
            qtdDiasMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())
            viewNomeMes.text = nomeMes.uppercase()

            // Verifica se a lista "entries" tem elementos
            if (entries.isNotEmpty() && legendaColunas.isNotEmpty()) {
//                Toast.makeText(
//                    this,
//                    "Atualizando",
//                    Toast.LENGTH_SHORT
//                ).show()

                entries.clear() // Esvazia a lista
                legendaColunas = arrayOf() // Esvazia a lista
                criarColunasGraf(entries, qtdDiasMes)
                legendaColunas = criarLegendas(qtdDiasMes, formataData, calendar)

                //atualiza as legendas do grafico
                val xAxis = grafico.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(legendaColunas)

                grafico.notifyDataSetChanged() // Atualiza o grafico quando recebe novos dados
                grafico.invalidate() // Atualiza o gráfico
            }
        }

        //quando o btn_PrevMes for clicado vai chamar uma função para retroceder o mes
        btn_PrevMes.setOnClickListener{

            // Retrocede para o mês anterior
            calendar.add(Calendar.MONTH, -1)
            calendar.add(Calendar.MONTH, -1)

            // Define o dia do mês para 1
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            qtdDiasMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())
            viewNomeMes.text = nomeMes.uppercase()

            // Verifica se a lista "entries" tem elementos
            if (entries.isNotEmpty() && legendaColunas.isNotEmpty()) {
//                Toast.makeText(
//                    this,
//                    "Atualizando",
//                    Toast.LENGTH_SHORT
//                ).show()

                entries.clear() // Esvazia a lista
                legendaColunas = arrayOf() // Esvazia a lista
                criarColunasGraf(entries, qtdDiasMes)
                legendaColunas = criarLegendas(qtdDiasMes, formataData, calendar)

                //atualiza as legendas do grafico
                val xAxis = grafico.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(legendaColunas)

                grafico.notifyDataSetChanged() // Atualiza o grafico quando recebe novos dados
                grafico.invalidate() // Atualiza o gráfico
            }

        }

        //Obtendo a refencia do grafico
        grafico = findViewById(R.id.bar_chart)

        //chama a função para criar as colunas do grafico
        criarColunasGraf(entries, qtdDiasMes)

        //Estilizando o grafico
        val barDataSet1 = BarDataSet(entries, "")

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

        //definindo o zoom inicial do grafico no eixo X
        grafico.zoom(5f, 1f, 0f, 0f)

        grafico.viewPortHandler.setMaximumScaleX(9f) // Define o zoom máximo no eixo X
        grafico.viewPortHandler.setMinimumScaleX(5f) // Define o zoom minimo no eixo X
        xAxis.setDrawGridLines(false) //não deixa desenhar a linha de grade no eixo X

//        Toast.makeText(
//            this,
//            "Colunas: ${barDataSet1.entryCount}",
//            Toast.LENGTH_SHORT
//        ).show()

        //configurando as legendas das colunas do grafico
        xAxis.position = XAxis.XAxisPosition.BOTTOM // exibe as legendas na parte de baixo do grafico
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 45f //rotaciona as legendas em 45 graus
        xAxis.valueFormatter = IndexAxisValueFormatter(legendaColunas) //adiciona as legendas nas colunas do grafico no eixo X

        //aplicando a formatação criada em "valueFormatter" nos textos que ficam acima de cada coluna
        barDataSet1.valueFormatter = createValueFormatter()

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

    //função que gera numeros do tipo float aleatorios
    fun randomFloat(min: Float, max: Float): Float {
        return Random.nextFloat() * (max - min) + min
    }

    // Função que retorna os textos acima das colunas formatados
    fun createValueFormatter(): ValueFormatter {
        return object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                val value = barEntry.y // Recebe os valores que ficam acima de cada coluna
                val valueFormatter = "R$ ${String.format("%.2f", value)}" // Formata os valores acima das colunas

                return when {
                    value < 20 -> { // caso o valor da coluna seja menor que 20 é possivel alterar a formatação
                        "$valueFormatter"
                    }
                    else -> {
                        "$valueFormatter"
                    }
                }
            }
        }
    }

    //função que cria as colunas do grafico
    private fun criarColunasGraf(entries: MutableList<BarEntry>, qtdDiasMes: Int) {
        for (i in 0..qtdDiasMes-1) {
            val valorAleatorio = randomFloat(1.0f, 1001.0f)

            entries.add(BarEntry(i.toFloat(), valorAleatorio)) //adicionando as colunas no array
        }
    }

    // função que cria as legendas das colunas
    private fun criarLegendas(qtdDiasMes: Int, formataData: SimpleDateFormat, calendar: Calendar): Array<String>{
        val legendaColunas = mutableListOf<String>() // Lista mutável para armazenar as legendas

        //adiciona uma legenda para cada coluna do grafico
        for (i in 1..qtdDiasMes) {
            var dataFormatada = formataData.format(calendar.time) //pega o dia do mes e formata ele

            legendaColunas += dataFormatada //adiciona as datas formatadas no array

            calendar.add(Calendar.DAY_OF_MONTH, 1) //avançar os dias no mes
        }

        return legendaColunas.toTypedArray() // Converte a lista mutável para um array
    }

}