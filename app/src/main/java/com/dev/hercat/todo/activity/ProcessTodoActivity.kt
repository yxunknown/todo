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
                    resources.getString(R.string.status_todo) to resources.getString(R.string.status_todo_tip)
                }
                1 -> {
                    btnMarkDone.visibility = View.GONE
                    btnMarkIgnore.visibility = View.GONE
                    resources.getString(R.string.status_finished) to resources.getString(R.string.status_finished_tip)
                }
                2 -> {
                    resources.getString(R.string.status_expired) to resources.getString(R.string.status_expired_tip)
                }
                3 -> {
                    btnMarkIgnore.visibility = View.GONE
                    resources.getString(R.string.status_ignored) to resources.getString(R.string.status_ignored_tip)
                }
                else -> "NOT DEFINED" to "Opps, I have no idea about this"
            }
            tvTaskStatus.text = state.first
            btnStatusTips.onClick {
                MaterialDialog(this@ProcessTodoActivity)
                        .title(res = R.string.status_tips)
                        .message(text = state.second)
                        .positiveButton(res = R.string.positive_text)
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
                    .title(res = R.string.dialog_tip)
                    .message(res = R.string.mark_as_finished)
                    .negativeButton(res = R.string.negative_text)
                    .positiveButton(res = R.string.positive_text) { dialog ->
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
                            toast(R.string.mark_as_finished_error)
                        }
                    }
                    .show()
        }

        btnMarkIgnore.onClick {
            MaterialDialog(this@ProcessTodoActivity)
                    .title(res = R.string.dialog_tip)
                    .message(res = R.string.mark_as_ignored)
                    .negativeButton(res = R.string.negative_text)
                    .positiveButton(res = R.string.positive_text) { dialog ->
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
                            toast(R.string.mark_as_ignored_error)
                        }
                    }
                    .show()
        }
        btnMarkDelete.onClick {
            MaterialDialog(this@ProcessTodoActivity)
                    .title(res = R.string.dialog_tip)
                    .message(res = R.string.mark_as_deleted)
                    .negativeButton(res = R.string.negative_text)
                    .positiveButton(res = R.string.positive_text) { dialog ->
                        dialog.dismiss()
                        if (db.deleteTodoById(intent.getIntExtra("todoId", 0))) {
                            //return to last page
                            onBackPressed()
                        } else {
                            toast(R.string.mark_as_deleted_error)
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
                    toast(R.string.mark_as_redo_error)
                }
            }
                    .isDialog(false)
                    .setLabel("", "", "",
                            "", "", "")
                    .setTitleText(resources.getString(R.string.tap_to_chose_date))
                    .setTitleColor(resources.getColor(R.color.textPrimary))
                    .setCancelText(resources.getString(R.string.cancel_text))
                    .setSubmitText(resources.getString(R.string.submit_text))
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
