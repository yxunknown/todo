package com.dev.hercat.todo.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DatabaseHelper private constructor(val context: Context,
                     val name: String,
                     val factory: SQLiteDatabase.CursorFactory?,
                     val version: Int): ManagedSQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private var helper: DatabaseHelper? = null
        @Synchronized
        fun getInstance(context: Context, name: String,
                        factory: SQLiteDatabase.CursorFactory?, version: Int): DatabaseHelper {
            if (helper == null) {
                helper = DatabaseHelper(context.applicationContext, name, factory, version)
            }
            return helper!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("task", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "name" to TEXT,
                "color" to TEXT)
        db.createTable("todo", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "taskId" to INTEGER + NOT_NULL,
                "name" to TEXT,
                "desc" to TEXT,
                "status" to INTEGER,
                "doTime" to TEXT)
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //do update work
    }
}