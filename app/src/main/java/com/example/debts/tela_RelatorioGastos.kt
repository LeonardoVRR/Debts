package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.Locale
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosFinanceiros_Usuario_BD_Debts
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.layoutExpandivel.criarListaItems
import com.example.debts.layoutExpandivel.removerListaItems
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.example.debts.models.MovintoDia
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.random.Random

class tela_RelatorioGastos : AppCompatActivity() {
    private lateinit var grafico: BarChart
    private var campoEntradas_isExpanded = false
    private var campoDespesas_isExpanded = false
    private var campoGastos_isExpanded = false

    private lateinit var recyclerViewManager: criarListaItems

    //função para formatar numeros float para o formato Real(R$)
    fun formatToCurrency(value: Float): String =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_relatorio_gastos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        val listaEntradas = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaEntradasMes()

        //private val listaDespesas = DadosFinanceiros_Usuario_BD_Debts().pegarListaDespesasMes()

        val listaGastos = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaGastosMes()

        //id do usuario logado
        val usuarioID = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        // Inicializar o RecyclerViewManager
        recyclerViewManager = criarListaItems(this)

        //array que vai conter todas as colunas
        val entries = mutableListOf<BarEntry>()

        //configurando os botões p/ trocar o mes do grafico
        val btn_PrevMes: ImageButton = findViewById(R.id.btn_PrevMes)
        val btn_ProxMes: ImageButton = findViewById(R.id.btn_ProxMes)

        //manipulando data
        var calendar = Calendar.getInstance() // Cria uma instância de Calendar
        var anoAtual = calendar.get(Calendar.YEAR) //pegando o ano atual
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
        viewNomeMes.text = "${nomeMes.uppercase()} $anoAtual"

        //array que contem todas as legendas das colunas
        var legendaColunas = criarLegendas(qtdDiasMes, calendar)

        //lista que vai conter os gastos diarios do mes
        var listaValores: MutableList<Float> = BancoDados(this).gastosDiariosMes(nomeMes.lowercase(), usuarioID).toMutableList()

        //quando o btn_ProxMes for clicado vai chamar uma função para avançar o mes
        btn_ProxMes.setOnClickListener {
            // Avança para o próximo mês
            calendar.add(Calendar.MONTH, 1)
            //retroce um mes mês
            calendar.add(Calendar.MONTH, -1)

            // Define o dia do mês para 1
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            anoAtual = calendar.get(Calendar.YEAR)

            //atualizando as informações do calendario
            qtdDiasMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())
            viewNomeMes.text = "${nomeMes.uppercase()} $anoAtual"

            // Verifica se a lista "entries" tem elementos
            if (entries.isNotEmpty() && legendaColunas.isNotEmpty()) {
//                Toast.makeText(
//                    this,
//                    "Atualizando",
//                    Toast.LENGTH_SHORT
//                ).show()

                listaValores.clear() // Esvazia a lista
                entries.clear() // Esvazia a lista
                legendaColunas = arrayOf() // Esvazia a lista

                listaValores = BancoDados(this).gastosDiariosMes(nomeMes.lowercase(), usuarioID).toMutableList()
                criarColunasGraf(entries, qtdDiasMes, nomeMes, usuarioID)
                legendaColunas = criarLegendas(qtdDiasMes, calendar)

                Log.d("Gastos do Mes", "$listaValores")

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

            anoAtual = calendar.get(Calendar.YEAR)

            qtdDiasMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.getDefault())
            viewNomeMes.text = "${nomeMes.uppercase()} $anoAtual"

            // Verifica se a lista "entries" tem elementos
            if (entries.isNotEmpty() && legendaColunas.isNotEmpty()) {
//                Toast.makeText(
//                    this,
//                    "Atualizando",
//                    Toast.LENGTH_SHORT
//                ).show()

                listaValores.clear() // Esvazia a lista
                entries.clear() // Esvazia a lista
                legendaColunas = arrayOf() // Esvazia a lista

                listaValores = BancoDados(this).gastosDiariosMes(nomeMes.lowercase(), usuarioID).toMutableList()
                criarColunasGraf(entries, qtdDiasMes, nomeMes, usuarioID)
                legendaColunas = criarLegendas(qtdDiasMes, calendar)

                Log.d("Gastos do Mes", "$listaValores")

                //atualiza as legendas do grafico
                val xAxis = grafico.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(legendaColunas)

                grafico.notifyDataSetChanged() // Atualiza o grafico quando recebe novos dados
                grafico.invalidate() // Atualiza o gráfico
            }

        }

        //----------------------- config. grafico de colunas -------------------------------------//

        //Obtendo a refencia do grafico
        grafico = findViewById(R.id.bar_chart)

        //chama a função para criar as colunas do grafico
        criarColunasGraf(entries, qtdDiasMes, nomeMes, usuarioID)

        //Estilizando o grafico
        val barDataSet1 = BarDataSet(entries, "")

        //colorindo as colunas do grafico
        //barDataSet1.color = android.graphics.Color.rgb(255, 50, 50) // colorindo as colunas da tabela de vermelho

        //lista de cores das colunas
        val coresColunas = listOf<Int>(android.graphics.Color.rgb(255, 50, 50), android.graphics.Color.rgb(109, 251, 114))

        //colorindo as colunas do grafico
        barDataSet1.setColors(coresColunas)

        // Definindo os rótulos para as legenda das cores do grafico
        barDataSet1.stackLabels = arrayOf("Despesas", "Rendimentos")

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

        grafico.legend.isEnabled = true // Desativar a legenda
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

        //------------------------------ configs layouts expansivos ------------------------------//

        //---------- Configurando o Campo Entradas ------------------------//
        val btnExp_Entradas: ImageButton = findViewById(R.id.btnExp_Entradas)
        val lytExp_Entradas: ConstraintLayout = findViewById(R.id.lytExp_Entradas)

        //obtendo os parametros da minha view "lytExp_Entradas" e as convertendo para `ConstraintLayout.LayoutParams` para poderem ser manipuladas
        val lytParams_Entradas = lytExp_Entradas.layoutParams as ConstraintLayout.LayoutParams

        //---------- Configurando o Campo Despesas ------------------------//
//        val btnExp_Despesas: ImageButton = findViewById(R.id.btnExp_Despesas)
//        val lytExp_Despesas: ConstraintLayout = findViewById(R.id.lytExp_Despesas)
//
//        //obtendo os parametros da minha view "lytExp_Despesas" e as convertendo para `ConstraintLayout.LayoutParams` para poderem ser manipuladas
//        val lytParams_Despesas = lytExp_Despesas.layoutParams as ConstraintLayout.LayoutParams

        //---------- Configurando o Campo Gastos ------------------------//
        val btnExp_Gastos: ImageButton = findViewById(R.id.btnExp_Gastos)
        val lytExp_Gastos: ConstraintLayout = findViewById(R.id.lytExp_Gastos)

        //obtendo os parametros da minha view "lytExp_Gastos" e as convertendo para `ConstraintLayout.LayoutParams` para poderem ser manipuladas
        val lytParams_Gastos = lytExp_Gastos.layoutParams as ConstraintLayout.LayoutParams


        //------------------------ configs. botões dos campos ------------------------//
        btnExp_Entradas.setOnClickListener {

            if (campoEntradas_isExpanded) {
                // Aumentar a altura da view
                lytParams_Entradas.height = 500

                //trocar o icone do "btnExp_Entradas"
                btnExp_Entradas.setImageResource(R.drawable.arrow_up)

                //aplicando o aumento na view "lytExp_Entradas"
                lytExp_Entradas.layoutParams = lytParams_Entradas

                //trocando os icones dos botoes
                //btnExp_Despesas.setImageResource(R.drawable.arrow_down)
                btnExp_Gastos.setImageResource(R.drawable.arrow_down)

                //fechando os outros campos
//                lytParams_Despesas.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//                lytExp_Despesas.layoutParams = lytParams_Despesas

                lytParams_Gastos.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                lytExp_Gastos.layoutParams = lytParams_Gastos

                //chamando a função para remover a lista de items do campo
                //removerListaItems.removerListaItems(lytExp_Despesas)
                removerListaItems.removerListaItems(lytExp_Gastos)

                // Crie o adaptador para o RecyclerView
                val adapter = MyConstraintAdapter(listaEntradas)
                //chamando a função para criar a lista de items do campo
                recyclerViewManager.criarListaItems(lytExp_Entradas, adapter)
            } else {
                lytParams_Entradas.height = ConstraintLayout.LayoutParams.WRAP_CONTENT

                //trocar o icone do "btnExp_Entradas"
                btnExp_Entradas.setImageResource(R.drawable.arrow_down)

                //chamando a função para remover a lista de items do campo
                removerListaItems.removerListaItems(lytExp_Entradas)

                lytExp_Entradas.layoutParams = lytParams_Entradas
            }
            campoEntradas_isExpanded = !campoEntradas_isExpanded
        }

//        btnExp_Despesas.setOnClickListener {
//
//            if (campoDespesas_isExpanded) {
//                // Aumentar a altura da view
//                lytParams_Despesas.height = 500
//
//                campoEntradas_isExpanded = false
//                campoGastos_isExpanded = false
//
//                //trocar o icone do "btnExp_Despesas"
//                btnExp_Despesas.setImageResource(R.drawable.arrow_up)
//
//                //aplicando o aumento na view "lytExp_Despesas"
//                lytExp_Despesas.layoutParams = lytParams_Despesas
//
//                //trocando os icones dos botoes
//                btnExp_Entradas.setImageResource(R.drawable.arrow_down)
//                btnExp_Gastos.setImageResource(R.drawable.arrow_down)
//
//                //fechando os outros campos
//                lytParams_Entradas.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//                lytExp_Entradas.layoutParams = lytParams_Entradas
//
//                lytParams_Gastos.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//                lytExp_Gastos.layoutParams = lytParams_Gastos
//
//                //chamando a função para remover a lista de items do campo
//                removerListaItems.removerListaItems(lytExp_Entradas)
//                removerListaItems.removerListaItems(lytExp_Gastos)
//
//
//                // Crie o adaptador para o RecyclerView
//                val adapter = MyConstraintAdapter(pegarDados(listaDespesas))
//                //chamando a função para criar a lista de items do campo
//                recyclerViewManager.criarListaItems(lytExp_Despesas, adapter)
//            } else {
//                lytParams_Despesas.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//
//                //trocar o icone do "btnExp_Despesas"
//                btnExp_Despesas.setImageResource(R.drawable.arrow_down)
//
//                //chamando a função para remover a lista de items do campo
//                removerListaItems.removerListaItems(lytExp_Despesas)
//
//                lytExp_Despesas.layoutParams = lytParams_Despesas
//            }
//            campoDespesas_isExpanded = !campoDespesas_isExpanded
//        }

        btnExp_Gastos.setOnClickListener {

            if (campoGastos_isExpanded) {
                // Aumentar a altura da view
                lytParams_Gastos.height = 500

                campoEntradas_isExpanded = false
                campoDespesas_isExpanded = false

                //trocar o icone do "btnExp_Gastos"
                btnExp_Gastos.setImageResource(R.drawable.arrow_up)

                //aplicando o aumento na view "lytExp_Gastos"
                lytExp_Gastos.layoutParams = lytParams_Gastos

                //trocando os icones dos botoes
                btnExp_Entradas.setImageResource(R.drawable.arrow_down)
                //btnExp_Despesas.setImageResource(R.drawable.arrow_down)

                //fechando os outros campos
                lytParams_Entradas.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                lytExp_Entradas.layoutParams = lytParams_Entradas

                //lytParams_Despesas.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                //lytExp_Despesas.layoutParams = lytParams_Despesas

                //chamando a função para remover a lista de items do campo
                removerListaItems.removerListaItems(lytExp_Entradas)
                //removerListaItems.removerListaItems(lytExp_Despesas)

                // Crie o adaptador para o RecyclerView
                val adapter = MyConstraintAdapter(listaGastos)
                //chamando a função para criar a lista de items do campo
                recyclerViewManager.criarListaItems(lytExp_Gastos, adapter)
            } else {
                lytParams_Gastos.height = ConstraintLayout.LayoutParams.WRAP_CONTENT

                //trocar o icone do "btnExp_Gastos"
                btnExp_Gastos.setImageResource(R.drawable.arrow_down)

                //chamando a função para remover a lista de items do campo
                removerListaItems.removerListaItems(lytExp_Gastos)

                lytExp_Gastos.layoutParams = lytParams_Gastos
            }
            campoGastos_isExpanded = !campoGastos_isExpanded
        }

        //-------------------- config. somas dos gastos dos items de cada campo ------------------//

        val somarItemsListaEntradas = somarValoresCampo(listaEntradas)
        //val somarItemsListaDespesas = somarValoresCampo(pegarDados(listaDespesas))
        val somarItemsListaGastos = somarValoresCampo(listaGastos)

        val txt_valorEntradas: TextView = findViewById(R.id.txt_valorEntradas)
        //val txt_valorDespesas: TextView = findViewById(R.id.txt_valorDespesas)
        val txt_valorGastos: TextView = findViewById(R.id.txt_valorGastos)

        txt_valorEntradas.text = "${formatToCurrency(somarItemsListaEntradas)}"
        //txt_valorDespesas.text = "${formatToCurrency(somarItemsListaDespesas)}"
        txt_valorGastos.text = "${formatToCurrency(somarItemsListaGastos)}"

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

    //função que gera numeros do tipo float aleatorios
    fun randomFloat(min: Float, max: Float): Float {
        return Random.nextFloat() * (max - min) + min
    }

    // Função que retorna os textos acima das colunas formatados
    fun createValueFormatter(): ValueFormatter {
        return object : ValueFormatter() {
            override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry): String {
                val stackedValues = stackedEntry.yVals

                // Se houver múltiplos valores (barra empilhada)
                return if (stackedValues != null) {
                    // Formata e exibe os valores empilhados individualmente
                    val despesas = stackedValues[0]
                    val rendimentos = stackedValues[1]

                    val despesasFormatado = if (despesas > 0) {
                        "R$ ${String.format(Locale("pt", "BR"), "%,.2f", despesas)}"
                    } else {
                        ""
                    }

                    val rendimentosFormatado = if (rendimentos > 0) {
                        "R$ ${String.format(Locale("pt", "BR"), "%,.2f", rendimentos)}"
                    } else {
                        ""
                    }

                    // Retorna o valor correto para a barra empilhada
                    when (value) {
                        despesas -> despesasFormatado
                        rendimentos -> rendimentosFormatado
                        else -> ""
                    }
                } else {
                    // Para o caso de uma única barra
                    if (value > 0) "R$ ${String.format(Locale("pt", "BR"), "%,.2f", value)}" else ""
                }
            }
        }
    }

    fun formatarValor(valorString: String): Float {
        // Remover o símbolo de moeda "R$", remover os espaços e os separadores de milhar.
        val valorLimpo = valorString
            .replace("R$", "") // Remove o símbolo "R$"
            .replace(".", "")  // Remove os separadores de milhar
            .replace(",", ".") // Substitui a vírgula decimal por ponto
            .trim()            // Remove espaços em branco extras

        // Converte a string limpa para Float
        return valorLimpo.toFloat()
    }

    //função que cria as colunas do grafico
    private fun criarColunasGraf(entries: MutableList<BarEntry>, qtdDiasMes: Int, nomeMes: String, usuarioID: Int) {
        entries.clear()

        // Recuperar a lista de valores do banco de dados
        val listaGastos = BancoDados(this).gastosDiariosMesGraf(nomeMes, usuarioID)
        val listaRendimentos = BancoDados(this).rendimentosDiariosMesGraf(nomeMes, usuarioID)

        // lista preenchida com zeros
        val listaGastosAjustados = MutableList(qtdDiasMes) { 0f }
        val listaRendimentosAjustadas = MutableList(qtdDiasMes) { 0f }

        listaGastos.forEach { item ->
            val dia = item.dia
            val valor = item.valor

            listaGastosAjustados.add(dia, valor)
        }

        listaRendimentos.forEach { item ->
            val dia = item.dia
            val valor = item.valor

            listaRendimentosAjustadas.add(dia, valor)
        }

        // Iterar e adicionar os valores ao gráfico
        for (i in 0 until qtdDiasMes) {
            val valorGasto = listaGastosAjustados[i]
            val valorEntrada = listaRendimentosAjustadas[i]

            entries.add(BarEntry(i.toFloat(), floatArrayOf(valorGasto, valorEntrada)))
        }

        // Atualizar o gráfico
        val barDataSet = BarDataSet(entries, "")

        //lista de cores das colunas
        val coresColunas = listOf<Int>(android.graphics.Color.rgb(255, 50, 50), android.graphics.Color.rgb(109, 251, 114))

        //colorindo as colunas do grafico
        barDataSet.setColors(coresColunas)

        //colorindo as colunas do grafico
        //barDataSet.color = android.graphics.Color.rgb(255, 50, 50) // colorindo as colunas da tabela de vermelho

        //aplicando a formatação criada em "valueFormatter" nos textos que ficam acima de cada coluna
        barDataSet.valueFormatter = createValueFormatter()

        // Configurar o tamanho do texto dos valores nas colunas
        barDataSet.valueTextSize = 14f // Exemplo: Tamanho 14sp

        // Definindo os rótulos para as legenda das cores do grafico
        barDataSet.stackLabels = arrayOf("Despesas", "Rendimentos")

        val barData = BarData(barDataSet)
        grafico.data = barData
        grafico.invalidate() // Atualiza o gráfico
    }

//    //função que cria as colunas do grafico
//    private fun criarColunasGraf(entries: MutableList<BarEntry>, qtdDiasMes: Int, nomeMes: String, usuarioID: Int) {
//        entries.clear()
//
//        // Recuperar a lista de valores do banco de dados
//        val listaValores: List<Float> = BancoDados(this).gastosDiariosMes(nomeMes.lowercase(), usuarioID)
//        val listaEntradas: List<Float> = BancoDados(this).rendimentosMes(usuarioID, nomeMes.lowercase())
//
//        Log.d("LISTA GASTOS", "$listaValores")
//        Log.d("LISTA RENDIMENTOS", "$listaEntradas")
//
//        // Se uma lista for menor, preencha com zeros
//        val maxSize = maxOf(listaValores.size, listaEntradas.size)
//        val listaValoresAjustada = listaValores + List(maxSize - listaValores.size) { 0f }
//        val listaEntradasAjustada = listaEntradas + List(maxSize - listaEntradas.size) { 0f }
//
//        // Iterar e adicionar os valores ao gráfico
//        for (i in 0 until qtdDiasMes) {
//            val valorGasto = if (i < listaValoresAjustada.size) listaValoresAjustada[i] else 0f
//            val valorEntrada = if (i < listaEntradasAjustada.size) listaEntradasAjustada[i] else 0f
//
//            entries.add(BarEntry(i.toFloat(), floatArrayOf(valorGasto, valorEntrada)))
//        }
//
//        // Preenchendo os dias restantes com valores 0 se necessário
//        if (qtdDiasMes > maxSize) {
//            for (i in maxSize until qtdDiasMes) {
//                entries.add(BarEntry(i.toFloat(), floatArrayOf(0f, 0f)))
//            }
//        }
//
//        // Atualizar o gráfico
//        val barDataSet = BarDataSet(entries, "")
//
//        //lista de cores das colunas
//        val coresColunas = listOf<Int>(android.graphics.Color.rgb(255, 50, 50), android.graphics.Color.rgb(109, 251, 114))
//
//        //colorindo as colunas do grafico
//        barDataSet.setColors(coresColunas)
//
//        //colorindo as colunas do grafico
//        //barDataSet.color = android.graphics.Color.rgb(255, 50, 50) // colorindo as colunas da tabela de vermelho
//
//        //aplicando a formatação criada em "valueFormatter" nos textos que ficam acima de cada coluna
//        barDataSet.valueFormatter = createValueFormatter()
//
//        // Configurar o tamanho do texto dos valores nas colunas
//        barDataSet.valueTextSize = 14f // Exemplo: Tamanho 14sp
//
//        // Definindo os rótulos para as legenda das cores do grafico
//        barDataSet.stackLabels = arrayOf("Despesas", "Rendimentos")
//
//        val barData = BarData(barDataSet)
//        grafico.data = barData
//        grafico.invalidate() // Atualiza o gráfico
//    }


    // função que cria as legendas das colunas
    private fun criarLegendas(qtdDiasMes: Int, calendar: Calendar): Array<String>{
        val legendaColunas = mutableListOf<String>() // Lista mutável para armazenar as legendas

        val formataData = SimpleDateFormat("dd", Locale("pt", "BR"))

        //adiciona uma legenda para cada coluna do grafico
        repeat (qtdDiasMes) {
            //pega o dia do mes e formata ele
            var dataFormatada = "dia ${formataData.format(calendar.time)}"

            legendaColunas += dataFormatada //adiciona as datas formatadas no array

            calendar.add(Calendar.DAY_OF_MONTH, 1) //avançar os dias no mes
        }

        return legendaColunas.toTypedArray() // Converte a lista mutável para um array
    }

    //função que pega os dados do BD para colocar nas listas de items
    private fun pegarDados(listaItems: List<OperacaoFinanceira> = emptyList()): List<OperacaoFinanceira> {

        // Lista que será preenchida com os itens formatados
        val items: MutableList<OperacaoFinanceira> = mutableListOf()

        // Itera sobre cada item da lista de entrada e cria novos itens formatados
        listaItems.forEach { item ->
            // Adiciona um novo item à lista 'items' com os valores formatados
            items.add(
                OperacaoFinanceira(
                    item.id,
                    item.descricao,  // Descrição da compra
                    item.tipo_movimento,  // Forma de pagamento
                    formatToCurrency(item.valor.toFloat()),  // Valor formatado
                    item.data  // Data formatada
                )
            )
        }

        return items
    }

    //função para voltar a tela inicial do aplicativo
    fun voltarTelaInicial(v: View){
        val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
        startActivity(navegarTelaPrincipal)
        finish()
    }

    //função para somar o valor total dos gastos ou custos do campo
    private fun somarValoresCampo(listaItems: List<OperacaoFinanceira> = emptyList()): Float {

        // Mapeia cada item para o valor numérico após remover caracteres de formatação
        val valorTotal = listaItems
            .map {
                it.valor
                    .replace("R$", "")  // Remove símbolo de moeda
                    .replace(".", "")   // Remove separador de milhares
                    .replace(",", ".") // Substitui vírgula por ponto decimal
                    .trim()
                    .toFloat()
            }
            .sum()  // Soma todos os valores

        return valorTotal
    }
}