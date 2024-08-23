package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.Conexao_BD.CustomToast
import com.example.debts.databinding.ActivityCadastrarBinding

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

        val voltarTelaLogin = Intent(this, MainActivity::class.java)

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de login
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                startActivity(voltarTelaLogin)
                finish()
            }
        })
    }

    //configurando o evento de click no botão cadastrar
    public fun termosUso(v: View) {
        val termosLidos: CheckBox = findViewById(R.id.checkBox_termoLido)
        val autorizacaoUsoDados: CheckBox = findViewById(R.id.checkBox_AutorizarUsoDados)

        //verificando se as checkboxs foram marcadas para prosseguir com a navegação para a tela Principal do App
        if (termosLidos.isChecked && autorizacaoUsoDados.isChecked) {

            CustomToast().showCustomToast(this, "Conta Criada")

            val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
            startActivity(navegarTelaPrincipal)
            finish()
        }
    }
}