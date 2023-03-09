package ru.neito.habitlyrpg.Logic

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import ru.neito.habitlyrpg.Model.Habit
import ru.neito.habitlyrpg.Model.HabitAdapter
import ru.neito.habitlyrpg.Model.HabitViewModel
import ru.neito.habitlyrpg.Model.Habits
import java.io.File
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val path = context?.filesDir?.absolutePath
        val file = File("$path/habit.json")
        if (file.exists() && file.length() > 0) {
            val jsonString = file.readText()
            val gson = Gson()
            val habits = gson.fromJson(jsonString, JsonObject::class.java)
                .get("habits")
                .asJsonArray
                .map {
                    gson.fromJson(it, Habit::class.java)
                }
            for (habit in habits) {
                if (habit.type == 1 && habit.complete) {
                    habit.complete = false
                }
                if (habit.type == 2 && !habit.complete) {
                    habit.complete = true
                }
            }
            val habitsJsonString = gson.toJson(mapOf("habits" to habits))
            file.writeText(habitsJsonString)
            val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)
                .create(HabitViewModel::class.java)

            viewModel.updateHabits(habits)
        }

    }

}