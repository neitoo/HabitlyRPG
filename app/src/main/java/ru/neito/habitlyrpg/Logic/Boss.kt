package ru.neito.habitlyrpg.Logic

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ru.neito.habitlyrpg.Model.Monster

class Boss(
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
            currentBossIndex++
            if (currentBossIndex >= monsterList.size) {
                currentBossIndex = 0
            }
            setBoss(currentBossIndex)
        }
        bossHealthBar.progress = currentBossHp
        bossTextHp.text = "Здоровье $currentBossHp/${bossHealthBar.max}"

    }
}