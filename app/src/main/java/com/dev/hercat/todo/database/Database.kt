package com.dev.hercat.todo.database

import android.content.ContentValues
import android.content.Context
import com.dev.hercat.todo.data.Task
import com.dev.hercat.todo.data.Todo
import org.jetbrains.anko.db.RowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class Database(val context: Context) {
    val NAME = "TASKS_LISTS"
    val VERSION = 1
    val db = DatabaseHelper.getInstance(context = context,
            name =  NAME,
            factory = null,
            version = VERSION)

    fun insertTask(task: Task): Boolean {
        val values = ContentValues()
        values.put("name", task.name)
        values.put("color", task.color)
        // the insert fun will return the id of new row
        // or -1 when error occurs
        return db.use {
            insert("task",null, values)
        } != -1L
    }

    fun selectTaskById(taskId: Int): List<Task> {
        return db.use {
           select("task")
                   .whereArgs("id = {id}", "id" to taskId)
                   .parseList(TaskParser)
        }
    }

    fun getTasks(): List<Task> {
        return db.use {
            select("task")
                    .parseList(TaskParser)
        }
    }

    fun insertTodo(todo: Todo): Boolean {
        return if (selectTaskById(todo.taskId).isEmpty()) {
            false
        } else {
            val values = ContentValues()
            values.put("taskId", todo.taskId)
            values.put("name", todo.name)
            values.put("desc", todo.desc)
            values.put("status", todo.status)
            values.put("doTime", todo.doTime)
            db.use {
                insert("todo", null, values)
            } != -1L
        }
    }

    fun selectTodoByTaskId(taskId: Int): List<Todo> {
        return db.use {
            select("todo")
                    .whereArgs("taskId = {taskId}", "taskId" to taskId)
                    .parseList(TodoParser)
        }
    }

    fun selectTodoByDate(date: String): List<Todo> {
        return db.use {
            select("todo")
                    .whereArgs("doTime LIKES {date}", "date" to "$date%")
                    .parseList(TodoParser)
        }
    }

    object TaskParser: RowParser<Task> {
        override fun parseRow(columns: Array<Any?>): Task {
            return Task(id = (columns[0] as Long).toInt(),
                    name = columns[1] as String,
                    color = columns[2] as String)
        }
    }

    object TodoParser: RowParser<Todo> {
        override fun parseRow(columns: Array<Any?>): Todo {
            return Todo(id = (columns[0] as Long).toInt(),
                    taskId =  (columns[1] as Long).toInt(),
                    name = columns[2] as String,
                    desc = columns[3] as String,
                    status = (columns[4] as Long).toInt(),
                    doTime = columns[5] as String)
        }
    }

}