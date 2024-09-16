package com.example.debts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.ManipularUsoCartaoCredito.ManipularUsoCartaoCredito
import com.example.debts.MsgCarregando.MensagemCarregando
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class tela_Consulta_IA : AppCompatActivity() {
    private var valorSeekBarFinal: Int = 0
    private val listaOpcoesSelecionadas: MutableList<String> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_consulta_ia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //------------------ config. seekBar Conhecimento Financeiro -----------------------------//

        val indicador_SeekBar: ImageView = findViewById(R.id.indicador_seekBar)
        val txt_indicador_SeekBar: TextView = findViewById(R.id.txt_indicador_seekBar)
        val seekBar: SeekBar = findViewById(R.id.seekBar_ConhecimentoFinanceiro)


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var valorSeekBar: Int = 0

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Calcular a nova posição do ImageView com base no progresso
                val max = seekBar.max
                val width = seekBar.width - seekBar.paddingStart - seekBar.paddingEnd

                // Aqui você tem o valor atual do SeekBar
                valorSeekBar = progress

                // Converter o progresso em posição X
                val thumbPosX = seekBar.paddingStart + (width * progress / max)

                //atuliza o texto do indicador do seekBar
                txt_indicador_SeekBar.text = "${valorSeekBar}%"

                // Atualizar a posição do ImageView e TextView
                indicador_SeekBar.x = (thumbPosX.toFloat() - (indicador_SeekBar.width / 2)) + 27
                txt_indicador_SeekBar.x = (thumbPosX.toFloat() - (txt_indicador_SeekBar.width / 2)) + 27
            }

            //função que vai executar quando o usuário começar a interagir com o seekBar
            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            //função que vai executar quando o usuário terminar de interagir com o seekBar
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                valorSeekBarFinal = valorSeekBar
                //CustomToast().showCustomToast(this@tela_Consulta_IA, "Conhecimento: ${valorSeekBarFinal}%")
            }
        })

        //------------------ config. campo uso cartão credito ------------------------------------//

        val painel_opc1: EditText = findViewById(R.id.editTextNumber_opc1)
        val painel_opc2: EditText = findViewById(R.id.editTextNumber_opc2)
        val painel_opc3: EditText = findViewById(R.id.editTextNumber_opc3)

        val btn_AumentarUso_opc1: ImageButton = findViewById(R.id.btn_aumentar_opc1)
        val btn_DiminuirUso_opc1: ImageButton = findViewById(R.id.btn_diminuir_opc1)

        btn_AumentarUso_opc1.setOnClickListener {
            ManipularUsoCartaoCredito(painel_opc1, this).AumentarUso(btn_AumentarUso_opc1)
        }

        btn_DiminuirUso_opc1.setOnClickListener {
            ManipularUsoCartaoCredito(painel_opc1, this).DiminuirUso(btn_DiminuirUso_opc1)
        }

        val btn_AumentarUso_opc2: ImageButton = findViewById(R.id.btn_aumentar_opc2)
        val btn_DiminuirUso_opc2: ImageButton = findViewById(R.id.btn_diminuir_opc2)

        btn_AumentarUso_opc2.setOnClickListener {
            ManipularUsoCartaoCredito(painel_opc2, this).AumentarUso(btn_AumentarUso_opc2)
        }

        btn_DiminuirUso_opc2.setOnClickListener {
            ManipularUsoCartaoCredito(painel_opc2, this).DiminuirUso(btn_DiminuirUso_opc2)
        }

        val btn_AumentarUso_opc3: ImageButton = findViewById(R.id.btn_aumentar_opc3)
        val btn_DiminuirUso_opc3: ImageButton = findViewById(R.id.btn_diminuir_opc3)

        btn_AumentarUso_opc3.setOnClickListener {
            ManipularUsoCartaoCredito(painel_opc3, this).AumentarUso(btn_AumentarUso_opc3)
        }

        btn_DiminuirUso_opc3.setOnClickListener {
            ManipularUsoCartaoCredito(painel_opc3, this).DiminuirUso(btn_DiminuirUso_opc3)
        }

        //------------------------ config area tipos de investimentos ----------------------------//

        val btn_switch_TiposInvestimentos: Switch = findViewById(R.id.switch_Questionario)

        val checkBox_opc_Poupanca: CheckBox = findViewById(R.id.checkBox_opc_Poupanca)
        val checkBox_poc_TitulosPublicos: CheckBox = findViewById(R.id.checkBox_poc_TitulosPublicos)
        val checkBox_opc_TituloCapitalizacao: CheckBox = findViewById(R.id.checkBox_opc_TituloCapitalizacao)
        val checkBox_opc_Consorcio: CheckBox = findViewById(R.id.checkBox_opc_Consorcio)
        val checkBox_opc_FundosImobiliarios: CheckBox = findViewById(R.id.checkBox_opc_FundosImobiliarios)
        val checkBox_opc_FundosMultimercado: CheckBox = findViewById(R.id.checkBox_opc_FundosMultimercado)
        val checkBox_opc_TesouroDireto: CheckBox = findViewById(R.id.checkBox_opc_TesouroDireto)
        val checkBox_opc_Acoes: CheckBox = findViewById(R.id.checkBox_opc_Acoes)

        btn_switch_TiposInvestimentos.setOnCheckedChangeListener { _, isChecked ->
            var liberarOpcoes: Boolean

            if (isChecked) {
                liberarOpcoes = true

                // Abilita a seleção das checkBox's
                checkBox_opc_Poupanca.isEnabled = liberarOpcoes
                checkBox_poc_TitulosPublicos.isEnabled = liberarOpcoes
                checkBox_opc_TituloCapitalizacao.isEnabled = liberarOpcoes
                checkBox_opc_Consorcio.isEnabled = liberarOpcoes
                checkBox_opc_FundosImobiliarios.isEnabled = liberarOpcoes
                checkBox_opc_FundosMultimercado.isEnabled = liberarOpcoes
                checkBox_opc_TesouroDireto.isEnabled = liberarOpcoes
                checkBox_opc_Acoes.isEnabled = liberarOpcoes

                //verifica se o checkBox foi selecionado e o adiciona a lista de tipos de investimentos selecionados
                val listaOpcoes_TiposInvestimentos = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    val text = (buttonView as CheckBox).text.toString() //verificar se a view que sofreu ateração de estado é um checkBox e pegando seu texto

                    if (isChecked) {
                        // Adiciona à lista se o CheckBox for marcado
                        if (text !in listaOpcoesSelecionadas) {
                            listaOpcoesSelecionadas.add(text)
                        }
                    }

                    else {
                        // Remove da lista se o CheckBox for desmarcado
                        if (text in listaOpcoesSelecionadas) {
                            listaOpcoesSelecionadas.remove(text)
                        }
                    }

                    // Exemplo de uso: exibir a lista no log
                    Log.d("lista de tipos de investimentos selecionados", listaOpcoesSelecionadas.joinToString(", "))
                }

                //chama a função para adicionar a lista de tipos de investimentos selecionados
                checkBox_opc_Poupanca.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
                checkBox_poc_TitulosPublicos.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
                checkBox_opc_TituloCapitalizacao.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
                checkBox_opc_Consorcio.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
                checkBox_opc_FundosImobiliarios.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
                checkBox_opc_FundosMultimercado.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
                checkBox_opc_TesouroDireto.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
                checkBox_opc_Acoes.setOnCheckedChangeListener(listaOpcoes_TiposInvestimentos)
            }

            else {
                liberarOpcoes = false

                //exclui todas as opções selecionadas salvas na lista de tipos de investimentos selecionados
                listaOpcoesSelecionadas.clear()

                val desmarcarCheckBoxs: Boolean = false

                // Desabilita a seleção das checkBox's
                checkBox_opc_Poupanca.isEnabled = liberarOpcoes
                checkBox_poc_TitulosPublicos.isEnabled = liberarOpcoes
                checkBox_opc_TituloCapitalizacao.isEnabled = liberarOpcoes
                checkBox_opc_Consorcio.isEnabled = liberarOpcoes
                checkBox_opc_FundosImobiliarios.isEnabled = liberarOpcoes
                checkBox_opc_FundosMultimercado.isEnabled = liberarOpcoes
                checkBox_opc_TesouroDireto.isEnabled = liberarOpcoes
                checkBox_opc_Acoes.isEnabled = liberarOpcoes

                // Desmaca todas as checkBox's selecionadas
                checkBox_opc_Poupanca.isChecked = desmarcarCheckBoxs
                checkBox_poc_TitulosPublicos.isChecked = desmarcarCheckBoxs
                checkBox_opc_TituloCapitalizacao.isChecked = desmarcarCheckBoxs
                checkBox_opc_Consorcio.isChecked = desmarcarCheckBoxs
                checkBox_opc_FundosImobiliarios.isChecked = desmarcarCheckBoxs
                checkBox_opc_FundosMultimercado.isChecked = desmarcarCheckBoxs
                checkBox_opc_TesouroDireto.isChecked = desmarcarCheckBoxs
                checkBox_opc_Acoes.isChecked = desmarcarCheckBoxs
            }
        }

        //-------------------- config. botão salvarQuest -----------------------------------------//

        val btn_salvarQuest: Button = findViewById(R.id.btn_salvarQuest)

        btn_salvarQuest.setOnClickListener {
            salvarQuestionario()
        }

        //-------------------- config. botão de voltar do celular --------------------------------//

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela principal
        val voltarTelaPrinpipal = Intent(this, telaPrincipal::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaPrinpipal)
                finish()
            }
        })
    }

    //função para salvar os dados na lista de dados p/ calcular a rota financeira
    fun salvarQuestionario() {

//        var listaMetasCriadas: MutableList<String> = mutableListOf(
//            "economizar R$ 1000,00 para uma viagem",
//            "quitar uma dívida de R$ 500,00",
//            "acumular R$ 2000,00 em fundo de emergência",
//            "investir R$ 300,00 por mês em ações",
//            "economizar R$ 1500,00 para a compra de um novo eletrônico"
//        )
//
//        val nomeMeta = "Meta Cartão A"

        //Log.d("LISTA CALCULAR ROTA", "${listaDadosCalcularRotaFinanceira}")

        //metasCriadas(nomeMeta, listaMetasCriadas)

        //BancoDados(this).salvarQuestionario(valorSeekBarFinal, listaOpcoesSelecionadas, valor_painel_opc1.toInt(), valor_painel_opc2.toInt(), valor_painel_opc3.toInt(), idUsuario)

        val msgCarregando = MensagemCarregando(this)
        msgCarregando.mostrarMensagem()

        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {

            //--------------- config. lista de dados para salvar as informações preechidas pelo usuario -------------------//
            val listaDadosCalcularRotaFinanceira: MutableList<String> = mutableListOf()

            val painel_opc1: EditText = findViewById(R.id.editTextNumber_opc1)
            val painel_opc2: EditText = findViewById(R.id.editTextNumber_opc2)
            val painel_opc3: EditText = findViewById(R.id.editTextNumber_opc3)

            //pegando o uso do cartão de credito
            val valor_painel_opc1: String = painel_opc1.text.toString()
            val valor_painel_opc2: String = painel_opc2.text.toString()
            val valor_painel_opc3: String = painel_opc3.text.toString()

            listaDadosCalcularRotaFinanceira += "Conhecimento Financeiro: ${valorSeekBarFinal}%"
            listaDadosCalcularRotaFinanceira += "Tipos de investimentos selecionados: ${listaOpcoesSelecionadas}"
            listaDadosCalcularRotaFinanceira += "Uso cartão E-commerce: ${valor_painel_opc1}"
            listaDadosCalcularRotaFinanceira += "Uso cartão App de transporte: ${valor_painel_opc2}"
            listaDadosCalcularRotaFinanceira += "Uso cartão App de entregas: ${valor_painel_opc3}"

            val idUsuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

            var salvar = ""

            try {

                salvar = Metodos_BD_MySQL().salvarQuestionario(valorSeekBarFinal, listaOpcoesSelecionadas, valor_painel_opc1.toInt(), valor_painel_opc2.toInt(), valor_painel_opc3.toInt(), idUsuario)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Atualizar a UI no thread principal
            runOnUiThread {
                msgCarregando.ocultarMensagem()
                CustomToast().showCustomToast(this, "$salvar")
            }
        }
    }

    //função para salvar a lista de metas criadas pela IA
    fun metasCriadas(nomeMeta: String, listaMetasCriadas: MutableList<String>) {

        //--------------- config. data de criação da meta ----------------------------------------//

        // Obtendo a data atual para a criação da meta

        val dataCriacaoMeta = Calendar.getInstance().time // Cria uma instância de Calendar e pega a data atual

        // Define uma formação para a data
        val formataData = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))
        // usa a formatação definida
        var dataCriacaoMetaFormatada = formataData.format(dataCriacaoMeta)

        //--------------- fim da config. data de criação da meta ---------------------------------//

        val listaMetas = listaMetasCriadas.toList()
        val listaMetaEstados = MutableList(listaMetasCriadas.size) { false } // Inicializa uma lista com a qtd de elementos referente a qtd de metas e com todos os valores como false
        val progressoMeta: Float = 0f
        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        //BancoDados(this).salvarMeta(nomeMeta, dataCriacaoMetaFormatada, listaMetas, listaMetaEstados, progressoMeta, IDusuario)
    }

    //função para voltar a tela inicial do aplicativo
    fun voltarTelaInicial(v: View){
        val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
        startActivity(navegarTelaPrincipal)
        finish()
    }
}