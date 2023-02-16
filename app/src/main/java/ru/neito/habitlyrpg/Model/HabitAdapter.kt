package ru.neito.habitlyrpg.Model

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_layout.view.*
import ru.neito.habitlyrpg.R
import java.io.File

class HabitAdapter(private var titleList: ArrayList<Habit>,
                   private val context: Context?):
    RecyclerView.Adapter<HabitAdapter.ViewHolderClass>() {


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
        holder.checkHab.setOnCheckedChangeListener { _, isChecked ->
            titleList[position].complete = isChecked
            saveData()
        }
    }

    private fun saveData() {
        val gson = Gson()
        val jsonString = gson.toJson(mapOf("habits" to titleList))
        val path = context?.filesDir?.absolutePath
        File("$path/habit.json").writeText(jsonString)
    }


    class ViewHolderClass(itemView: View,
                          private var titleList: ArrayList<Habit>,
                          private val context: Context?,
                          private val adapter: HabitAdapter) : RecyclerView.ViewHolder(itemView) {
        val nameCategHab = itemView.nameCategHabit
        val checkHab = itemView.checkHabit
        var menu = itemView.moreBtn
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