package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.databinding.ActivityCriarContaBinding
import android.view.View
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import com.example.debts.visibilidadeSenha.AlterarVisibilidade

class criarConta : AppCompatActivity() {

    //inicializando as variaveis
    private lateinit var telaCriarConta: ActivityCriarContaBinding;

    private var visibilidadeNovaSenha = true
    private var visibilidadeConfirmarSenha = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_criar_conta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //configurando o botão de icone das senhas para mudarem quando forem clicados
        val btn_IconeNovaSenha: ImageButton = findViewById(R.id.btn_visibilidadeNovaSenha)
        val btn_IconeConfirmarNovaSenha: ImageButton = findViewById(R.id.btn_visibilidadeConfirmarSenha)

        val input_NovaSenha: EditText = findViewById(R.id.input_novaSenha)
        val input_ConfirmarNovaSenha: EditText = findViewById(R.id.input_confirmarSenha)

        //criando uma instancia nova para cada campo senha para que elas não se interfiram
        val verNovaSenha = AlterarVisibilidade(input_NovaSenha, btn_IconeNovaSenha)
        val verConfirmarcaoNovaSenha = AlterarVisibilidade(input_ConfirmarNovaSenha, btn_IconeConfirmarNovaSenha)

        btn_IconeNovaSenha.setOnClickListener { verNovaSenha.verSenha() }
        btn_IconeConfirmarNovaSenha.setOnClickListener { verConfirmarcaoNovaSenha.verSenha() }

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de login
        val voltarTelaLogin = Intent(this, MainActivity::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaLogin)
                finish()
            }
        })
    }

    //configurando a ação de click do botão criar conta
    fun cadastrarDados(v: View) {
        val senhaDigitada: EditText = findViewById(R.id.input_novaSenha) //referencia o elemento do loyault da tela
        val confirmarSenhaDigitada: EditText = findViewById(R.id.input_confirmarSenha)
        val entradaSenha = senhaDigitada.text.toString().trim() //resgata o que foi digitado no input e converte p/ Str e tira os espaços no inicio e no fim da string
        val entradaCofirmarSenha = confirmarSenhaDigitada.text.toString().trim()

        val email:EditText = findViewById(R.id.input_email)
        val entradaEmail = email.text.toString().trim()
        val validarEmail = "(?=.*@)(?=.*\\.com)".toRegex() // expressão regular que verifica se a string tem o "@" e ".com"

        val cpf:EditText = findViewById(R.id.input_cpf)
        val entradaCPF = cpf.text.toString().trim()

        val nomeUsuario: EditText = findViewById(R.id.input_nomeUsuario)
        val entradaNomeUsuario = nomeUsuario.toString().trim()

        //verifica se os inputs estão vazios
        if (entradaSenha.isEmpty() || entradaCofirmarSenha.isEmpty() || entradaEmail.isEmpty() || entradaCPF.isEmpty() || entradaNomeUsuario.isEmpty()){
            CustomToast().showCustomToast(this, "Preencha todos os campos.")
        }

        else {
            //caso os campos senhas forem diferentes exibira uma mensagem alertam isso
            if (entradaSenha != entradaCofirmarSenha){
                CustomToast().showCustomToast(this, "Confirmação de senha incorreta.")
            }
            //verifica se o email digitado tem o "@" e ".com"
            else if (!validarEmail.containsMatchIn(entradaEmail)) {
                CustomToast().showCustomToast(this, "Este email não é valido: ${entradaEmail}")
            }
            //verificar se o cpf digitado tem 11 numeros
            else if (entradaCPF.length < 11){
                CustomToast().showCustomToast(this, "Este CPF não é valido: ${entradaCPF}")
            }

            else {

                val navegarCriarConta = Intent(this, cadastrar::class.java)
                startActivity(navegarCriarConta)
                finish()
            }
        }

    }


}