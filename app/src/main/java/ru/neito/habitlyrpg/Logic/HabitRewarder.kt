package ru.neito.habitlyrpg.Logic

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.firebase.database.*
import com.google.gson.Gson
import ru.neito.habitlyrpg.Model.Habit
import ru.neito.habitlyrpg.Model.Habits

class HabitRewarder(private val context: Context, habits: Habits) {

    private val habitsType1 = mutableListOf<Habit>()
    private val habitsType2 = mutableListOf<Habit>()
    private val habitsType3 = mutableListOf<Habit>()

    init {
        for (habit in habits.habits) {
            when (habit.type) {
                1 -> habitsType1.add(habit)
                2 -> habitsType2.add(habit)
                3 -> habitsType3.add(habit)
            }
        }
    }

    fun onHabitCompleted(habit: Habit, isChecked: Boolean) {
        val callback: (Int) -> Unit = { healthPoints ->
            if (habit.type == 1) {
                if (isChecked) {
                    // награда за выполнение
                    giveExperience(10)
                } else {
                    // штраф за невыполнение
                    takeHealth(10)
                    takeExperience(10)
                }
            } else if (habit.type == 2) {
                if (!isChecked) {
                    // награда за выполнение
                    giveExperience(10)
                } else {
                    // штраф за невыполнение
                    takeHealth(10)
                    takeExperience(10)
                }
            } else if (habit.type == 3) {
                if (isChecked) {
                    // награда за выполнение одноразовой привычки
                    giveExperience(20)
                    habitsType3.remove(habit)
                }
            }
            checkExperience()
        }

        getHealthPoints(callback)
    }

    
    private fun getHealthPoints(callback: (Int) -> Unit) {
        val sharePref = context.getSharedPreferences("SHARED_PREF", MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hp = (snapshot.child("hp").value as Long).toInt()
                // вызываем колбэк и передаем значение hp
                callback(hp)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("getHealthPoints", "Error getting data", error.toException())
            }
        })
    }

    private fun giveExperience(exp: Int) {
        val sharePref = context.getSharedPreferences("SHARED_PREF", MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).child("experience").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var experience = (currentData.value as Long).toInt()
                experience += exp
                currentData.value = experience.toLong()
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // обработка ошибок и обновление UI
            }
        })
    }

    private fun takeExperience(exp: Int) {
        val sharePref = context.getSharedPreferences("SHARED_PREF", MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).child("experience").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var experience = (currentData.value as Long).toInt()
                experience -= exp
                currentData.value = experience.toLong()
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // обработка ошибок и обновление UI
            }
        })
    }

    private fun takeHealth(healthPenalty: Int) {
        val sharePref = context.getSharedPreferences("SHARED_PREF", MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).child("hp").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var healthPoints = (currentData.value as Long).toInt()
                healthPoints -= healthPenalty
                currentData.value = healthPoints.toLong()

                // проверяем, достиг ли пользователь 0 hp
                if (healthPoints <= 0) {
                    // сброс до 1 уровня и сброс опыта до 0
                    currentData.value = 100
                    mDataBase.child(userID).child("lvl").setValue(1)
                    mDataBase.child(userID).child("experience").setValue(0)
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // обработка ошибок и обновление UI
            }
        })
    }

    private fun giveLvlUp() {
        val sharePref = context.getSharedPreferences("SHARED_PREF", MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).child("lvl").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var lvl = (currentData.value as Long).toInt()
                lvl++
                currentData.value = lvl.toLong()
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // обработка ошибок и обновление UI
            }
        })
    }

    private fun checkExperience() {
        val sharePref = context.getSharedPreferences("SHARED_PREF", MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val experience = (snapshot.child("experience").value as Long).toInt()

                if (experience >= 100) {
                    giveLvlUp()
                    takeExperience(100)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("checkExperience", "Error getting data", error.toException())
            }
        })
    }


}