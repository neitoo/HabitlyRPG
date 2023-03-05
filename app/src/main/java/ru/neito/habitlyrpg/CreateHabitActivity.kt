package ru.neito.habitlyrpg

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_create_habit.*
import ru.neito.habitlyrpg.Model.Habit
import java.util.*

class CreateHabitActivity : AppCompatActivity() {
    val context = this
    private val mapper = jacksonObjectMapper()
    private val fileName = "habit.json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)


        closeBtn.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        saveHabitBtn.setOnClickListener {
            when{
                TextUtils.isEmpty(nameHabitInput.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@CreateHabitActivity,
                        "Пожалуйста введите название.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                radioGroup.checkedRadioButtonId == -1 -> {
                    Toast.makeText(
                        this@CreateHabitActivity,
                        "Выберите один из типов.",
                        Toast.LENGTH_SHORT
                    ).show()

            }
            else ->{
                val currentTime = Calendar.getInstance().time
                val timeInMillis = currentTime.time
                val id = (timeInMillis / 1000L % Integer.MAX_VALUE).toInt()
                var nameHabit = nameHabitInput.text.toString().trim{ it <= ' '}
                var type = when(radioGroup.checkedRadioButtonId){
                    R.id.regularRB -> 1
                    R.id.harmfulRB -> 2
                    R.id.disposableRB -> 3
                    else -> {-1}
                }
                val completed = when(type){
                    1 -> false
                    2 -> true
                    3 -> false
                    else -> false
                }

                val fileInputStream = context.openFileInput(fileName)
                val habits = mapper.readValue<Map<String, List<Habit>>>(fileInputStream).toMutableMap()
                fileInputStream.close()

                habits["habits"] = habits["habits"]!!.plus(Habit(id, nameHabit, type, completed))
                val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                mapper.writeValue(fileOutputStream, habits)
                fileOutputStream.close()
                /*val fileInputStream = openFileInput(fileName)
                val habits = mapper.readValue<Map<String, List<Habit>>>(fileInputStream).toMutableMap()
                fileInputStream.close()

                habits["habits"] = habits["habits"]!!.plus(Habit(id, nameHabit, type, completed))
                val fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
                mapper.writeValue(fileOutputStream, habits)
                fileOutputStream.close()*/


                finish()
                }
            }
        }
    }
}