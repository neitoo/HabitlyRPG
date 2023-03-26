package ru.neito.habitlyrpg.Model

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_layout.view.*
import ru.neito.habitlyrpg.Logic.HabitRewarder
import ru.neito.habitlyrpg.R
import java.io.File
import java.util.*

class HabitAdapter(
    private var titleList: MutableList<Habit>,
    private val context: Context?,
    private val viewModel: HabitViewModel
) : RecyclerView.Adapter<HabitAdapter.ViewHolderClass>() {

    private val habitsObserver = Observer<List<Habit>> { habits ->
        titleList.clear()
        titleList.addAll(habits)
        notifyDataSetChanged()
    }


    init {
        viewModel.getHabitsLiveData().observeForever(habitsObserver)
    }
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        viewModel.getHabitsLiveData().removeObserver(habitsObserver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView,titleList,context,this)
    }


    override fun getItemCount(): Int {
        return titleList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = titleList[position]
        holder.nameCategHab.text = currentItem.title
        holder.checkHab.isChecked = titleList[position].complete
        if (titleList[position].complete) {
            holder.frame.setBackgroundResource(R.drawable.habit_bg_checked)
            holder.itemView.isEnabled = currentItem.type != 3
        } else {
            holder.frame.setBackgroundResource(R.drawable.habit_bg)
            holder.itemView.isEnabled = true
        }

        holder.itemView.setOnClickListener { view ->
            val isChecked = !holder.checkHab.isChecked
            titleList[position].complete = isChecked
            holder.checkHab.isChecked = isChecked
            saveData()

            val habitsInstance = Habits(titleList)
            HabitRewarder(context!!,habitsInstance).onHabitCompleted(currentItem, isChecked)
            if (isChecked) {
                holder.frame.setBackgroundResource(R.drawable.habit_bg_checked)
            } else {
                holder.frame.setBackgroundResource(R.drawable.habit_bg)
            }

            // Сортировка списка при изменении состояния чекбокса
            titleList.sortWith(compareBy({it.complete}, {it.id}))
            /*val fromPosition = position
            val toPosition = titleList.indexOf(currentItem)
            if (fromPosition != toPosition) {
                // Анимация перемещения карточки
                val animation = if (fromPosition > toPosition) {
                    AnimationUtils.loadAnimation(context, R.anim.slide_down)
                } else {
                    AnimationUtils.loadAnimation(context, R.anim.slide_up)
                }
                holder.itemView.startAnimation(animation)
                // Перемещение карточки
                Collections.swap(titleList, fromPosition, toPosition)
                notifyItemMoved(fromPosition, toPosition)
            }*/


            holder.itemView.post {
                notifyDataSetChanged()
            }
        }

    }


    private fun saveData() {
        val gson = Gson()
        val jsonString = gson.toJson(mapOf("habits" to titleList))
        val path = context?.filesDir?.absolutePath
        File("$path/habit.json").writeText(jsonString)
    }


    class ViewHolderClass(itemView: View,
                          private var titleList: MutableList<Habit>,
                          private val context: Context?,
                          private val adapter: HabitAdapter) : RecyclerView.ViewHolder(itemView) {
        val nameCategHab = itemView.nameCategHabit
        val checkHab = itemView.checkHabit
        var menu = itemView.moreBtn
        var frame = itemView.backgroundHabit
        init {

            menu.setOnClickListener { popupMenu(it) }

        }


        private fun popupMenu(v:View) {
            val id = titleList[adapterPosition].id
            val gson = Gson()
            val popupMenus = PopupMenu(context,v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.deleteHabit ->{
                        AlertDialog.Builder(context)
                            .setTitle("Удаление")
                            .setIcon(R.drawable.warning)
                            .setMessage("Вы хотите удалить привычку?")
                            .setPositiveButton("Да"){
                                dialog,_->
                                titleList.removeAt(adapterPosition)
                                val habits = mapOf("habits" to titleList)
                                val jsonString = gson.toJson(habits)
                                val path = context?.filesDir?.absolutePath
                                File("$path/habit.json").writeText(jsonString)

                                adapter.notifyItemRemoved(adapterPosition)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Нет"){
                                dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }

    }

}