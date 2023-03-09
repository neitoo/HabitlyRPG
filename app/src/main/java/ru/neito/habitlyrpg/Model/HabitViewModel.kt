package ru.neito.habitlyrpg.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HabitViewModel : ViewModel() {
    private val habitsLiveData = MutableLiveData<List<Habit>>()

    fun getHabitsLiveData(): LiveData<List<Habit>> {
        return habitsLiveData
    }

    fun updateHabits(habits: List<Habit>) {
        habitsLiveData.value = habits
    }
}