package com.dev.hercat.todo.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
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
import org.jetbrains.anko.support.v4.drawerListener
import org.jetbrains.anko.toast
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnAddList.onClick { startActivity(intentFor<AddTaskActivity>()) }
        drawer.drawerListener {
            onDrawerOpened {
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
                todayTodoList.onItemClick { adapter, view, position, id ->
                    val todo = todos[position]
                    val task = db.selectTaskById(todo.taskId).firstOrNull()
                    if (task !is Task) {
                        toast("Ohh, Parse data error")
                    } else {
                        startActivity(intentFor<ProcessTodoActivity>(
                                "taskName" to task.name,
                                "color" to task.color,
                                "todoStatus" to todo.status,
                                "todoDesc" to todo.desc,
                                "todoDate" to todo.doTime,
                                "todoName" to todo.name,
                                "todoId" to todo.id,
                                "taskId" to task.id
                        ))
                    }

                }
            }
            onDrawerClosed {
                YoYo.with(Techniques.RotateIn)
                        .duration(500)
                        .playOn(btnOpenDrawer)
            }
        }
        btnOpenDrawer.onClick {
            drawer.openDrawer(Gravity.START)

        }
        btnSetting.onClick {
            startActivity(intentFor<SettingActivity>())
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTask()
        YoYo.with(Techniques.SlideInUp)
                .duration(1000)
                .playOn(taskListContainer)

    }

    private fun refreshTask() {
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
                v.setOnLongClickListener {
                    val deleteBtn = v.findViewById<View>(R.id.btnDeleteTask)
                    deleteBtn.visibility = if (deleteBtn.visibility == View.GONE) View.VISIBLE else View.GONE
                    deleteBtn.onClick {
                        MaterialDialog(this@MainActivity)
                                .title(res = R.string.dialog_tip)
                                .message(res = R.string.delete_task)
                                .negativeButton(res = R.string.negative_text)
                                .positiveButton(res = R.string.positive_text) { dialog ->
                                    dialog.dismiss()

                                    if (db.deleteTaskById(task.id)) {
                                        YoYo.with(Techniques.FadeOutDown)
                                                .duration(500)
                                                .playOn(v)
                                        taskListContainer.removeView(v)
                                    } else {
                                        toast(R.string.delete_task_error)
                                    }
                                }
                                .show()
                    }
                    true
                }
                taskListContainer.addView(v)
            }
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
