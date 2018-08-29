package com.dev.hercat.todo.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.dev.hercat.todo.R
import com.dev.hercat.todo.data.Todo
import com.dev.hercat.todo.data.TodoStatus
import kotlinx.android.synthetic.main.activity_process_todo.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.util.*

class ProcessTodoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_process_todo)
        init()
    }

    fun init() {
        tvTaskName.text = intent.getStringExtra("taskName")
        tvTaskName.textColor = Color.parseColor(intent.getStringExtra("color"))
        fun initStatus(status: Int) {
            btnMarkIgnore.visibility = View.VISIBLE
            btnMarkDone.visibility = View.VISIBLE
            btnMarkRedo.visibility = View.VISIBLE
            btnMarkDelete.visibility = View.VISIBLE
            val state = when (status) {
                0 -> {
                    btnMarkIgnore.visibility = View.GONE
                    btnMarkRedo.visibility = View.GONE
                    "TODO" to "You are going to finish this task."
                }
                1 -> {
                    btnMarkDone.visibility = View.GONE
                    btnMarkIgnore.visibility = View.GONE
                    "FINISHED" to "You have finished this task, Congratulations!"
                }
                2 -> {
                    "EXPIRED" to "Opps, You don't finish this task at time."
                }
                3 -> {
                    btnMarkIgnore.visibility = View.GONE
                    "IGNORED" to "You have ignored this task since it was expired"
                }
                else -> "NOT DEFINED" to "Opps, I have no idea about this"
            }
            tvTaskStatus.text = state.first
            btnStatusTips.onClick {
                MaterialDialog(this@ProcessTodoActivity)
                        .title(text = "Status Tips")
                        .message(text = state.second)
                        .positiveButton(text = "Ok")
                        .show()
            }
        }
        val status = intent.getIntExtra("todoStatus", 0)
        initStatus(status)
        tvTodoName.text = intent.getStringExtra("todoName")
        tvTodoDate.text = intent.getStringExtra("todoDate")
        tvTodoDesc.text = intent.getStringExtra("todoDesc")
        btnCancel.onClick { onBackPressed() }
        btnMarkDone.onClick {
            MaterialDialog(this@ProcessTodoActivity)
                    .title(text = "Tips")
                    .message(text = "Continue to mark this task as finished?")
                    .negativeButton(text = "No")
                    .positiveButton(text = "Yes") { dialog ->
                        dialog.dismiss()
                        val todo = Todo(
                                id = intent.getIntExtra("todoId", 0),
                                taskId = intent.getIntExtra("taskId", 0),
                                name = intent.getStringExtra("todoName"),
                                desc = intent.getStringExtra("todoDesc"),
                                status = TodoStatus.FINISHED.value,
                                doTime = intent.getStringExtra("todoDate"))
                        if (db.updateTodo(todo)) {
                            initStatus(TodoStatus.FINISHED.value)
                        } else {
                            toast("Oops, Mark task as finished fail due to database error.")
                        }
                    }
                    .show()
        }

        btnMarkIgnore.onClick {
            MaterialDialog(this@ProcessTodoActivity)
                    .title(text = "Tips")
                    .message(text = "Continue to mark this task as ignore?")
                    .negativeButton(text = "No")
                    .positiveButton(text = "Yes") { dialog ->
                        dialog.dismiss()
                        val todo = Todo(
                                id = intent.getIntExtra("todoId", 0),
                                taskId = intent.getIntExtra("taskId", 0),
                                name = intent.getStringExtra("todoName"),
                                desc = intent.getStringExtra("todoDesc"),
                                status = TodoStatus.IGNORED.value,
                                doTime = intent.getStringExtra("todoDate"))
                        if (db.updateTodo(todo)) {
                            initStatus(TodoStatus.IGNORED.value)
                        } else {
                            toast("Opps, Mark task as ignored fail due to database error.")
                        }
                    }
                    .show()
        }
        btnMarkDelete.onClick {
            MaterialDialog(this@ProcessTodoActivity)
                    .title(text = "Tips")
                    .message(text = "Continue to delete this task?")
                    .negativeButton(text = "No")
                    .positiveButton(text = "Yes") { dialog ->
                        dialog.dismiss()
                        if (db.deleteTodoById(intent.getIntExtra("todoId", 0))) {
                            //return to last page
                            onBackPressed()
                        } else {
                            toast("Oops, delete this task fail due to database error.")
                        }
                    }
                    .show()
        }
        btnMarkRedo.onClick {
            TimePickerBuilder(this@ProcessTodoActivity) { date, _ ->
                val todo = Todo(
                        id = intent.getIntExtra("todoId", 0),
                        taskId = intent.getIntExtra("taskId", 0),
                        name = intent.getStringExtra("todoName"),
                        desc = intent.getStringExtra("todoDesc"),
                        status = TodoStatus.TODO.value,
                        doTime = formateDate(date))
                if (db.updateTodo(todo)) {
                    initStatus(TodoStatus.TODO.value)
                    tvTodoDate.text = formateDate(date)
                } else {
                    toast("Oops, redo this task fail due to database error.")
                }
            }
                    .isDialog(false)
                    .setLabel("", "", "",
                            "", "", "")
                    .setTitleText("Chose Datetime")
                    .setTitleColor(resources.getColor(R.color.textPrimary))
                    .setCancelText("Cancel")
                    .setSubmitText("Ok")
                    .setRangDate(Calendar.getInstance(), Calendar.getInstance().apply {
                        add(Calendar.YEAR, 5)
                    })
                    .isCyclic(false)
                    .setType(listOf(true, true, true, true, true, true).toBooleanArray())
                    .setTextColorCenter(resources.getColor(R.color.textSecondary))
                    .setTextColorOut(resources.getColor(R.color.colorDivider))
                    .setSubmitColor(resources.getColor(R.color.colorSuccess))
                    .setCancelColor(resources.getColor(R.color.colorDanger))
                    .setDividerColor(resources.getColor(R.color.colorDivider))
                    .build()
                    .show()
        }
    }
}
