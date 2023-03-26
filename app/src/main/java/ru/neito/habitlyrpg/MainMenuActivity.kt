package ru.neito.habitlyrpg

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_info_account.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import ru.neito.habitlyrpg.Logic.AlarmService
import ru.neito.habitlyrpg.Logic.HabitRewarder
import ru.neito.habitlyrpg.Model.Habits
import java.util.prefs.AbstractPreferences

class MainMenuActivity : AppCompatActivity() {
    private lateinit var sharePref: SharedPreferences
    private lateinit var mDataBase: DatabaseReference
    private lateinit var  userID: String

    lateinit var moneyValue: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        sharePref = getSharedPreferences("SHARED_PREF",MODE_PRIVATE)
        userID = sharePref.getString("user_id","")!!
        readData(userID)

        replaceFragment(Tasks())
        startService(Intent(this, AlarmService::class.java))

        navigation_view.setOnItemSelectedListener {
            when(it.itemId){
                R.id.tasks -> replaceFragment(Tasks())
                R.id.store -> replaceFragment(Shop())
                R.id.bosses -> replaceFragment(BossFight())
                else -> {}
            }
            true
        }

        setting_btn.setOnClickListener {
            val intent = Intent(this@MainMenuActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        
        addTaskBtn.setOnClickListener {
            val intent = Intent(this,CreateHabitActivity::class.java)
            startActivity(intent)
        }
    }

    private  fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    private fun readData(userID:String) {
        mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userNickname.text = snapshot.child("username").value as CharSequence?
                val HPvalue = (snapshot.child("hp").value as Long).toInt()
                val lvlValue = (snapshot.child("lvl").value as Long).toInt()
                val expValue = (snapshot.child("experience").value as Long).toInt()
                moneyValue = (snapshot.child("money").value as Long).toString()
                progressHp.progress = HPvalue
                textHp.text = "Здоровье $HPvalue/100"

                progressLvl.progress = expValue
                textLvl.text = "Уровень $lvlValue ($expValue/100)"

                cointCountText.text = moneyValue
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseDB", "Failed to read value.", error.toException())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        readData(userID)
    }
}