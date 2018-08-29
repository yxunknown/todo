package com.dev.hercat.todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dev.hercat.todo.R
import com.dev.hercat.todo.data.Todo
import com.dev.hercat.todo.data.TodoStatus

class TodoItemAdapter(val todos: List<Todo>,
                      val context: Context): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.simple_todo_item, null)
        val checkbox = view.findViewById<View>(R.id.checkbox)
        val tvTodoName = view.findViewById<TextView>(R.id.tvTodoName)
        val deleteLine = view.findViewById<View>(R.id.deleteLine)
        val todo = todos[position]
        checkbox.visibility = if (todo.status == TodoStatus.TODO.value) View.VISIBLE else View.GONE
        deleteLine.visibility = if (todo.status == TodoStatus.FINISHED.value) View.VISIBLE else View.GONE
        tvTodoName.text = todo.name
        return view
    }

    override fun getItem(position: Int): Any {
        return todos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return todos.size
    }
}