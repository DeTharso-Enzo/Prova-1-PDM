package com.example.prova1_enzo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "gastroenterite.db"
        const val DB_VERSION = 1
        const val T_CLIENTE = "cliente"
        const val T_CHOCOLATE = "chocolate"
    }

    // Chaves estrangeiras vêm DESLIGADAS por padrão no SQLite do Android
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $T_CLIENTE (
                cpf      TEXT PRIMARY KEY,
                nome     TEXT NOT NULL,
                email    TEXT NOT NULL,
                telefone TEXT NOT NULL,
                idade    INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE $T_CHOCOLATE (
                id     TEXT PRIMARY KEY,
                cacau  INTEGER NOT NULL CHECK (cacau BETWEEN 20 AND 90),
                cor    TEXT NOT NULL CHECK (cor IN ('branco', 'preto')),
                preco  REAL NOT NULL,
                fk_cpf TEXT NOT NULL,
                FOREIGN KEY (fk_cpf) REFERENCES $T_CLIENTE(cpf)
                    ON DELETE CASCADE ON UPDATE CASCADE
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $T_CHOCOLATE")
        db.execSQL("DROP TABLE IF EXISTS $T_CLIENTE")
        onCreate(db)
    }

    // ---------------------------------------------------------------- Cliente

    private fun Cursor.toCliente() = Cliente(
        cpf = getString(getColumnIndexOrThrow("cpf")),
        nome = getString(getColumnIndexOrThrow("nome")),
        email = getString(getColumnIndexOrThrow("email")),
        telefone = getString(getColumnIndexOrThrow("telefone")),
        idade = getInt(getColumnIndexOrThrow("idade"))
    )

    /** Retorna false se o CPF já existir (ou qualquer outro erro). */
    fun inserirCliente(c: Cliente): Boolean {
        val v = ContentValues().apply {
            put("cpf", c.cpf)
            put("nome", c.nome)
            put("email", c.email)
            put("telefone", c.telefone)
            put("idade", c.idade)
        }
        return writableDatabase.insert(T_CLIENTE, null, v) != -1L
    }

    fun atualizarCliente(c: Cliente): Boolean {
        val v = ContentValues().apply {
            put("nome", c.nome)
            put("email", c.email)
            put("telefone", c.telefone)
            put("idade", c.idade)
        }
        return writableDatabase.update(T_CLIENTE, v, "cpf = ?", arrayOf(c.cpf)) > 0
    }

    /** Também exclui os chocolates do cliente (ON DELETE CASCADE). */
    fun excluirCliente(cpf: String): Boolean =
        writableDatabase.delete(T_CLIENTE, "cpf = ?", arrayOf(cpf)) > 0

    fun listarClientes(): List<Cliente> =
        readableDatabase.query(T_CLIENTE, null, null, null, null, null, "nome COLLATE NOCASE")
            .use { c -> buildList { while (c.moveToNext()) add(c.toCliente()) } }

    fun buscarCliente(cpf: String): Cliente? =
        readableDatabase.query(T_CLIENTE, null, "cpf = ?", arrayOf(cpf), null, null, null)
            .use { if (it.moveToFirst()) it.toCliente() else null }

    // -------------------------------------------------------------- Chocolate

    private fun Cursor.toChocolate() = Chocolate(
        id = getString(getColumnIndexOrThrow("id")),
        cacau = getInt(getColumnIndexOrThrow("cacau")),
        cor = getString(getColumnIndexOrThrow("cor")),
        preco = getDouble(getColumnIndexOrThrow("preco")),
        fkCpf = getString(getColumnIndexOrThrow("fk_cpf"))
    )

    /** Retorna false se o ID já existir ou se o CPF do cliente não existir. */
    fun inserirChocolate(ch: Chocolate): Boolean {
        val v = ContentValues().apply {
            put("id", ch.id)
            put("cacau", ch.cacau)
            put("cor", ch.cor)
            put("preco", ch.preco)
            put("fk_cpf", ch.fkCpf)
        }
        return writableDatabase.insert(T_CHOCOLATE, null, v) != -1L
    }

    fun atualizarChocolate(ch: Chocolate): Boolean {
        val v = ContentValues().apply {
            put("cacau", ch.cacau)
            put("cor", ch.cor)
            put("preco", ch.preco)
            put("fk_cpf", ch.fkCpf)
        }
        return writableDatabase.update(T_CHOCOLATE, v, "id = ?", arrayOf(ch.id)) > 0
    }

    fun excluirChocolate(id: String): Boolean =
        writableDatabase.delete(T_CHOCOLATE, "id = ?", arrayOf(id)) > 0

    fun listarChocolates(): List<Chocolate> =
        readableDatabase.query(T_CHOCOLATE, null, null, null, null, null, "id")
            .use { c -> buildList { while (c.moveToNext()) add(c.toChocolate()) } }

    fun buscarChocolate(id: String): Chocolate? =
        readableDatabase.query(T_CHOCOLATE, null, "id = ?", arrayOf(id), null, null, null)
            .use { if (it.moveToFirst()) it.toChocolate() else null }

    // ------------------------------------------------------------ Estatísticas

    /** 1 - número de clientes cadastrados */
    fun contarClientes(): Int =
        DatabaseUtils.queryNumEntries(readableDatabase, T_CLIENTE).toInt()

    /** 2 - número de chocolates cadastrados */
    fun contarChocolates(): Int =
        DatabaseUtils.queryNumEntries(readableDatabase, T_CHOCOLATE).toInt()

    /** 3 - chocolate com maior preço (null se não houver chocolates) */
    fun chocolateMaisCaro(): Chocolate? =
        readableDatabase.query(T_CHOCOLATE, null, null, null, null, null, "preco DESC", "1")
            .use { if (it.moveToFirst()) it.toChocolate() else null }

    /** 4 - preço médio (null se não houver chocolates) */
    fun precoMedio(): Double? =
        readableDatabase.rawQuery("SELECT AVG(preco) FROM $T_CHOCOLATE", null)
            .use { if (it.moveToFirst() && !it.isNull(0)) it.getDouble(0) else null }

    /** 7 - barras com mais de 50% de cacau */
    fun chocolatesAcimaDe50(): List<Chocolate> =
        readableDatabase.query(T_CHOCOLATE, null, "cacau > 50", null, null, null, "cacau DESC")
            .use { c -> buildList { while (c.moveToNext()) add(c.toChocolate()) } }

    // (5) busca de cliente por CPF  -> buscarCliente(cpf)
    // (6) busca de chocolate por ID -> buscarChocolate(id)
}
