package ru.neito.habitlyrpg.Activity

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*
import ru.neito.habitlyrpg.R

class SettingsActivity : AppCompatActivity() {
    lateinit var sharePref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharePref = getSharedPreferences("SHARED_PREF",MODE_PRIVATE)

        arrowBackBtn.setOnClickListener {
            onBackPressed()
        }

        userAccountBtn.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, InfoAccountActivity::class.java))

        }

        userLogoutBtn.setOnClickListener {
            val alertLogout = AlertDialog.Builder(this)
            alertLogout.setTitle("Выйти из аккаунта?")
            alertLogout.setIcon(R.mipmap.ic_launcher)
            alertLogout.setPositiveButton("Да"){dialogInterface: DialogInterface, id: Int ->
                val editor = sharePref.edit()
                editor.putBoolean("rememberMe", false)
                editor.apply()
                FirebaseAuth.getInstance().signOut()

                startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                finish()
            }
            alertLogout.setNegativeButton("Нет"){dialogInterface: DialogInterface, id: Int ->
                dialogInterface.dismiss()

            }
            alertLogout.show()
        }

        helpButton.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, HelpActivity::class.java))
        }
    }


}