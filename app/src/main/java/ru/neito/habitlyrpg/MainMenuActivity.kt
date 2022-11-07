package ru.neito.habitlyrpg

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import java.util.prefs.AbstractPreferences

class MainMenuActivity : AppCompatActivity() {
    lateinit var sharePref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        sharePref = getSharedPreferences("SHARED_PREF",MODE_PRIVATE)

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")

        uidText.text = "User ID :: $userId"
        mailText.text = "Email ID :: $emailId"

        logoutBT.setOnClickListener {
            val editor = sharePref.edit()
            editor.clear()
            editor.apply()
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this@MainMenuActivity, LoginActivity::class.java))
            finish()
        }
    }
}