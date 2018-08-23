package com.dev.hercat.todo.data

data class Task(val id: Int,
                val name: String,
                val color: String)

data class Todo(val id: Int,
                val taskId: Int,
                val name: String,
                val desc: String,
                val status: Int,
                val doTime: String)
enum class TodoStatus(val value: Int) {
    TODO(0),
    FINISHED(1),
    EXPIRED(2),
    IGNORED(3)
}