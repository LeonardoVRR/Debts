package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_SQLite_App.BancoDados

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
    private fun recuperarDados(): Boolean {
        val nome = intent.getStringExtra("nome").toString()
        val email = intent.getStringExtra("email").toString()
        val cpf = intent.getStringExtra("cpf").toString()
        val senha = intent.getStringExtra("senha").toString()

        var contaExistente: Boolean = false

        //Log.d("nome", nome.toString())
        //CustomToast().showCustomToast(this, nome.toString())

        if (BancoDados(this).verificarDados(email)) {
            contaExistente = true
            //CustomToast().showCustomToast(this, "Esse usuário já existe")
        }
        else {
            contaExistente = false
            BancoDados(this).cadastrarConta(nome, email, cpf, senha)
            //CustomToast().showCustomToast(this, "Usuário adicionado com sucesso")
        }

        return contaExistente
    }

    //configurando o evento de click no botão cadastrar
    fun termosUso(v: View) {
        val termosLidos: CheckBox = findViewById(R.id.checkBox_termoLido)
        val autorizacaoUsoDados: CheckBox = findViewById(R.id.checkBox_AutorizarUsoDados)

        //verificando se as checkboxs foram marcadas para prosseguir com a navegação para a tela Principal do App
        if (termosLidos.isChecked && autorizacaoUsoDados.isChecked) {

            if (recuperarDados()) {
                CustomToast().showCustomToast(this, "Essa conta já existe")
            }

            else {
                CustomToast().showCustomToast(this, "Conta Criada")

                val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
                startActivity(navegarTelaPrincipal)
                finish()
            }

        }
    }
}