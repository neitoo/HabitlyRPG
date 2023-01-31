package ru.neito.habitlyrpg

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_tasks.*
import org.json.JSONException
import org.json.JSONObject
import ru.neito.habitlyrpg.classLogic.AdapterClass
import ru.neito.habitlyrpg.classLogic.DataClass
import java.io.File
import java.io.IOException
import java.io.InputStream


class Tasks : Fragment() {
    private var titleList: ArrayList<DataClass> = ArrayList()
    lateinit var title: Array<String>
    lateinit var id: Array<Int>
    lateinit var type: Array<Int>
    private lateinit var recView: RecyclerView
    private lateinit var helperAdapter: AdapterClass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()
        recView = view.findViewById(R.id.recyclerView)
        var linearLayoutManager = LinearLayoutManager(context)
        recView.layoutManager = linearLayoutManager


        helperAdapter = AdapterClass(titleList,context)
        recView.adapter = helperAdapter
    }

    override fun onResume() {
        super.onResume()
        dataInitialize()
        helperAdapter.notifyDataSetChanged()
    }

    private fun dataInitialize() {

        try {
            val path = context?.filesDir?.absolutePath
            val jsonString = File("$path/habit.json").readText()
            val gson = Gson()
            val habits = gson.fromJson(jsonString, JsonObject::class.java)
                .get("habits")
                .asJsonArray
                .map {
                    gson.fromJson(it, DataClass::class.java)
                }
            titleList.clear()
            titleList.addAll(habits)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


}