package com.dev.hercat.todo.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.dev.hercat.todo.R
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        btnBack.onClick { onBackPressed() }

        //init language setting
        val index = getSharedPreferences("language", Context.MODE_PRIVATE)
                .getInt("language", 0)
        tvLanguage.text = resources.getStringArray(R.array.languages)[index]

        //init notification
        val sp = getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE)
        val checked = sp.getBoolean("should_notification", true)
        notificationSwitcher.isChecked = checked
        val text = if (notificationSwitcher.isChecked) R.string.turn_on else R.string.turn_off
        notificationSwitcher.text = resources.getText(text)

        btnSetLanguage.onClick {
            val select = getSharedPreferences("language", Context.MODE_PRIVATE)
                        .getInt("language", 0)
            MaterialDialog(this@SettingActivity)
                    .title(res = R.string.language)
                    .listItemsSingleChoice(res = R.array.languages, initialSelection = select) {dialog, index, text ->
                        dialog.dismiss()
                        tvLanguage.text = text
                        val sp = getSharedPreferences("language", Context.MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putInt("language", index)
                        editor.apply()
                        updateLanguage()
                        MaterialDialog(this@SettingActivity)
                                .positiveButton(res = R.string.positive_text) {
                                    finish()
                                    startActivity(intentFor<MainActivity>())
                                }
                                .negativeButton(res = R.string.negative_text)
                                .message(res = R.string.set_language_tip)
                                .show()
                    }
                    .positiveButton(res = R.string.positive_text)
                    .negativeButton(res = R.string.negative_text)
                    .show()
        }
        btnSetNotification.onClick {
            val isCheck = notificationSwitcher.isChecked
            notificationSwitcher.isChecked = !isCheck
            val notificationStatusStr = if (notificationSwitcher.isChecked) R.string.turn_on else R.string.turn_off
            notificationSwitcher.text = resources.getString(notificationStatusStr)
            val editor = sp.edit()
            editor.putBoolean("should_notification", !isCheck)
            editor.apply()
        }
        btnAbout.onClick { startActivity(intentFor<AboutActivity>()) }
    }
}
