package com.dev.hercat.todo.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.dev.hercat.todo.R
import com.dev.hercat.todo.data.Task
import com.dev.hercat.todo.fagement.TaskFragment
import kotlinx.android.synthetic.main.activity_task.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.onPageChangeListener

class TaskActivity : AppCompatActivity() {
    var currentTask = Task(1, "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        init()
    }

    private fun init() {
        val tasks = db.getTasks()
        if (tasks.isNotEmpty()) {
            val postion = intent.getIntExtra("position", 0)
            currentTask = tasks[postion]
            taskIndicator.selectedColor = Color.parseColor(currentTask.color)
            taskPager.adapter = TaskAdapter(supportFragmentManager, tasks)
            taskPager.setCurrentItem(postion, true)
            taskPager.onPageChangeListener {
                onPageSelected { i ->
                    currentTask = tasks[i]
                    taskIndicator.selectedColor = Color.parseColor(tasks[i].color)
                }
            }
        }
        btnAddNewTodo.onClick {
            startActivity(intentFor<NewTodoActivity>("color" to currentTask.color,
                    "id" to currentTask.id,
                    "name" to currentTask.name)) }
        btnCancel.onClick { onBackPressed() }
    }

    inner class TaskAdapter(val fm: FragmentManager, val tasks: List<Task>): FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return tasks.size
        }

        override fun getItem(p0: Int): Fragment {
            val fragment = TaskFragment().apply {
                task = tasks[p0]
            }
            return fragment
        }
    }
}
