package ru.neito.habitlyrpg

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_info_account.*
import kotlinx.android.synthetic.main.layout_dialog.*
import kotlinx.android.synthetic.main.layout_dialog.view.*

class InfoAccountActivity : AppCompatActivity() {
    private lateinit var sharePref: SharedPreferences
    private lateinit var mDataBase: DatabaseReference
    private lateinit var userID: String
    private lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_account)


        mDataBase = FirebaseDatabase.getInstance().getReference("User")
        sharePref = getSharedPreferences("SHARED_PREF",MODE_PRIVATE)
        userID = sharePref.getString("user_id","")!!
        readData(userID)

        arrowBackSettingsBtn.setOnClickListener {
            onBackPressed()
        }

        idUserText.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("copy id",userID )
            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                this@InfoAccountActivity,
                "ID скопирован.",
                Toast.LENGTH_SHORT
            ).show()
        }

        nameUserText.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog,null)
        val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme).setView(dialogView).show()

        dialogView.editName.setText(name)
        dialogView.saveChangeBtn.setOnClickListener {
            builder.dismiss()

            writeData(dialogView,userID!!)

        }
        builder.cancelBtn.setOnClickListener {
            builder.dismiss()
        }
    }

    private fun writeData(mDialog: View, userID: String){
        val updateName = mDialog.editName.text.toString().trim()

        if(updateName.isEmpty()) {
            Toast.makeText(this,"Введено пустое имя.", Toast.LENGTH_SHORT).show()
            return
        }

        mDataBase.child(userID).child("username").setValue(updateName)
        readData(userID)
        Toast.makeText(this,"Имя изменено!", Toast.LENGTH_SHORT).show()
    }

    private fun readData(userID:String) {


        mDataBase.child(userID).get().addOnSuccessListener {
            idUserText.text = it.child("id").value as CharSequence?
            nameUserText.text = it.child("username").value as CharSequence?

            name = it.child("username").value as String
        }
    }

    override fun onStart() {
        super.onStart()
        readData(userID)
    }
}