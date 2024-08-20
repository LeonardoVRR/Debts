package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.databinding.ActivityCriarContaBinding
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class criarConta : AppCompatActivity() {

    //inicializando as variaveis
    private lateinit var telaCriarConta: ActivityCriarContaBinding;

    var visibilidadeNovaSenha = true
    var visibilidadeConfirmarSenha = true

    //val novaSenha: EditText = findViewById(R.id.input_novaSenha)
    //val confirmarNovaSenha: EditText = findViewById(R.id.input_confirmarSenha)
    //val cpf = findViewById<EditText>(R.id.input_cpf)
    //val email = findViewById<EditText>(R.id.input_email)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_criar_conta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //configurando a navegação para a tela de cadastrar conta
        //telaCriarConta = ActivityCriarContaBinding.inflate(layoutInflater)
        //setContentView(telaCriarConta.root)




         //configurando o evento de click no botão cadastrar
        /*
        telaCriarConta.btnCriarConta.setOnClickListener {

            //Log.v("valor Input", novaSenha.)

            //val userInput = novaSenha.toString()
            //Toast.makeText(this, "Entrada do usuário: ${userInput}   123", Toast.LENGTH_SHORT).show()

            if (validarEmail.containsMatchIn(email.getText().toString()) == false) {
                Toast.makeText(
                    this,
                    "Este email não é valido: ${email.text.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            //if (novaSenha.getText().toString() != confirmarNovaSenha.getText().toString()) {
            //     Toast.makeText(this, "Confirmação de senha incorreta!", Toast.LENGTH_SHORT).show()
            //}

            if (cpf.getText().toString().length < 11) {
                Toast.makeText(
                    this,
                    "Este CPF não é valido: ${cpf.text.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }


            if (validarEmail.containsMatchIn(email.getText().toString()) == true) {
                val navegarCriarConta = Intent(this, cadastrar::class.java)
                startActivity(navegarCriarConta)
            }

            //obs: Toast exibe uma mensagem no celular do usuario

            else {
                Log.v("Erro Criar Conta", "Não foi possivel criar sua conta!")

            }


        }
        */
    }

    //configurando o botão de icone das senhas para mudarem quando forem clicados
    public fun verNovaSenha(v: View){
        val iconeSenha: ImageButton = findViewById(R.id.btn_visibilidadeSenha)


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
        val iconeConfirmarSenha: ImageButton = findViewById(R.id.btn_confirmarSenha)

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
                Toast.makeText(
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
            }
        }

    }


}