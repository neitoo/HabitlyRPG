package ru.neito.habitlyrpg.Model

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_shop.view.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_dialog_buy.view.*
import kotlinx.android.synthetic.main.shop_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.neito.habitlyrpg.R

class ShopAdapter(private var context: Context,
                  private var shopItems: MutableList<DataShop>,
                  private val moneyValue: () -> String
    ) : RecyclerView.Adapter<ShopAdapter.ShopHolder>(){

    private val storage = FirebaseStorage.getInstance()

    class ShopHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card = itemView.shopCard
        val image = itemView.itemShop
        val price = itemView.textPrice
        val availability = itemView.textAvailability
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_layout, parent, false)
        return ShopHolder(view)
    }

    override fun onBindViewHolder(holder: ShopHolder, position: Int) {
        val item = shopItems[position]

        getLvl { userLvl ->
            if (userLvl >= item.atLvl) {
                holder.card.alpha = 1f
                holder.card.isEnabled = true
                holder.availability.text = "Доступно"
            } else {
                holder.card.alpha = 0.7f
                holder.card.isEnabled = false
                holder.availability.text = "С ${item.atLvl} уровня"
            }
        }
        holder.price.text = "${item.price}"
        val storageRef = storage.reference.child(item.image)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .into(holder.image)
        }

        holder.card.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_dialog_buy, null)

            val currentItem = shopItems[position]
            val currentValue = moneyValue()
            val storageRef = storage.reference.child(currentItem.image)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get()
                    .load(uri)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(dialogView.itemBuyView)
            }

            dialogView.textNameBuy.text = currentItem.name
            dialogView.textDescBuy.text = currentItem.description
            if(currentItem.id == 1){
                dialogView.textDamageInfo.text = ""
                dialogView.textDamageInfo2.text = ""
            }
            else{
                dialogView.textDamageInfo2.text = "+${currentItem.damage}"
            }
            dialogView.textYouBalanc.text = "Ваш баланс: ${currentValue}"
            dialogView.textCost.text = "Цена: " + currentItem.price.toString()


            val dialog = AlertDialog.Builder(holder.itemView.context, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create()
            dialog.show()
            dialogView.okBuyBtn.setOnClickListener {
                dialog.dismiss()
                buyItem(position)
            }
            dialogView.cancelBuyBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun getItemCount() = shopItems.size

    private fun getLvl(callback: (Int) -> Unit) {
        val sharePref = context.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        mDataBase.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userLvl = (snapshot.child("lvl").value as Long).toInt()
                callback(userLvl)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("getHealthPoints", "Error getting data", error.toException())
                callback(0)
            }
        })
    }

    fun buyItem(position: Int) {
        val sharePref = context.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        val currentItem = shopItems[position]
        val price = currentItem.price

        mDataBase.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userMoney = snapshot.child("money").value as Long
                if (userMoney >= price) {
                    if (currentItem.id != 1) {
                        val purchasedItemsRef = mDataBase.child(userID).child("purchasedItems")
                        val newItemRef = purchasedItemsRef.push()
                        newItemRef.setValue(currentItem.id)
                    } else {
                        val userHealth = snapshot.child("hp").value as Long
                        mDataBase.child(userID).child("hp").setValue(userHealth + 10)
                    }
                    mDataBase.child(userID).child("money").setValue(userMoney - price)
                    Toast.makeText(context,
                        "Предмет успешно куплен и установлен!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "У вас не достаточно средств для покупки.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("tag", "Database error: ${error.message}")
            }
        })
    }

}