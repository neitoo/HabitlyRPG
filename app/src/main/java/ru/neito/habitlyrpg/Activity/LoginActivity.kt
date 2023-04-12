package ru.neito.habitlyrpg.Activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import ru.neito.habitlyrpg.R

class LoginActivity : AppCompatActivity() {
    lateinit var sharePref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharePref = getSharedPreferences("SHARED_PREF",MODE_PRIVATE)

        val emailShare = sharePref.getString("email","")
        val passwordShare = sharePref.getString("password","")
        val checkboxShare = sharePref.getBoolean("rememberMe", false)

        rememberMeCheck.isChecked = checkboxShare

        if (rememberMeCheck.isChecked){
            emailPlain.setText(emailShare)
            passwordPlain.setText(passwordShare)

        }




        registerBtn.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            when{
                TextUtils.isEmpty(emailPlain.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Пожалуйста введите email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(passwordPlain.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Пожалуйста введите пароль.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else ->{
                    var editor = sharePref.edit()
                    val email: String = emailPlain.text.toString().trim{ it <= ' '}
                    val password: String =passwordPlain.text.toString().trim {it <= ' '}

                    if (rememberMeCheck.isChecked){
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.putBoolean("rememberMe", true)
                        editor.apply()
                    }
                    else if(!rememberMeCheck.isChecked){
                        editor.putString("email", "")
                        editor.putString("password", "")
                        editor.putBoolean("rememberMe", false)
                        editor.apply()
                    }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            userLogin(task, email)
                        }
                }
            }
        }
    }

    private fun userLogin(
        task: Task<AuthResult>,
        email: String
    ) {
        if (task.isSuccessful) {
            val firebaseUser: FirebaseUser = task.result!!.user!!

            Toast.makeText(
                this@LoginActivity,
                "Добро пожаловать в HabitlyRPG.",
                Toast.LENGTH_SHORT
            ).show()

            val intent =
                Intent(this@LoginActivity, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            var editor = sharePref.edit()
            editor.putString("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
            editor.apply()
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(
                this@LoginActivity,
                task.exception!!.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}