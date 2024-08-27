package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.databinding.ActivityCadastrarBinding
import com.example.debts.databinding.ActivityTelaPrincipalBinding

class telaPrincipal : AppCompatActivity() {

    //private lateinit var telaPrincipalApp: ActivityTelaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val voltarTelaLogin = Intent(this, MainActivity::class.java)

        //configurando o botão voltar do celular quando for prescionado p/ voltar na tela de login
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Log.v("Voltar", "Botão voltar Presscionado")

                startActivity(voltarTelaLogin)
                finish()
            }
        })
    }

    //configurando o evento de click no botão do DebtMap
    fun telaDebtMap(v: View) {
        val navegartelaDebtMap = Intent(this, tela_DebtMap::class.java)
        startActivity(navegartelaDebtMap)
    }

    //configurando o evento de click no botão do perfil do usuario
    fun telaPerfilUsuario(v: View) {
        val navegartelaPerfilUsuario = Intent(this, telaPerfilUsuario::class.java)
        startActivity(navegartelaPerfilUsuario)
    }

    //configurando o evento de click no botão do Relatorio Gastos
    fun teleRelatorioGastos(v: View) {
        val navegarTelaRelatorioGastos = Intent(this, tela_RelatorioGastos::class.java)
        startActivity(navegarTelaRelatorioGastos)
    }
}