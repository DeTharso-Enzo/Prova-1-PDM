package com.example.prova1_enzo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** tela02 - cadastra um cliente novo ou, se receber EXTRA_CPF, edita um existente. */
class CadastroClienteActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CPF = "extra_cpf"
    }

    private lateinit var db: DBHelper
    private var cpfEdicao: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela02)
        findViewById<View>(R.id.main).aplicarInsets()
        db = DBHelper(this)

        val etNome = findViewById<EditText>(R.id.et_nome)
        val etCpf = findViewById<EditText>(R.id.et_cpf)
        val etIdade = findViewById<EditText>(R.id.et_idade)
        val etTelefone = findViewById<EditText>(R.id.et_telefone)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val btCadastrar = findViewById<Button>(R.id.bt_cadastrarC)

        // Modo edição: preenche os campos e trava o CPF (é a chave primária)
        val cliente = intent.getStringExtra(EXTRA_CPF)?.let { db.buscarCliente(it) }
        if (cliente != null) {
            cpfEdicao = cliente.cpf
            etNome.setText(cliente.nome)
            etCpf.setText(cliente.cpf)
            etCpf.isEnabled = false
            etIdade.setText(cliente.idade.toString())
            etTelefone.setText(cliente.telefone)
            etEmail.setText(cliente.email)
            btCadastrar.text = "Salvar alterações"
        }

        btCadastrar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val cpf = etCpf.text.toString().trim()
            val telefone = etTelefone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val idade = etIdade.text.toString().trim().toIntOrNull()

            if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
                toast("Preencha todos os campos")
                return@setOnClickListener
            }
            if (idade == null || idade < 0 || idade > 130) {
                toast("Idade inválida")
                return@setOnClickListener
            }

            val novo = Cliente(cpf, nome, email, telefone, idade)

            if (cpfEdicao != null) {
                if (db.atualizarCliente(novo)) {
                    toast("Cliente atualizado")
                    finish() // volta para a lista
                } else {
                    toast("Erro ao atualizar cliente")
                }
            } else {
                if (db.inserirCliente(novo)) {
                    toast("Cliente cadastrado")
                    etNome.text.clear(); etCpf.text.clear(); etIdade.text.clear()
                    etTelefone.text.clear(); etEmail.text.clear()
                } else {
                    toast("CPF já cadastrado")
                }
            }
        }

        findViewById<Button>(R.id.bt_pag1).setOnClickListener {
            startActivity(Intent(this, ListaClientesActivity::class.java))
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
