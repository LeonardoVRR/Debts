package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.MsgCarregando.MensagemCarregando
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class cadastrar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastrar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //configurando o botão criar conta
        val btnCriarConta: Button = findViewById(R.id.btn_cadastrarConta)

        btnCriarConta.setOnClickListener { cadastrarConta() }

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de login
        val voltarTelaLogin = Intent(this, MainActivity::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                startActivity(voltarTelaLogin)
                finish()
            }
        })
    }

    //função para recuperar os dados passados pela activity criarConta
    private fun cadastrarConta() {
        val nome = intent.getStringExtra("nome").toString()
        val email = intent.getStringExtra("email").toString()
        val cpf = intent.getStringExtra("cpf").toString()
        val senha = intent.getStringExtra("senha").toString()

        //Log.d("nome", nome.toString())
        //CustomToast().showCustomToast(this, nome.toString())

//        if (BancoDados(this).verificarDados(email)) {
//            contaExistente = true
//            //CustomToast().showCustomToast(this, "Esse usuário já existe")
//        }
//        else {
//            contaExistente = false
//            BancoDados(this).cadastrarConta(nome, email, cpf, senha)
//
//
//            //CustomToast().showCustomToast(this, "Usuário adicionado com sucesso")
//        }

        if (termosUso()) {

            var contaExistente: String = ""

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    val conta = Metodos_BD_MySQL().cadastrarConta(nome, email, cpf, senha)

                    //verifica se a conta existe para fazer o login
                    if (conta){
                        contaExistente = "Essa conta já existe"
                    }

                    else {

                        contaExistente = "Conta Criada"

                        //salva o nome do usuario logado
                        DadosUsuario_BD_Debts(this).salvarUsuarioLogado(nome)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()
                        CustomToast().showCustomToast(this, contaExistente)

                        val navegarTelaQuestionario = Intent(this, tela_Consulta_IA::class.java)
                        startActivity(navegarTelaQuestionario)
                        finish()
                    }

                    executorService.shutdown()
                }
            }
        }

        else {
            CustomToast().showCustomToast(this, "Para continuar, aceite os Termos.")
        }

    }

    //configurando o evento de click no botão cadastrar
    fun termosUso(): Boolean {
        val termosLidos: CheckBox = findViewById(R.id.checkBox_termoLido)
        val autorizacaoUsoDados: CheckBox = findViewById(R.id.checkBox_AutorizarUsoDados)

        var termosAceitos: Boolean = false

        //verificando se as checkboxs foram marcadas para prosseguir com a navegação para a tela Principal do App
        if (termosLidos.isChecked && autorizacaoUsoDados.isChecked) {
            termosAceitos = true
        }

        else {
            termosAceitos = false
        }

        return termosAceitos
    }
}