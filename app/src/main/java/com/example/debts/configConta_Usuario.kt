package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.visibilidadeSenha.AlterarVisibilidade

class configConta_Usuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_config_conta_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //--------------- config. texto do hint dos input nome e email ---------------------------//
        val hint_txt_Nome: EditText = findViewById(R.id.input_mudarNomeUsuario)
        val hint_txt_email: EditText = findViewById(R.id.input_NovoEmail)

        val nomeUsuario: String = DadosUsuario_BD_Debts(this).pegarNomeUsuario()
        val emailUsuario: String = DadosUsuario_BD_Debts(this).pegarEmailUsuario()

        hint_txt_Nome.setText(nomeUsuario)
        hint_txt_email.setText(emailUsuario)

        //--- configurando o botão de icone das senhas para mudarem quando forem clicados --------//
        val btn_IconeRedefinirSenha: ImageButton = findViewById(R.id.btn_visibilidadeRedefinirSenha)
        val btn_IconeConfirmarRedefinirSenha: ImageButton = findViewById(R.id.btn_visibilidadeConfirmarRedefinirSenha)

        val input_RedefinirSenha: EditText = findViewById(R.id.input_redefinirSenha)
        val input_ConfirmarRedefinirSenha: EditText = findViewById(R.id.input_confirmarRedefinirSenha)

        btn_IconeRedefinirSenha.setOnClickListener { AlterarVisibilidade(input_RedefinirSenha, btn_IconeRedefinirSenha).verSenha() }
        btn_IconeConfirmarRedefinirSenha.setOnClickListener { AlterarVisibilidade(input_ConfirmarRedefinirSenha, btn_IconeConfirmarRedefinirSenha).verSenha() }

        //----- configurando o botão para voltar para a tela do perfil do usuário --------------//
        val btn_btn_voltarPerfilUsuario: ImageButton = findViewById(R.id.btn_voltarPerfilUsuario)

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

    //configurando a função para o botão que altera o nome e email do usuário
    fun EditarDados(v: View) {
        val nomeAtual: EditText = findViewById<EditText?>(R.id.input_mudarNomeUsuario)
        val novoNome = nomeAtual.text.toString().trim()

        val emailAtual: EditText = findViewById(R.id.input_NovoEmail)
        val novoEmail = emailAtual.text.toString().trim()
        val validarEmail = "^[^@]+@[^@]+\\.com$".toRegex() // expressão regular que verifica se a string tem o "@" e ".com"

        val idUsuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        if(novoNome.isEmpty() && novoEmail.isEmpty()){
            CustomToast().showCustomToast(this, "Preencha um dos campos primeiro.")
        }

        else {
            if (novoEmail.isNotEmpty() && !validarEmail.containsMatchIn(novoEmail)){
                CustomToast().showCustomToast(this, "Este email não é valido: ${novoEmail}")
            }

            else {
                BancoDados(this).atualizarDados(novoNome, novoEmail, idUsuario)

                //atualiza o nome do usuario logado
                DadosUsuario_BD_Debts(this).salvarUsuarioLogado(novoNome)

                CustomToast().showCustomToast(this, "Dados Atualizados com sucesso.")
            }
        }
    }

    fun RedifinirSenha(v: View) {
        val senhaDigitada: EditText = findViewById(R.id.input_redefinirSenha)
        val confirmarSenhaDigitada: EditText = findViewById(R.id.input_confirmarRedefinirSenha)

        val entradaSenha = senhaDigitada.text.toString().trim() //resgata o que foi digitado no input e converte p/ Str e tira os espaços no inicio e no fim da string
        val entradaCofirmarSenha = confirmarSenhaDigitada.text.toString().trim()

        val idUsuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        //verifica se os inputs estão vazios
        if (entradaSenha.isEmpty() || entradaCofirmarSenha.isEmpty()){
            CustomToast().showCustomToast(this, "Preencha todos os campos.")
        }

        else {
            //caso os campos senhas forem diferentes exibira uma mensagem alertam isso
            if (entradaSenha != entradaCofirmarSenha){
                CustomToast().showCustomToast(this, "Confirmação de senha incorreta.")
            }

            else {
                BancoDados(this).atualizarSenha(entradaSenha, idUsuario)

                CustomToast().showCustomToast(this, "Senha redefinida com sucesso.")
            }
        }
    }

    // Configurando a função que vai exibir a mensagem de aviso ao clicar em "Deletar Conta"
    fun AvisoDeletarConta(v: View) {

        // Inflar o layout personalizado
        val inflater: LayoutInflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_layout_aviso_exclusao_conta, null)

        // Constroi o dialog/pop-up
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        // Criar o dialog/pop-up
        val dialog: AlertDialog = builder.create()

        // Acessar os botões do layout inflado usando dialogView.findViewById
        val btnConfirmarExclusao: Button = dialogView.findViewById(R.id.btn_ConfirmarExclusaoConta)
        val btnCancelarExclusao: Button = dialogView.findViewById(R.id.btn_CancelarExclusaoConta)

        val idUsuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        // Configurar ações para os botões
        btnConfirmarExclusao.setOnClickListener {
            CustomToast().showCustomToast(this, "Conta excluída com sucesso.")
            dialog.dismiss()

            BancoDados(this).deletarUsuario(idUsuario)

            val voltarTelaLogin = Intent(this, MainActivity::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }

        btnCancelarExclusao.setOnClickListener {
            CustomToast().showCustomToast(this, "Exclusão de conta cancelada.")
            dialog.dismiss()
        }

        // Exibir o diálogo
        dialog.show()
    }
}