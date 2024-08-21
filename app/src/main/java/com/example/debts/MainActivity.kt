package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class MainActivity : AppCompatActivity() {

    private var visibilidadeSenha = true
    lateinit var connect: Connection
    lateinit var connectionResult: String

    //configurando o crud no BD
    private var userDB = "Leonardo"
    private var senhaDB = "123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    public fun login(v: View) {
        //configurando a navegação para a tela principal
        val inputNome: EditText = findViewById(R.id.input_nomeUsuarioLogin)
        val inputSenha: EditText = findViewById(R.id.input_senhaLogin)

        val limparEntradaNome = inputNome.text.toString().lowercase().trim()
        val limparEntradaSenha = inputSenha.text.toString().trim()

        val limparNomeBD = userDB.toString().lowercase().trim() //tira os espaços no inicio e no fim da string e deixa tudo em minusculo
        val limparSenhaBD = senhaDB.toString().trim()

        //verificando se o nome de usuario e senha conferem com os que foram salvos no BD
        if (limparEntradaSenha.isEmpty() && limparEntradaNome.isEmpty()) {
            Toast.makeText(
                this,
                "Preencha todos os campos.",
                Toast.LENGTH_SHORT
            ).show()
        }

        else {
            if (limparEntradaNome == limparNomeBD && limparEntradaSenha == limparSenhaBD){
                val navegarTelaPrincipal = Intent(this, telaPrincipal::class.java)
                startActivity(navegarTelaPrincipal)
                finish()
            }

            else {
                Toast.makeText(
                    this,
                    "Usuario ou Senha incorreto.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    public fun criarConta(v: View) {
        val navegarCadastrarConta = Intent(this, criarConta::class.java)
        startActivity(navegarCadastrarConta)
        finish()
    }

    //configurando o botão de icone da senha para mudar quando for clicado
    public fun verNovaSenha(v: View){
        val iconeSenha: ImageButton = findViewById(R.id.btn_visibilidadeSenhaLogin)
        val mostrarSenha: EditText = findViewById(R.id.input_senhaLogin)

        if (visibilidadeSenha) {
            visibilidadeSenha = false
            iconeSenha.setImageResource(R.drawable.visibility)
            mostrarSenha.inputType = InputType.TYPE_CLASS_TEXT
        }

        else {
            visibilidadeSenha = true
            iconeSenha.setImageResource(R.drawable.visibility_off)
            mostrarSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        //move o cursor do input para o final do texto digitado
        val length = mostrarSenha.text.length
        mostrarSenha.setSelection(length)
    }

    public fun GetTextFromSQL(v: View) {
        val inputNome: EditText = findViewById(R.id.input_nomeUsuarioLogin)
        val limparEntradaNome = inputNome.text.toString().lowercase().trim()

        try {
            var conexaoBD_Debts = conexaoBD_Debts()
            connect = conexaoBD_Debts.connectionClass()!!

            if (connect != null) {
                if (!connect.isClosed()){
                    var query: String = "SELECT * FROM cadastroUsuarios"
                    var st: Statement = connect.createStatement()
                    var rs: ResultSet = st.executeQuery(query)

                    while (rs.next()){
                        Log.v("Consulta BD", "${rs.getString(2)}")
                    }
                }

                else {
                    Log.v("Conexao BD", "Conexão encerrada")
                }
            }

            else {
                connectionResult="Check Connection"
                Log.v("DB", connectionResult)
            }
        }

        catch (ex: Exception) {
            Log.e("Error 2 ", "${ex.message}")
        }
    }
}