package com.example.prova1_enzo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** tela03 - lista, busca por CPF, edita e exclui clientes. */
class ListaClientesActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var listView: ListView
    private lateinit var etBusca: EditText
    private lateinit var adapter: ArrayAdapter<Cliente>
    private val itens = mutableListOf<Cliente>()
    private var filtroCpf = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela03)
        findViewById<View>(R.id.main).aplicarInsets()
        db = DBHelper(this)

        listView = findViewById(R.id.listView)
        etBusca = findViewById(R.id.et_buscaCpf)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, itens)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        // (5) busca por CPF; campo vazio = mostra todos
        findViewById<Button>(R.id.bt_buscarC).setOnClickListener {
            filtroCpf = etBusca.text.toString().trim()
            carregar()
            if (filtroCpf.isNotEmpty() && itens.isEmpty()) toast("Nenhum cliente com esse CPF")
        }

        findViewById<Button>(R.id.bt_editarC).setOnClickListener {
            val c = selecionado() ?: return@setOnClickListener
            startActivity(
                Intent(this, CadastroClienteActivity::class.java)
                    .putExtra(CadastroClienteActivity.EXTRA_CPF, c.cpf)
            )
        }

        findViewById<Button>(R.id.bt_excluirC).setOnClickListener {
            val c = selecionado() ?: return@setOnClickListener
            AlertDialog.Builder(this)
                .setTitle("Excluir cliente")
                .setMessage("Excluir ${c.nome}? Os chocolates vinculados a ele também serão excluídos.")
                .setPositiveButton("Excluir") { _, _ ->
                    if (db.excluirCliente(c.cpf)) toast("Cliente excluído")
                    carregar()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        findViewById<Button>(R.id.bt_telaC).setOnClickListener {
            startActivity(Intent(this, CadastroClienteActivity::class.java))
        }
        findViewById<Button>(R.id.bt_telaIni).setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        carregar() // atualiza após cadastro/edição
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun carregar() {
        itens.clear()
        if (filtroCpf.isEmpty()) {
            itens.addAll(db.listarClientes())
        } else {
            db.buscarCliente(filtroCpf)?.let { itens.add(it) }
        }
        listView.clearChoices()
        adapter.notifyDataSetChanged()
    }

    private fun selecionado(): Cliente? {
        val pos = listView.checkedItemPosition
        if (pos == ListView.INVALID_POSITION || pos >= itens.size) {
            toast("Selecione um cliente na lista")
            return null
        }
        return itens[pos]
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
