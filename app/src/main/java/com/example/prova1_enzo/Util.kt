package com.example.prova1_enzo

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

// Formata um Double como moeda brasileira, ex.: R$ 12,50
fun Double.emReais(): String = String.format(Locale("pt", "BR"), "R$ %.2f", this)

// Evita que o conteúdo fique embaixo da barra de status/navegação (edge-to-edge)
fun View.aplicarInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
        insets
    }
}
