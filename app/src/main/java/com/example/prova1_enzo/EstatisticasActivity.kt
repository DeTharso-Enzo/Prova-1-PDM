package com.example.prova1_enzo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** tela06 - estatísticas (itens 1, 2, 3, 4 e 7 do enunciado). */
class EstatisticasActivity : AppCompatActivity() {

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela06)
        findViewById<View>(R.id.main).aplicarInsets()
        db = DBHelper(this)

        findViewById<Button>(R.id.bt_inicio6).setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        atualizar()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun atualizar() {
        findViewById<TextView>(R.id.tv_numClientes).text =
            "Clientes cadastrados: ${db.contarClientes()}"

        findViewById<TextView>(R.id.tv_numChocolates).text =
            "Chocolates cadastrados: ${db.contarChocolates()}"

        val maisCaro = db.chocolateMaisCaro()
        findViewById<TextView>(R.id.tv_maisCaro).text =
            if (maisCaro == null) "Chocolate mais caro: -"
            else "Chocolate mais caro:\n$maisCaro"

        val media = db.precoMedio()
        findViewById<TextView>(R.id.tv_precoMedio).text =
            "Preço médio: ${media?.emReais() ?: "-"}"

        val acima50 = db.chocolatesAcimaDe50()
        findViewById<TextView>(R.id.tv_tituloAcima50).text =
            "Barras com mais de 50% de cacau (${acima50.size}):"
        findViewById<ListView>(R.id.lv_acima50).adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, acima50)
    }
}
