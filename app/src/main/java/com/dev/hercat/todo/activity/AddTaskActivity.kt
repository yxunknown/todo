package com.dev.hercat.todo.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.transition.Explode
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dev.hercat.todo.R
import com.dev.hercat.todo.data.Task
import kotlinx.android.synthetic.main.activity_add_task.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class AddTaskActivity : AppCompatActivity() {

    val colors = com.dev.hercat.todo.tools.colors
    var currentColor: Pair<String, String> = "" to ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_add_task)
        edtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvTaskName.text = s.toString()
            }
        })
        btnCancel.onClick { onBackPressed() }
        gvTheme.adapter = GridThemAdapter(applicationContext)
        gvTheme.setOnItemClickListener { _, view, position, _ ->
            val color = colors[position]
            currentColor = color
            taskContainer.setBackgroundColor(Color.parseColor(color.first))
            YoYo.with(Techniques.BounceIn)
                    .duration(500)
                    .playOn(view)
        }
        currentColor = colors[0]
        taskContainer.setBackgroundColor(Color.parseColor(currentColor.first))

        btnSubmit.onClick {
            if (edtName.text.toString() != "") {
                val task = Task(1, edtName.text.toString(), currentColor.first)
                if (db.insertTask(task)) {
                    onBackPressed()
                }
            } else {
                edtName.error = "plz input the name of your new task"
                YoYo.with(Techniques.Bounce)
                        .duration(500)
                        .playOn(edtName)
            }
        }

    }
}
class GridThemAdapter(val context: Context): BaseAdapter() {
    val colors = com.dev.hercat.todo.tools.colors

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val color = colors[position]
        val v = LayoutInflater.from(context).inflate(R.layout.theme_item, null)
        val background = v.findViewById<View>(R.id.themeColor)
        val name = v.findViewById<TextView>(R.id.tvThemeName)
        background.setBackgroundColor(Color.parseColor(color.first))
        name.text = color.second.toUpperCase()
        return v
    }

    override fun getItem(position: Int): Any {
        return colors[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return colors.size
    }
}