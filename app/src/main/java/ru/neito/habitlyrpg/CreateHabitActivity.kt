package ru.neito.habitlyrpg

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_habit.*
import kotlinx.android.synthetic.main.activity_login.*
import ru.neito.habitlyrpg.classLogic.DataClass
import java.io.File
import java.io.IOException
import java.util.*

class CreateHabitActivity : AppCompatActivity() {
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
                val completed = false

                val fileInputStream = openFileInput(fileName)
                val habits = mapper.readValue<Map<String, List<DataClass>>>(fileInputStream).toMutableMap()
                fileInputStream.close()

                habits["habits"] = habits["habits"]!!.plus(DataClass(id, nameHabit, type, completed))
                val fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
                mapper.writeValue(fileOutputStream, habits)
                fileOutputStream.close()

                finish()
                }
            }
        }







    }
}