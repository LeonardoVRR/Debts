package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.ManipularUsoCartaoCredito.ManipularUsoCartaoCredito

class tela_Consulta_IA : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_consulta_ia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // config uso cartão
        val painel_opc1: EditText = findViewById(R.id.editTextNumber_opc1)
        val btn_AumentarUso_opc1: ImageButton = findViewById(R.id.btn_aumentar_opc1)
        val btn_DiminuirUso_opc1: ImageButton = findViewById(R.id.btn_diminuir_opc1)

        btn_AumentarUso_opc1.setOnClickListener {
            var valorPainel = ManipularUsoCartaoCredito(painel_opc1, this).AumentarUso(btn_AumentarUso_opc1)
        }

        btn_DiminuirUso_opc1.setOnClickListener {
            var valorPainel = ManipularUsoCartaoCredito(painel_opc1, this).DiminuirUso(btn_DiminuirUso_opc1)
        }

        val painel_opc2: EditText = findViewById(R.id.editTextNumber_opc2)
        val btn_AumentarUso_opc2: ImageButton = findViewById(R.id.btn_aumentar_opc2)
        val btn_DiminuirUso_opc2: ImageButton = findViewById(R.id.btn_diminuir_opc2)

        btn_AumentarUso_opc2.setOnClickListener {
            var valorPainel = ManipularUsoCartaoCredito(painel_opc2, this).AumentarUso(btn_AumentarUso_opc2)
        }

        btn_DiminuirUso_opc2.setOnClickListener {
            var valorPainel = ManipularUsoCartaoCredito(painel_opc2, this).DiminuirUso(btn_DiminuirUso_opc2)
        }

        val painel_opc3: EditText = findViewById(R.id.editTextNumber_opc3)
        val btn_AumentarUso_opc3: ImageButton = findViewById(R.id.btn_aumentar_opc3)
        val btn_DiminuirUso_opc3: ImageButton = findViewById(R.id.btn_diminuir_opc3)

        btn_AumentarUso_opc3.setOnClickListener {
            var valorPainel = ManipularUsoCartaoCredito(painel_opc3, this).AumentarUso(btn_AumentarUso_opc3)
        }

        btn_DiminuirUso_opc3.setOnClickListener {
            var valorPainel = ManipularUsoCartaoCredito(painel_opc3, this).DiminuirUso(btn_DiminuirUso_opc3)
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

        //val grupo_opc_Investimentos: ConstraintLayout = findViewById(R.id.lyt_ListaOpcoes_TiposInvestimentos)

        val listaOpcoesSelecionadas: MutableList<String> = mutableListOf()

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

    //função para voltar a tela inicial do aplicativo
    fun voltarTelaInicial(v: View){
        val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
        startActivity(navegarTelaPrincipal)
        finish()
    }
}