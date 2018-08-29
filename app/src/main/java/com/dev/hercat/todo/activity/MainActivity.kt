package com.dev.hercat.todo.activity

import android.app.ActivityOptions
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dev.hercat.todo.R
import com.dev.hercat.todo.adapter.TodayTodoAdapter
import com.dev.hercat.todo.adapter.TodoItemAdapter
import com.dev.hercat.todo.data.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.todo_today.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.toast
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnAddList.onClick { startActivity(intentFor<AddTaskActivity>()) }

        btnOpenDrawer.onClick {
            drawer.openDrawer(Gravity.START)
            val todos = db.selectTodoByDate(formateDate(Date()).substringBeforeLast(" "))
            println(todos)
            if (todos.isNotEmpty()) {
                todayTodoList.visibility = View.VISIBLE
                niceView.visibility = View.GONE
                todayTodoList.adapter = TodayTodoAdapter(todos, this@MainActivity)
            } else {
                todayTodoList.visibility = View.GONE
                niceView.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val tasks = db.getTasks()
        if (tasks.isNotEmpty()) {
            taskListContainer.removeAllViews()
            for ((index, task) in tasks.withIndex()) {
                val v = LayoutInflater.from(applicationContext).inflate(R.layout.task_list_item, null)
                val cardView = v.findViewById<CardView>(R.id.cvTaskContainer)
                val name = v.findViewById<TextView>(R.id.tvTaskName)
                val lvTaskItem = v.findViewById<ListView>(R.id.lvTaskItem)
                val errorView = v.findViewById<View>(R.id.errorView)
                val todos = db.selectTodoByTaskId(task.id)
                if (todos.isNotEmpty()) {
                    lvTaskItem.adapter = TodoItemAdapter(todos = todos, context = this)
                    errorView.visibility = View.GONE
                    lvTaskItem.visibility = View.VISIBLE
                } else {
                    errorView.visibility = View.VISIBLE
                    lvTaskItem.visibility = View.GONE
                }
                cardView.setCardBackgroundColor(Color.parseColor(task.color))
                name.text = task.name
                lvTaskItem.onItemClick { _, _, _, _ -> taskItemClicked(task, cardView, index) }
                v.onClick { taskItemClicked(task, cardView, index) }
                taskListContainer.addView(v)
            }
            YoYo.with(Techniques.SlideInUp)
                    .duration(1000)
                    .playOn(taskListContainer)
        }
    }

    private fun taskItemClicked(task: Task, cardView: CardView, position: Int) {
        YoYo.with(Techniques.Pulse)
                .duration(500)
                .playOn(cardView)
        startActivity(intentFor<TaskActivity>(
                "position" to position
        ))
    }
}
