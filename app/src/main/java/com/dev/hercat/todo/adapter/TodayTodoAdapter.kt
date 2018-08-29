package com.dev.hercat.todo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dev.hercat.todo.R
import com.dev.hercat.todo.data.Todo

class TodayTodoAdapter(val todos: List<Todo>,
                       val context: Context): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.today_todo_item, null)
        val tvTodoName = view.findViewById<TextView>(R.id.tvTodoName)
        val tvTodoDate = view.findViewById<TextView>(R.id.tvTodoDate)
        tvTodoName.text = todos[position].name
        tvTodoDate.text = todos[position].doTime
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