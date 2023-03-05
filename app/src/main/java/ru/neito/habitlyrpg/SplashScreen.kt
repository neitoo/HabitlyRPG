package ru.neito.habitlyrpg

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash_screen.*


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    lateinit var sharePref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        progressBar.visibility = View.GONE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        sharePref = getSharedPreferences("SHARED_PREF",MODE_PRIVATE)

        val emailShare = sharePref.getString("email","")
        val passwordShare = sharePref.getString("password","")
        val checkboxShare = sharePref.getBoolean("rememberMe", false)




        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.VISIBLE
            if (checkboxShare){
                val email: String = emailShare.toString().trim{ it <= ' '}
                val password: String = passwordShare.toString().trim {it <= ' '}

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = View.GONE
                        userLogin(task, email)
                    }
            }
            else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        },2000)
    }
    private fun userLogin(
        task: Task<AuthResult>,
        email: String
    ) {
        if (task.isSuccessful) {
            val firebaseUser: FirebaseUser = task.result!!.user!!

            Toast.makeText(
                this@SplashScreen,
                "Добро пожаловать в HabitlyRPG.",
                Toast.LENGTH_SHORT
            ).show()

            val intent =
                Intent(this@SplashScreen, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            var editor = sharePref.edit()
            editor.putString("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
            editor.apply()
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(
                this@SplashScreen,
                task.exception!!.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}