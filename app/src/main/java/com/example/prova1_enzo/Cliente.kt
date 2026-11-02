package com.example.prova1_enzo

data class Cliente(
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val idade: Int
) {
    // Texto exibido no ListView
    override fun toString(): String =
        "$nome\nCPF: $cpf  |  $idade anos\nTel: $telefone  |  $email"
}
