package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        //----- configurando o botão para voltar para a tela do perfil do usuário --------------//
        val btn_btn_voltarPerfilUsuario: Button = findViewById(R.id.btn_homeRendimentos)

        btn_btn_voltarPerfilUsuario.setOnClickListener{
            val navegarPerfilUsuario = Intent(this, telaPerfilUsuario::class.java)
            startActivity(navegarPerfilUsuario)
            finish()
        }

        //-------------------- config. botão de voltar do celular --------------------------------//

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de perfil usuario
        val voltarTelaPerfilUsuario = Intent(this, telaPerfilUsuario::class.java)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaPerfilUsuario)
                finish()
            }
        })
    }
}