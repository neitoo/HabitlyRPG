package ru.neito.habitlyrpg.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import ru.neito.habitlyrpg.Logic.Boss
import ru.neito.habitlyrpg.Model.Monster
import ru.neito.habitlyrpg.R

class BossFight : Fragment() {
    private lateinit var bossImageView: ImageView
    private lateinit var bossHealthBar: ProgressBar
    private lateinit var bossTextHp: TextView
    private lateinit var bossData: JSONArray
    private var currentBossIndex = 0
    private lateinit var monsterList: MutableList<Monster>
    private lateinit var bossClicker: Boss
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_boss_fight, container, false)

        bossImageView = view.findViewById(R.id.monsterView)
        bossHealthBar = view.findViewById(R.id.progressBossHp)
        bossTextHp = view.findViewById(R.id.textHp)
        monsterList = mutableListOf()
        val gson = Gson()
        val jsonString = requireContext().assets.open("Boss.json").bufferedReader().use { it.readText() }
        val json = JSONObject(jsonString)
        val monsterJsonArray = json.getJSONArray("monster")


        for (i in 0 until monsterJsonArray.length()) {
            val monsterJsonObject = monsterJsonArray.getJSONObject(i)
            val monster = gson.fromJson(monsterJsonObject.toString(), Monster::class.java)
            monsterList.add(monster)
        }
        bossClicker = Boss(context,bossImageView, bossHealthBar, bossTextHp, monsterList)
        bossImageView.setOnClickListener {
            bossClicker.onBossClick()
        }
        return view
    }


}