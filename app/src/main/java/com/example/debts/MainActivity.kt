package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.databinding.ActivityMainBinding
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    private lateinit var telaLogin: ActivityMainBinding
    //private

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //configurando a navegação para a tela de criar conta
        telaLogin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(telaLogin.root)


        val inputNome = findViewById<EditText>(R.id.input_userName)
        val inputSenha = findViewById<EditText>(R.id.input_password)

        //configurando o crud no BD
        var userDB = "Leonardo"
        var senhaDB = "123"

        val btnLogin = findViewById<Button>(R.id.btn_login)

        //configurando o evento de click no botão criar conta
        telaLogin.btnCriarConta.setOnClickListener{
            val navegarCadastrarConta = Intent(this, criarConta::class.java)
            startActivity(navegarCadastrarConta)
            finish()
        }

        //configurando o evento de click no botão login
        btnLogin.setOnClickListener {
            var limparEntradaNome = inputNome.getText().toString().lowercase().trim() //tira os espaços no inicio e no fim da string e pegando o input do campo nome usuario
            var limparNomeBD = userDB.lowercase().trim() //tira os espaços no inicio e no fim da string

            if (limparEntradaNome == limparNomeBD && inputSenha.text.toString() == senhaDB) {

                telaLogin.btnLogin.setOnClickListener{
                    val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
                    startActivity(navegarTelaPrincipal)
                    finish()
                }
                Log.v("Dentro do IF", "Deu Certo")
            }

            else {
                Log.v("Dentro do ELSE", "Deu Errado")
            }
        }

    }
}