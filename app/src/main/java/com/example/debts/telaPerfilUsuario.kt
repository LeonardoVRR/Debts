package com.example.debts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.API_Flask.Flask_Consultar_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosFinanceiros_Usuario_BD_Debts
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.ConsultaBD_MySQL.CompararListas_MySQL_SQLite
import com.example.debts.FormatarNome.FormatarNome
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.SomarValoresCampo.Somar
import com.example.debts.layout_Item_lista.ItemSpacingDecoration
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.layout_Item_lista.OperacaoFinanceira
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class telaPerfilUsuario : AppCompatActivity() {

    private val listaCoresPieChart = listOf(
        android.graphics.Color.rgb(109, 251, 114),
        android.graphics.Color.rgb(255, 50, 50),
        android.graphics.Color.rgb(255, 226, 11)
    )

    //função para formatar numeros float para o formato Real(R$)
    fun formatToCurrency(value: Float): String =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)

    //@SuppressLint("MissingInflatedId")
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

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        var listaCartoesSalvos = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaCartoes().toMutableList()

        val listaEntradas = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaEntradasMes()

        val listaGastos = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaGastosMes()

        val listaGastosRecentes = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaGastosRecentes()

        //--------------- config. nome do mes grafico de pizza -----------------------------------//

//        //manipulando data
//        var calendar = Calendar.getInstance() // Cria uma instância de Calendar
//        var anoAtual = calendar.get(Calendar.YEAR) //pegando o ano atual
//        var mesAtual = calendar.get(Calendar.MONTH) //pegando o mes atual
//
//        // Obtém o nome do mês atual para exibição
//        var nomeMes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("pt", "BR"))
//
//        val nomeMesFormatado = "${FormatarNome().formatar(nomeMes)} $anoAtual"

        //-------------- config. lista de items Entradas Não Rastreadas --------------------------------//

        val listaEntradas_N_Rastreadas: RecyclerView = findViewById(R.id.listaEntradas_N_Rastreadas)

        //configurando o layout manager
        listaEntradas_N_Rastreadas.layoutManager = LinearLayoutManager(this)
        listaEntradas_N_Rastreadas.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        listaEntradas_N_Rastreadas.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        val adapter = MyConstraintAdapter(listaEntradas)

        //adicionando os items na lista
        listaEntradas_N_Rastreadas.adapter = adapter

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

        //----- config. area de salvar cartão ----------------------------------------------------//

        val cpfUsuario = DadosUsuario_BD_Debts(this).pegarCPFUsuario()

        val input_numCartao: EditText = findViewById(R.id.input_numCartao)
        val btn_add_cartao: Button = findViewById(R.id.btn_cadastrarCartao)

        btn_add_cartao.setOnClickListener {
            val numCartao = input_numCartao.text.toString()

            if (numCartao.isNotEmpty()) {
                salvarCartao(numCartao.toInt(), cpfUsuario, IDusuario)
            }
        }

        val listaCartoes: RecyclerView = findViewById(R.id.lista_cartoesCadastrados)

        //configurando o layout manager
        listaCartoes.layoutManager = LinearLayoutManager(this)
        listaCartoes.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        listaCartoes.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        var adapterCartao = MyConstraintAdapter(listaCartoesSalvos)

        //adicionando os items na lista
        listaCartoes.adapter = adapterCartao

        //------------- config botao de atualizar lista cartoes ---------------------------------//

        val btn_atualizarListaCartoes: ImageButton = findViewById(R.id.btn_atualizar_listaCartoes)

        btn_atualizarListaCartoes.setOnClickListener {
            //CustomToast().showCustomToast(this, "Atualizando Cartões!")

            var resultado = ""

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    //salvando a lista de cartoes
                    DadosUsuario_BD_Debts.listas_MySQL.cartoesUsuario = Flask_Consultar_MySQL(this).listCartoes(IDusuario)

                    Log.d("Lista Cartoes SQLite", "${BancoDados(this).listarCartoes(IDusuario, "todos")}")
                    Log.d("Lista Cartoes MySQL", "${DadosUsuario_BD_Debts.listas_MySQL.cartoesUsuario}")


                    resultado = "Cartões Atualizados!"

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao se conectar: ${e.message}"
                    Log.e("Erro ao se conectar", "${e.message}")
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()

                        listaCartoesSalvos.clear()

                        CompararListas_MySQL_SQLite(this).adicionarNovosCartoes(DadosUsuario_BD_Debts.listas_MySQL.cartoesUsuario, BancoDados(this).listarCartoes(IDusuario, "todos"))

                        val novaListaCartoes = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaCartoes().toMutableList()

                        // Atualizar a lista e notificar o adapter
                        listaCartoesSalvos.addAll(novaListaCartoes)
                        adapter.notifyDataSetChanged() // Notifica o Adapter que os dados mudaram

                        CustomToast().showCustomToast(this, resultado)

                        // Isso faz com que a atividade atual seja destruída e recriada, essencialmente funcionando como um "refresh" completo da atividade.
                        this.recreate()
                    }

                    executorService.shutdown()
                }
            }
        }

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
//    private fun pegarDados(listaItems: List<OperacaoFinanceira> = emptyList()): List<OperacaoFinanceira> {
//
//        // Lista que será preenchida com os itens formatados
//        val items: MutableList<OperacaoFinanceira> = mutableListOf()
//
//        // Itera sobre cada item da lista de entrada e cria novos itens formatados
//        listaItems.forEach { item ->
//            // Adiciona um novo item à lista 'items' com os valores formatados
//            items.add(
//                OperacaoFinanceira(
//                    item.id,
//                    item.descricao,  // Descrição da compra
//                    item.tipo_movimento,  // Forma de pagamento
//                    formatToCurrency(item.valor.toFloat()),  // Valor formatado
//                    item.data  // Data formatada
//                )
//            )
//        }
//
//        return items
//    }

    // função para salvar um novo cartão no banco de dados
    fun salvarCartao(numCartao: Int, cpf_usuario: String, IDusuario: Int) {
        var resultado = ""

        val msgCarregando = MensagemCarregando(this)

        msgCarregando.mostrarMensagem()

        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {

                //salvando o cartão
                resultado = Flask_Consultar_MySQL(this).salvarCartao(IDusuario,cpf_usuario, numCartao)

            } catch (e: Exception) {
                e.printStackTrace()
                resultado = "Erro ao salvar cartao: ${e.message}"
                Log.e("Erro ao salvar cartao", "${e.message}")
            } finally {

                // Atualizar a UI no thread principal
                runOnUiThread {
                    msgCarregando.ocultarMensagem()

                    CustomToast().showCustomToast(this, resultado)
                }

                executorService.shutdown()
            }
        }
    }

    //função para ir a tela de rendimentos
    fun telaQuestionario(v: View) {
        val navegarTelaQuestionario = Intent(this, tela_Consulta_IA::class.java)
        startActivity(navegarTelaQuestionario)
        finish()
    }

    //função para voltar a tela inicial do aplicativo
    fun voltarTelaInicial(v: View){
        val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
        startActivity(navegarTelaPrincipal)
        finish()
    }
}