package ru.neito.habitlyrpg.Logic

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.*
import ru.neito.habitlyrpg.Model.Monster

class Boss(
    private val context: Context?,
    private val bossImageView: ImageView,
    private val bossHealthBar: ProgressBar,
    private val bossTextHp: TextView,
    private val monsterList: List<Monster>
) {

    private var currentBossIndex = 0
    private var currentBossHp = 0

    init {
        setBoss(currentBossIndex)
    }

    private fun setBoss(index: Int) {
        val monster = monsterList[index]
        bossImageView.setImageDrawable(getImageResourceId(monster.filePathImage))
        bossHealthBar.max = monster.hp
        currentBossHp = monster.hp
        bossHealthBar.progress = currentBossHp
    }


    private fun getImageResourceId(filePath: String): Drawable? {
        val assetManager = bossImageView.context.assets
        val inputStream = assetManager.open(filePath)
        val drawable = Drawable.createFromStream(inputStream, null)
        return drawable

    }

    fun onBossClick() {
        currentBossHp -= 10
        if (currentBossHp <= 0) {
            val defeatedBoss = monsterList[currentBossIndex]
            currentBossIndex++
            giveCoins(defeatedBoss.currency)
            if (currentBossIndex >= monsterList.size) {
                currentBossIndex = 0
            }
            setBoss(currentBossIndex)
        }
        bossHealthBar.progress = currentBossHp
        bossTextHp.text = "Здоровье $currentBossHp/${bossHealthBar.max}"

    }

    private fun giveCoins(coins: Int) {
        val sharePref = context!!.getSharedPreferences("SHARED_PREF", MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).child("money").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var currentCoins = (currentData.value as Long).toInt()
                currentCoins += coins
                currentData.value = currentCoins.toLong()
                return Transaction.success(currentData)
            }
            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    // обработка ошибок
                } else {
                    // обновление UI
                }
            }
        })
    }
}