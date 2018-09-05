package com.dev.hercat.todo.database

import android.content.ContentValues
import android.content.Context
import com.dev.hercat.todo.data.Task
import com.dev.hercat.todo.data.Todo
import org.jetbrains.anko.db.*

class Database(val context: Context) {
    private val NAME = "TASKS_LISTS"
    private val VERSION = 1
    private val db = DatabaseHelper.getInstance(context = context,
            name = NAME,
            factory = null,
            version = VERSION)

    fun insertTask(task: Task): Boolean {
        val values = ContentValues()
        values.put("name", task.name)
        values.put("color", task.color)
        // the insert fun will return the id of new row
        // or -1 when error occurs
        return db.use {
            insert("task", null, values)
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

    fun deleteTaskById(id: Int): Boolean {
        return db.use {
            //delete todos which taskId is id
            delete("todo",
                    "taskId = {taskId}",
                    "taskId" to id)
            //delete task by id
            delete("task",
                    "id = {id}",
                    "id" to id)
        } == 1
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
                    .whereArgs("doTime LIKE {date}", "date" to "$date%")
                    .parseList(TodoParser)
        }
    }

    fun updateTodo(todo: Todo): Boolean {
        return db.use {
            update("todo",
                    "taskId" to todo.taskId,
                    "name" to todo.name,
                    "desc" to todo.desc,
                    "doTime" to todo.doTime,
                    "status" to todo.status)
                    .whereArgs("id = {id}", "id" to todo.id)
                    .exec()
        } != -1
    }

    fun deleteTodoById(id: Int): Boolean {
        return db.use {
            delete("todo",
                    "id = {id}",
                    "id" to id)
        } == 1
    }

    object TaskParser : RowParser<Task> {
        override fun parseRow(columns: Array<Any?>): Task {
            return Task(id = (columns[0] as Long).toInt(),
                    name = columns[1] as String,
                    color = columns[2] as String)
        }
    }

    object TodoParser : RowParser<Todo> {
        override fun parseRow(columns: Array<Any?>): Todo {
            return Todo(id = (columns[0] as Long).toInt(),
                    taskId = (columns[1] as Long).toInt(),
                    name = columns[2] as String,
                    desc = columns[3] as String,
                    status = (columns[4] as Long).toInt(),
                    doTime = columns[5] as String)
        }
    }

}