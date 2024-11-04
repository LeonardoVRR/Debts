package com.example.debts

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debts.API_Flask.Flask_Consultar_MySQL
import com.example.debts.BD_MySQL_App.Metodos_BD_MySQL
import com.example.debts.BD_SQLite_App.BancoDados
import com.example.debts.Conexao_BD.DadosFinanceiros_Usuario_BD_Debts
import com.example.debts.Conexao_BD.DadosUsuario_BD_Debts
import com.example.debts.ConsultaBD_MySQL.AgendarConsulta_MySQL
import com.example.debts.ConsultaBD_MySQL.CompararListas_MySQL_SQLite
import com.example.debts.MsgCarregando.MensagemCarregando
import com.example.debts.layoutExpandivel.criarListaItems
import com.example.debts.layout_Item_lista.ItemSpacingDecoration
import com.example.debts.layout_Item_lista.MyConstraintAdapter
import com.example.debts.visibilidadeSenha.AlterarVisibilidade
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class configConta_Usuario : AppCompatActivity() {

    private lateinit var recyclerViewManager: criarListaItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_config_conta_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val IDusuario = DadosUsuario_BD_Debts(this).pegarIdUsuario()

        var listaCartoesSalvos = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaCartoes().toMutableList()

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

        //----- config. area de salvar cartão ----------------------------------------------------//

        val input_numCartao: EditText = findViewById(R.id.input_numCartao)
        val btn_add_cartao: Button = findViewById(R.id.btn_cadastrarCartao)

        btn_add_cartao.setOnClickListener {
            val numCartao = input_numCartao.text.toString()

            if (numCartao != "") {
                salvarCartao(numCartao.toInt())
            }
        }

        val listaCartoes: RecyclerView = findViewById(R.id.lista_cartoesCadastrados)

        //configurando o layout manager
        listaCartoes.layoutManager = LinearLayoutManager(this)
        listaCartoes.setHasFixedSize(true)

        //configurando o espaçamento entre os itens
        listaCartoes.addItemDecoration(ItemSpacingDecoration())

        // Crie o adaptador para o RecyclerView
        var adapter = MyConstraintAdapter(listaCartoesSalvos)

        //adicionando os items na lista
        listaCartoes.adapter = adapter

        //----- configurando o botão para voltar para a tela do perfil do usuário --------------//
        val btn_btn_voltarPerfilUsuario: ImageButton = findViewById(R.id.btn_voltarPerfilUsuario)

        btn_btn_voltarPerfilUsuario.setOnClickListener{
            val navegarPerfilUsuario = Intent(this, telaPerfilUsuario::class.java)
            startActivity(navegarPerfilUsuario)
            finish()
        }

        val btn_atualizarListaCartoes: ImageButton = findViewById(R.id.btn_atualizar_listaCartoes)

        btn_atualizarListaCartoes.setOnClickListener {
            CustomToast().showCustomToast(this, "Atualizando Cartões!")

            var resultado = ""

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    //salvando a lista de cartoes
                    DadosUsuario_BD_Debts.listas_MySQL.cartoesUsuario = Flask_Consultar_MySQL(this).listCartoes(IDusuario)

                    Log.d("Lista Cartoes SQLite", "${BancoDados(this).listarCartoes(IDusuario)}")
                    Log.d("Lista Cartoes MySQL", "${DadosUsuario_BD_Debts.listas_MySQL.cartoesUsuario}")


                    resultado = "Cartões Atualizados!"

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao se conectar: ${e.message}"
                    Log.e("Erro ao se conectar", "${e.message}")
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()

                        listaCartoesSalvos.clear()

                        CompararListas_MySQL_SQLite(this).adicionarNovosCartoes(DadosUsuario_BD_Debts.listas_MySQL.cartoesUsuario, BancoDados(this).listarCartoes(IDusuario))

                        val novaListaCartoes = DadosFinanceiros_Usuario_BD_Debts(this, IDusuario).pegarListaCartoes().toMutableList()

                        // Atualizar a lista e notificar o adapter
                        listaCartoesSalvos.addAll(novaListaCartoes)
                        adapter.notifyDataSetChanged() // Notifica o Adapter que os dados mudaram

                        CustomToast().showCustomToast(this, resultado)
                    }

                    executorService.shutdown()
                }
            }
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

    // função para salvar um novo cartão no banco de dados
    fun salvarCartao(numCartao: Int) {

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
                //BancoDados(this).atualizarDados(novoNome, novoEmail, idUsuario)
                //CustomToast().showCustomToast(this, "Dados Atualizados com sucesso.")

                //atualiza o nome do usuario logado
                DadosUsuario_BD_Debts(this).salvarUsuarioLogado(novoNome)

                var resultado = ""

                val msgCarregando = MensagemCarregando(this)

                msgCarregando.mostrarMensagem()

                val executorService: ExecutorService = Executors.newSingleThreadExecutor()
                executorService.execute {
                    try {

                        resultado = Flask_Consultar_MySQL(this).atualizarDados(novoNome, novoEmail, idUsuario)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        //str = "Erro ao se conectar: ${e.message}"
                    } finally {

                        // Atualizar a UI no thread principal
                        runOnUiThread {
                            msgCarregando.ocultarMensagem()

                            val texto = CustomToast()
                            texto.showCustomToast(this, resultado)

                            texto.showCustomToast(this, "Sessão expirada. Refaça o login.")

                            val navegarTelaLogin = Intent(this, MainActivity::class.java)
                            startActivity(navegarTelaLogin)
                            finish()
                        }

                        executorService.shutdown()
                    }
                }

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
//                BancoDados(this).atualizarSenha(entradaSenha, idUsuario)
//                CustomToast().showCustomToast(this, "Senha redefinida com sucesso.")

                var resultado = ""

                val msgCarregando = MensagemCarregando(this)

                msgCarregando.mostrarMensagem()

                val executorService: ExecutorService = Executors.newSingleThreadExecutor()
                executorService.execute {
                    try {

                        resultado = Flask_Consultar_MySQL(this).atualizarSenha(entradaSenha, idUsuario)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        //str = "Erro ao se conectar: ${e.message}"
                    } finally {

                        // Atualizar a UI no thread principal
                        runOnUiThread {
                            msgCarregando.ocultarMensagem()
                            CustomToast().showCustomToast(this, resultado)
                        }

                        executorService.shutdown()
                    }
                }
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

            var resultado = ""

            val msgCarregando = MensagemCarregando(this)

            msgCarregando.mostrarMensagem()

            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {

                    Flask_Consultar_MySQL(this).deletarUsuario(idUsuario)

                    resultado = "Conta excluída com sucesso."

                } catch (e: Exception) {
                    e.printStackTrace()
                    resultado = "Erro ao deletar conta: ${e.message}"
                } finally {

                    // Atualizar a UI no thread principal
                    runOnUiThread {
                        msgCarregando.ocultarMensagem()
                        dialog.dismiss()
                        CustomToast().showCustomToast(this, resultado)

                        val voltarTelaLogin = Intent(this, MainActivity::class.java)
                        startActivity(voltarTelaLogin)
                        finish()
                    }

                    executorService.shutdown()
                }
            }

            //BancoDados(this).deletarUsuario(idUsuario)
        }

        btnCancelarExclusao.setOnClickListener {
            CustomToast().showCustomToast(this, "Exclusão de conta cancelada.")
            dialog.dismiss()
        }

        // Exibir o diálogo
        dialog.show()
    }
}