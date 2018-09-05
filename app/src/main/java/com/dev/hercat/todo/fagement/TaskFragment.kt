package com.dev.hercat.todo.fagement

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dev.hercat.todo.R
import com.dev.hercat.todo.activity.ProcessTodoActivity
import com.dev.hercat.todo.activity.db
import com.dev.hercat.todo.data.Task
import com.dev.hercat.todo.data.Todo
import com.dev.hercat.todo.data.TodoStatus
import com.dev.hercat.todo.tools.getDate
import com.github.lzyzsd.circleprogress.DonutProgress
import kotlinx.android.synthetic.main.task_frgement.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import java.util.*

class TaskFragment: Fragment() {

    private lateinit var tvTaskName: TextView
    private lateinit var taskProgress: DonutProgress
    private lateinit var tvTaskInfo: TextView
    private lateinit var lvTask: ListView
    lateinit var task: Task
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.task_frgement, container, false)
        tvTaskName = view.findViewById(R.id.tvTaskName)
        taskProgress = view.findViewById(R.id.taskProgress)
        tvTaskInfo = view.findViewById(R.id.tvTaskInfo)
        lvTask = view.findViewById(R.id.lvTask)
        return view
    }

    override fun onStart() {
        super.onStart()
        tvTaskName.text = task.name
        tvTaskName.textColor = Color.parseColor(task.color)
        taskProgress.finishedStrokeColor = Color.parseColor(task.color)
        refresh()
    }

    override fun onResume() {
        super.onResume()
        YoYo.with(Techniques.BounceIn)
                .duration(500)
                .playOn(tvTaskName)
    }

    fun refresh() {
        val todos = activity?.db?.selectTodoByTaskId(task.id) ?: listOf()
        updateTodo(todos = todos.toMutableList())
    }

    fun updateTodo(todos: MutableList<Todo>) {
        val taskDesc = context!!.resources.getString(R.string.todo_of_task)
        if (todos.isNotEmpty()) {
            val now = Date()
            for ((index, todo) in todos.withIndex()) {
                if(todo.status == TodoStatus.TODO.value &&
                        getDate(todo.doTime).before(now)) {
                    todos.removeAt(index)
                    val newTodo = todo.copy(status = TodoStatus.EXPIRED.value)
                    todos.add(index, newTodo)
                    activity?.db?.updateTodo(newTodo)
                }
            }
            val progress = todos.count { it.status == TodoStatus.FINISHED.value } / todos.count().toFloat()
            taskProgress.progress = progress * 100
            tvTaskInfo.text = String.format(taskDesc, todos.count(), todos.count { it.status == TodoStatus.TODO.value })
            lvTask.adapter = TodoAdapter(context!!, task,  todos)
            errorView.visibility = View.GONE
            lvTask.visibility = View.VISIBLE
        } else {
            taskProgress.progress = 0f
            tvTaskInfo.text = String.format(taskDesc, 0, 0)
            errorView.visibility = View.VISIBLE
            lvTask.visibility = View.GONE
        }
    }


}

class TodoAdapter(val context: Context,
                  val task: Task,
                  val todos: List<Todo>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val todo = todos[position]
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.todo_item, null)
        val tvName = view.findViewById<TextView>(R.id.tvTodoName)
        val ivTaskStatus = view.findViewById<ImageView>(R.id.ivUnfinished)
        val expire = view.findViewById<View>(R.id.lvExpire)
        val tvInfo = view.findViewById<TextView>(R.id.tvTodoInfo)
        val cancelBg = view.findViewById<View>(R.id.cancelItemBg)
        tvInfo.text = todo.doTime
        tvName.text = todo.name
        if (todo.status == TodoStatus.EXPIRED.value || todo.status == TodoStatus.IGNORED.value) {
            cancelBg.visibility = View.VISIBLE
            tvName.textColor = context.resources.getColor(R.color.colorDanger)
            expire.visibility = View.VISIBLE
            ivTaskStatus.visibility = View.GONE
        } else {
            cancelBg.visibility = View.GONE
            expire.visibility = View.GONE
            ivTaskStatus.visibility = View.VISIBLE
            val icon = if (todo.status == TodoStatus.TODO.value)
                            R.drawable.icon_uncheck
                       else R.drawable.icon_checked
            val color = if (todo.status == TodoStatus.TODO.value)
                            context.resources.getColor(R.color.textPrimary)
                        else context.resources.getColor(R.color.colorSuccess)
            tvName.textColor = color
            ivTaskStatus.setImageResource(icon)
        }
        view.onClick {
            this@TodoAdapter.context.startActivity(
                   this@TodoAdapter.context.intentFor<ProcessTodoActivity>(
                           "taskName" to task.name,
                           "color" to task.color,
                           "todoStatus" to todo.status,
                           "todoDesc" to todo.desc,
                           "todoDate" to todo.doTime,
                           "todoName" to todo.name,
                           "todoId" to todo.id,
                           "taskId" to task.id
                   )
            )
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return todos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return todos.count()
    }
}
