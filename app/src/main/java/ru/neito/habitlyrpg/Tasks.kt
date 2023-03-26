package ru.neito.habitlyrpg

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import ru.neito.habitlyrpg.Logic.HabitRewarder
import ru.neito.habitlyrpg.Model.HabitAdapter
import ru.neito.habitlyrpg.Model.Habit
import ru.neito.habitlyrpg.Model.HabitViewModel
import ru.neito.habitlyrpg.Model.Habits
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class Tasks : Fragment() {
    private var titleList: MutableList<Habit> = ArrayList()
    lateinit var title: Array<String>
    lateinit var id: Array<Int>
    lateinit var type: Array<Int>
    private lateinit var recView: RecyclerView
    private lateinit var helperAdapter: HabitAdapter
    private lateinit var viewModel: HabitViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tasks, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HabitViewModel::class.java]
        val file = File(context?.filesDir, "habit.json")
        if (!file.exists()) {
            file.createNewFile()
            val habits = Habits(listOf<Habit>())
            val objectMapper = jacksonObjectMapper()
            val json = objectMapper.writeValueAsString(habits)
            file.writeText(json)
        }
        dataInitialize()
        recView = view.findViewById(R.id.recyclerView)
        var linearLayoutManager = LinearLayoutManager(context)
        recView.layoutManager = linearLayoutManager


        helperAdapter = HabitAdapter(titleList,context,viewModel)
        recView.adapter = helperAdapter

        viewModel.getHabitsLiveData().observe(viewLifecycleOwner) { habits ->
            titleList.clear()
            titleList.addAll(habits)
            helperAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        dataInitialize()
        helperAdapter.notifyDataSetChanged()
    }


    private fun dataInitialize() {

        try {
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
                titleList.clear()
                titleList.addAll(habits)
                titleList.sortWith(compareBy({it.complete}, {it.id}))

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }



}