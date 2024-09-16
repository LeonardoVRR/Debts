package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.MsgCarregando.MensagemCarregando
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class telaAdicionarRendimentos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_adicionar_rendimentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val input_valorRendimento: EditText = findViewById(R.id.input_valorRendimento)

        input_valorRendimento.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Remove formatting
                val cleanString = s.toString().replace("[^\\d]".toRegex(), "")

                // Convert to double and format
                val parsed = cleanString.toDoubleOrNull()
                val formatted = if (parsed != null) {
                    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed / 100)
                } else {
                    ""
                }

                // Remove listener to avoid infinite loop
                input_valorRendimento.removeTextChangedListener(this)
                input_valorRendimento.setText(formatted)
                input_valorRendimento.setSelection(formatted.length)
                input_valorRendimento.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //----- configurando o botão para voltar para a tela do perfil do usuário --------------//
        val btn_voltarTelaPrincipal: Button = findViewById(R.id.btn_homeRendimentos)

        btn_voltarTelaPrincipal.setOnClickListener{
            val navegarPerfilUsuario = Intent(this, telaPrincipal::class.java)
            startActivity(navegarPerfilUsuario)
            finish()
        }

        //--------------------- config. dropDown list --------------------------------------------//

        val tps_movimentacao = resources.getStringArray(R.array.Escolha)
        val arrayAdapter = ArrayAdapter(this, R.layout.activity_drop_down_item_lista_balanco, tps_movimentacao)
        val autoCompleteTextView: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(arrayAdapter)

        //-------------------- config. botão de voltar do celular --------------------------------//

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de perfil usuario
        val voltarTelaPrincipal = Intent(this, telaPrincipal::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaPrincipal)
                finish()
            }
        })
    }

    fun salvarRendimento(v: View) {

        val msgCarregando = MensagemCarregando(this)

        var resultado = ""

        msgCarregando.mostrarMensagem()

        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {

                //-------------------- config. dos campos inputs p/ adicionar rendimento -----------------//
                //val input_nomeRendimento: EditText = findViewById(R.id.input_nomeRendimento)
                val input_tpMovimentacao: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
                val input_dtRendimento: EditText = findViewById(R.id.input_dtRendimento)
                val input_valorRendimento: EditText = findViewById(R.id.input_valorRendimento)

                //val nomeRendimeto = input_nomeRendimento.text.toString().trim()
                val tipoMovimentacao = input_tpMovimentacao.text.toString().trim()

                val dataRendimento = input_dtRendimento.text.toString().trim()

                val valorRendimento = input_valorRendimento.text.toString().trim()

                val idUsuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

                if (tipoMovimentacao.isEmpty() || dataRendimento.isEmpty() || valorRendimento.isEmpty()){
                    CustomToast().showCustomToast(this, "Preencha todos os campos.")
                }

                else if (tipoMovimentacao == "Tipo de Movimentação") {
                    CustomToast().showCustomToast(this, "Escolha um tipo de movimentação.")
                }

                else {

                    //formata o valor do valor rendimento para o formato Real(R$)
                    val valorRendimentoFormatado = formatarValorRendimento(valorRendimento)

                    //verifica se a data digitada é valida
                    if (!validarData(dataRendimento)) {
                        // Data válida
                        CustomToast().showCustomToast(this@telaAdicionarRendimentos, "Data Inválida: $dataRendimento")
                    }

                    else {

                        if (tipoMovimentacao != "Divida") {
                            //BancoDados(this).salvarRendimento(tipoMovimentacao, dataRendimento, valorRendimentoFormatado, idUsuario)

                            resultado = Metodos_BD_MySQL().salvarRendimento(tipoMovimentacao, dataRendimento, valorRendimentoFormatado, idUsuario)
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                //str = "Erro ao se conectar: ${e.message}"
            }

            // Atualizar a UI no thread principal
            runOnUiThread {
                msgCarregando.ocultarMensagem()
                CustomToast().showCustomToast(this, resultado)
            }
        }
    }

    //função para validar uma data passada
    private fun validarData(data: String): Boolean {
        // Verifica o formato da data usando expressão regular
        val regex = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")
        if (!regex.matches(data)) {
            return false
        }

        // Divide a data em dia, mês e ano
        val (diaStr, mesStr, anoStr) = data.split("/")

        // Converte para inteiros
        val dia = diaStr.toInt()
        val mes = mesStr.toInt()
        val ano = anoStr.toInt()

        // Verifica a validade da data usando Calendar
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, ano)
        calendar.set(Calendar.MONTH, mes - 1)  // Meses são baseados em 0
        calendar.set(Calendar.DAY_OF_MONTH, dia)

        // Ajusta o mês e o dia e compara com os valores fornecidos
        return (calendar.get(Calendar.YEAR) == ano &&
                calendar.get(Calendar.MONTH) == mes - 1 &&
                calendar.get(Calendar.DAY_OF_MONTH) == dia)
    }

    //função para formatar o valor do rendimento para poder salvar no BD
    private fun formatarValorRendimento(valorRendimento: String): Float {

        val valorFormatado = valorRendimento
            .replace("R$", "")  // Remove símbolo de moeda
            .replace(".", "")   // Remove separador de milhares
            .replace(",", ".") // Substitui vírgula por ponto decimal
            .trim()
            .toFloat()

        return valorFormatado
    }
}