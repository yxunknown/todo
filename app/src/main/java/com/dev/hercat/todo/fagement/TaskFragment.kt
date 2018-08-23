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
import com.dev.hercat.todo.activity.db
import com.dev.hercat.todo.data.Task
import com.dev.hercat.todo.data.Todo
import com.dev.hercat.todo.data.TodoStatus
import com.github.lzyzsd.circleprogress.DonutProgress
import org.jetbrains.anko.textColor

class TaskFragment: Fragment() {

    private var tvTaskName: TextView? = null
    private var taskProgress: DonutProgress? = null
    private var tvTaskInfo: TextView? = null
    private var lvTask: ListView? = null

    var task: Task? = null
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
        tvTaskName!!.text = task?.name ?: ""
        tvTaskName!!.textColor = Color.parseColor(task?.color)
        taskProgress!!.finishedStrokeColor = Color.parseColor(task?.color)
        refresh()
    }

    override fun onResume() {
        super.onResume()
        YoYo.with(Techniques.BounceIn)
                .duration(500)
                .playOn(tvTaskName)
    }

    fun refresh() {
        val todos = activity?.db?.selectTodoByTaskId(task?.id ?: -1) ?: listOf()
        if (todos.isNotEmpty()) {
            val progress = todos.count { it.status == TodoStatus.TODO.value } / todos.count().toFloat()
            taskProgress!!.progress = progress * 100
            tvTaskInfo!!.text = "${todos.count { it.status == TodoStatus.TODO.value }} of ${todos.count()} tasks"
            lvTask!!.adapter = TodoAdapter(context!!, todos)
        }
    }


}

class TodoAdapter(val context: Context,
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
                            R.color.textPrimary
                        else R.color.colorSuccess
            tvName.textColor = color
            ivTaskStatus.setImageResource(icon)
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
