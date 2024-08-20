package com.example.debts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.databinding.ActivityCadastrarBinding

class cadastrar : AppCompatActivity() {

    private lateinit var telaCadastrarConta: ActivityCadastrarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastrar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //configurando a navegação para a tela Principal do App
        telaCadastrarConta = ActivityCadastrarBinding.inflate(layoutInflater)
        setContentView(telaCadastrarConta.root)

        //configurando o evento de click no botão cadastrar
        telaCadastrarConta.btnCadastrarConta.setOnClickListener{
            val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
            startActivity(navegarTelaPrincipal)
        }
    }
}