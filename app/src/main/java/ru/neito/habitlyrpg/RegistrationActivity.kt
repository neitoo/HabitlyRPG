package ru.neito.habitlyrpg

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*
import ru.neito.habitlyrpg.Model.User

class RegistrationActivity : AppCompatActivity() {
    lateinit var sharePref: SharedPreferences
    private lateinit var mDataBaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        sharePref = getSharedPreferences("SHARED_PREF",MODE_PRIVATE)

        mDataBaseRef = FirebaseDatabase.getInstance().reference.child("User")
        registerBtn.setOnClickListener {
            when{
                TextUtils.isEmpty(emailPlain.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Пожалуйста введите email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(passwordPlain.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Пожалуйста введите пароль.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else ->{
                    val email: String = emailPlain.text.toString().trim{ it <= ' '}
                    val password: String =passwordPlain.text.toString().trim {it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Успешная регистрация в мире HabitlyRPG.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@RegistrationActivity, MainMenuActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                var editor = sharePref.edit()
                                editor.putString("userID", FirebaseAuth.getInstance().currentUser!!.uid)
                                editor.putString("email", email)
                                editor.apply()

                                val userID = sharePref.getString("userID","")
                                val email = sharePref.getString("email", "")
                                val username = "user"
                                val HP = 100
                                val Lvl = 1
                                val money = 0
                                val experience = 0
                                var newUser: User = User(userID,username,email,HP, experience,Lvl,money)
                                if (userID != null) {
                                    mDataBaseRef.child(userID).setValue(newUser)
                                }
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        backBtn.setOnClickListener {
            onBackPressed()

        }
    }
}