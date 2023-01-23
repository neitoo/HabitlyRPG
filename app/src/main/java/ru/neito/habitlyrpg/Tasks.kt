package ru.neito.habitlyrpg

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.fragment_tasks.*


class Tasks : Fragment() {
    private lateinit var sharePref: SharedPreferences
    private lateinit var mDataBase: DatabaseReference
    private lateinit var  userID: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        readData(userID)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharePref = this.requireActivity().getSharedPreferences("SHARED_PREF", AppCompatActivity.MODE_PRIVATE)
        userID = sharePref.getString("user_id","")!!
        readData(userID)
    }

    private fun readData(userID:String) {

        mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).get().addOnSuccessListener {
            val HPvalue = (it.child("hp").value as Long).toInt()
            val lvlValue = (it.child("lvl").value as Long).toInt()
            val expValue = (it.child("experience").value as Long).toInt()
            val moneyValue = (it.child("money").value as Long).toString()
            progressHp.progress = HPvalue
            textHp.text = "Здоровье $HPvalue/100"

            progressLvl.progress = expValue
            textLvl.text = "Уровень $lvlValue ($expValue/100)"

            cointCountText.text = moneyValue


        }
    }

}