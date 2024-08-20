package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.databinding.ActivityCadastrarBinding
import com.example.debts.databinding.ActivityTelaPrincipalBinding

class telaPrincipal : AppCompatActivity() {

    private lateinit var telaPrincipalApp: ActivityTelaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //configurando a navegação para a tela de perfil do usuário
        telaPrincipalApp = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(telaPrincipalApp.root)

        //configurando o evento de click no perfil do usuario
        telaPrincipalApp.btnPerfilUsuario.setOnClickListener{
            val navegarCriarConta = Intent(this, telaPerfilUsuario::class.java)
            startActivity(navegarCriarConta)
        }

        //configurando p/ tela de login iniciar novamente quando o botão de voltar do celular for prescionado
        val voltarTelaLogin = Intent(this, MainActivity::class.java)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                // Inicie a tela de login novamente
                startActivity(voltarTelaLogin)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

    }
}