package com.example.prova1_enzo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** tela04 - cadastra um chocolate novo (vinculado a um cliente) ou edita um existente. */
class CadastroChocolateActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private lateinit var db: DBHelper
    private var idEdicao: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela04)
        findViewById<View>(R.id.main).aplicarInsets()
        db = DBHelper(this)

        val etId = findViewById<EditText>(R.id.et_idChoco)
        val etCacau = findViewById<EditText>(R.id.et_cacau)
        val rgCor = findViewById<RadioGroup>(R.id.rg_cor)
        val etPreco = findViewById<EditText>(R.id.et_preco)
        val spCliente = findViewById<Spinner>(R.id.sp_cliente)
        val btCadastrar = findViewById<Button>(R.id.bt_cadastrarChoco)

        // Spinner com os clientes (lista paralela para recuperar o CPF pela posição)
        val clientes = db.listarClientes()
        spCliente.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            clientes.map { "${it.nome} (${it.cpf})" }
        )

        // Modo edição
        val choco = intent.getStringExtra(EXTRA_ID)?.let { db.buscarChocolate(it) }
        if (choco != null) {
            idEdicao = choco.id
            etId.setText(choco.id)
            etId.isEnabled = false
            etCacau.setText(choco.cacau.toString())
            rgCor.check(if (choco.cor == "branco") R.id.rb_branco else R.id.rb_preto)
            etPreco.setText(choco.preco.toString())
            val idx = clientes.indexOfFirst { it.cpf == choco.fkCpf }
            if (idx >= 0) spCliente.setSelection(idx)
            btCadastrar.text = "Salvar alterações"
        }

        btCadastrar.setOnClickListener {
            val id = etId.text.toString().trim()
            val cacau = etCacau.text.toString().trim().toIntOrNull()
            val preco = etPreco.text.toString().trim().replace(',', '.').toDoubleOrNull()

            if (id.isEmpty()) {
                toast("Informe o número identificador"); return@setOnClickListener
            }
            if (cacau == null || cacau < 20 || cacau > 90) {
                toast("Cacau deve estar entre 20% e 90%"); return@setOnClickListener
            }
            val cor = when (rgCor.checkedRadioButtonId) {
                R.id.rb_branco -> "branco"
                R.id.rb_preto -> "preto"
                else -> {
                    toast("Escolha a cor do chocolate"); return@setOnClickListener
                }
            }
            if (preco == null || preco <= 0) {
                toast("Preço inválido"); return@setOnClickListener
            }
            if (clientes.isEmpty()) {
                toast("Cadastre um cliente antes de cadastrar um chocolate")
                return@setOnClickListener
            }

            val cpf = clientes[spCliente.selectedItemPosition].cpf
            val novo = Chocolate(id, cacau, cor, preco, cpf)

            if (idEdicao != null) {
                if (db.atualizarChocolate(novo)) {
                    toast("Chocolate atualizado")
                    finish() // volta para a lista
                } else {
                    toast("Erro ao atualizar chocolate")
                }
            } else {
                if (db.inserirChocolate(novo)) {
                    toast("Chocolate cadastrado")
                    etId.text.clear(); etCacau.text.clear(); etPreco.text.clear()
                    rgCor.clearCheck()
                } else {
                    toast("Número identificador já cadastrado")
                }
            }
        }

        findViewById<Button>(R.id.bt_listaChoco).setOnClickListener {
            startActivity(Intent(this, ListaChocolatesActivity::class.java))
        }
        findViewById<Button>(R.id.bt_inicio4).setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
