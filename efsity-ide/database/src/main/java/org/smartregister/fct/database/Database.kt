package org.smartregister.fct.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

object Database {

    private var db: AppDatabase? = null

    fun getDatabase() : AppDatabase {
        if (db == null) {
            val driver = JdbcSqliteDriver("jdbc:sqlite:fct.db")
            AppDatabase.Schema.create(driver)
            db = AppDatabase(driver)
        }

        return db!!
    }
}