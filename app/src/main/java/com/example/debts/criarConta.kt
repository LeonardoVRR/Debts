package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.databinding.ActivityCriarContaBinding
import android.view.View
import android.widget.ImageButton

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
    }

    //configurando o botão de icone das senhas para mudarem quando forem clicados
    public fun verNovaSenha(v: View){
        val iconeSenha: ImageButton = findViewById(R.id.btn_visibilidadeNovaSenha)

        if (visibilidadeNovaSenha) {
            visibilidadeNovaSenha = false
            iconeSenha.setImageResource(R.drawable.visibility)
        }

        else {
            visibilidadeNovaSenha = true
            iconeSenha.setImageResource(R.drawable.visibility_off)
        }
    }

    public fun verConfirmarSenha(v: View){
        val iconeConfirmarSenha: ImageButton = findViewById(R.id.btn_visibilidadeConfirmarSenha)

        if (visibilidadeConfirmarSenha) {
            visibilidadeConfirmarSenha = false
            iconeConfirmarSenha.setImageResource(R.drawable.visibility)
        }

        else {
            visibilidadeConfirmarSenha = true
            iconeConfirmarSenha.setImageResource(R.drawable.visibility_off)
        }
    }

    //configurando a ação de click do botão criar conta
    public fun cadastrarDados(v: View) {
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
            Toast.makeText(
                this,
                "Preencha todos os campos.",
                Toast.LENGTH_SHORT
            ).show()

        }

        else {
            //caso os campos senhas forem diferentes exibira uma mensagem alertam isso
            if (entradaSenha != entradaCofirmarSenha){
                Toast.makeText( //exibe uma mensagem caso as senhas não forem iguais
                    this,
                    "Confirmação de senha incorreta.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //verifica se o email digitado tem o "@" e ".com"
            else if (!validarEmail.containsMatchIn(entradaEmail)) {
                Toast.makeText(
                    this,
                    "Este email não é valido: ${entradaEmail}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //verificar se o cpf digitado tem 11 numeros
            else if (entradaCPF.length < 11){
                Toast.makeText(
                    this,
                    "Este CPF não é valido: ${entradaCPF}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else {
                Toast.makeText(
                    this,
                    "Conta Criada",
                    Toast.LENGTH_SHORT
                ).show()

                val navegarCriarConta = Intent(this, cadastrar::class.java)
                startActivity(navegarCriarConta)
            }
        }

    }


}