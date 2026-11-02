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

/** tela05 - lista, busca por ID, edita e exclui chocolates. */
class ListaChocolatesActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var listView: ListView
    private lateinit var etBusca: EditText
    private lateinit var adapter: ArrayAdapter<Chocolate>
    private val itens = mutableListOf<Chocolate>()
    private var filtroId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela05)
        findViewById<View>(R.id.main).aplicarInsets()
        db = DBHelper(this)

        listView = findViewById(R.id.listViewChoco)
        etBusca = findViewById(R.id.et_buscaId)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, itens)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        // (6) busca por número identificador; campo vazio = mostra todos
        findViewById<Button>(R.id.bt_buscarChoco).setOnClickListener {
            filtroId = etBusca.text.toString().trim()
            carregar()
            if (filtroId.isNotEmpty() && itens.isEmpty()) toast("Nenhum chocolate com esse ID")
        }

        findViewById<Button>(R.id.bt_editarChoco).setOnClickListener {
            val ch = selecionado() ?: return@setOnClickListener
            startActivity(
                Intent(this, CadastroChocolateActivity::class.java)
                    .putExtra(CadastroChocolateActivity.EXTRA_ID, ch.id)
            )
        }

        findViewById<Button>(R.id.bt_excluirChoco).setOnClickListener {
            val ch = selecionado() ?: return@setOnClickListener
            AlertDialog.Builder(this)
                .setTitle("Excluir chocolate")
                .setMessage("Excluir o chocolate ${ch.id}?")
                .setPositiveButton("Excluir") { _, _ ->
                    if (db.excluirChocolate(ch.id)) toast("Chocolate excluído")
                    carregar()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        findViewById<Button>(R.id.bt_telaChoco).setOnClickListener {
            startActivity(Intent(this, CadastroChocolateActivity::class.java))
        }
        findViewById<Button>(R.id.bt_inicio5).setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        carregar()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun carregar() {
        itens.clear()
        if (filtroId.isEmpty()) {
            itens.addAll(db.listarChocolates())
        } else {
            db.buscarChocolate(filtroId)?.let { itens.add(it) }
        }
        listView.clearChoices()
        adapter.notifyDataSetChanged()
    }

    private fun selecionado(): Chocolate? {
        val pos = listView.checkedItemPosition
        if (pos == ListView.INVALID_POSITION || pos >= itens.size) {
            toast("Selecione um chocolate na lista")
            return null
        }
        return itens[pos]
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
