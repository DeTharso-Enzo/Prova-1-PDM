package com.example.prova1_enzo

data class Chocolate(
    val id: String,
    val cacau: Int,        // entre 20 e 90
    val cor: String,       // "branco" ou "preto"
    val preco: Double,
    val fkCpf: String      // chave estrangeira -> Cliente.cpf
) {
    // Texto exibido no ListView
    override fun toString(): String =
        "ID: $id  |  Cacau: $cacau%  |  Cor: ${cor.replaceFirstChar { it.uppercase() }}\n" +
            "Preço: ${preco.emReais()}  |  Cliente (CPF): $fkCpf"
}
