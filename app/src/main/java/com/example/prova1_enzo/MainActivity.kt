package com.example.prova1_enzo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela01)
        findViewById<View>(R.id.main).aplicarInsets()

        findViewById<Button>(R.id.bt_clientes).setOnClickListener {
            startActivity(Intent(this, CadastroClienteActivity::class.java))
        }
        findViewById<Button>(R.id.bt_choco).setOnClickListener {
            startActivity(Intent(this, CadastroChocolateActivity::class.java))
        }
        findViewById<Button>(R.id.bt_estatisc).setOnClickListener {
            startActivity(Intent(this, EstatisticasActivity::class.java))
        }
    }
}
