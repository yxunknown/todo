package com.dev.hercat.todo.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dev.hercat.todo.R
import com.dev.hercat.todo.data.Todo
import com.dev.hercat.todo.data.TodoStatus
import kotlinx.android.synthetic.main.activity_new_todo.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class NewTodoActivity : AppCompatActivity() {
    val currentDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo)
        tvTaskName.text = intent.getStringExtra("name")
        tvTaskName.textColor = Color.parseColor(intent.getStringExtra("color"))
        btnTodoDate.onClick {
            TimePickerBuilder(this@NewTodoActivity) { date, _ ->
                currentDate.time = date
                btnTodoDate.textColor = resources.getColor(R.color.textPrimary)
                btnTodoDate.text = formateDate(date)
            }
                    .isDialog(false)
                    .setLabel("", "", "",
                            "", "", "")
                    .setTitleText("Chose Datetime")
                    .setTitleColor(resources.getColor(R.color.textPrimary))
                    .setCancelText("Cancel")
                    .setSubmitText("Ok")
                    .setDate(currentDate)
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
        btnCancel.onClick { onBackPressed() }
        btnSubmit.onClick { addNewTodo() }
    }

    fun addNewTodo() {
        val name = edtTodoName.text.toString()
        val desc = edtTodoDesc.text.toString()
        val date = btnTodoDate.text.toString()
        fun validateFiled(): Boolean {
           return if (name == "" || desc == "" || date.length != 19) {
               if (name == "") {
                   edtTodoName.error = "Plz input the name of your new todo"
               }
               if (desc == "") {
                   edtTodoDesc.error = "Plz input the describe of your new todo"
               }
               if (date.length != 19) {
                   YoYo.with(Techniques.Pulse)
                           .repeat(2)
                           .playOn(btnTodoDate)
                   toast("Plz select the datetime")
               }
               false
           } else true
        }
        if (validateFiled()) {
            val id = intent.getIntExtra("id", -1)
            if (id == -1) {
                toast("parameter error")
            } else {
                val todo = Todo(id = -1,
                        taskId = id,
                        name = name,
                        desc = desc,
                        status = TodoStatus.TODO.value,
                        doTime = date)
                if (db.insertTodo(todo)) {
                    onBackPressed()
                } else {
                    toast("store new todo failed")
                }
            }
        }
    }
}
