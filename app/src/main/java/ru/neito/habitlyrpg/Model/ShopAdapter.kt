package ru.neito.habitlyrpg.Model

import android.app.AlertDialog
import android.content.Context
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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_dialog_buy.view.*
import kotlinx.android.synthetic.main.shop_layout.view.*
import ru.neito.habitlyrpg.R

class ShopAdapter(private var context: Context,
                  private var shopItems: List<DataShop>,
                  private val moneyValue: () -> String,
                  private var userLvl: Int
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

        holder.card.isEnabled = userLvl >= item.atLvl
        holder.card.alpha = if (holder.card.isEnabled) 1f else 0.7f
        holder.availability.text = if (holder.card.isEnabled) "Доступно" else "С ${item.atLvl} уровня"

        holder.price.text = "${item.price}"
        val storageRef = storage.reference.child(item.image)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(holder.image)
        }

        holder.card.setOnClickListener {
            isItemPurchased(item.id) { isPurchased ->
                val dialogView = if (isPurchased) {
                    LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_dialog_use, null)
                } else {
                    LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_dialog_buy, null)
                }

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
                dialogView.textDamageInfo.text = if (currentItem.id == 1) "" else "УРОН"
                dialogView.textDamageInfo2.text = if (currentItem.id == 1) "" else "+${currentItem.damage}"
                dialogView.textYouBalanc?.text = "Ваш баланс: $currentValue"
                dialogView.textCost?.text = "Цена: ${currentItem.price}"

                val dialogBuilder = AlertDialog.Builder(holder.itemView.context, R.style.CustomDialogTheme)
                dialogBuilder.setView(dialogView)
                val dialog = dialogBuilder.create()
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
    }

    override fun getItemCount() = shopItems.size

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
                        val purchasedItems = snapshot.child("purchasedItems").value as? String
                        purchasedItemsRef.setValue(purchasedItems?.let { "$it,${currentItem.id}" } ?: "${currentItem.id}")
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

    fun isItemPurchased(itemId: Int, callback: (Boolean) -> Unit) {
        val sharePref = context.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val userID = sharePref.getString("user_id", "")!!
        val mDataBase = FirebaseDatabase.getInstance().getReference("User")
        val purchasedItemsRef = mDataBase.child(userID).child("purchasedItems")

        purchasedItemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val purchasedItems = snapshot.getValue(String::class.java)
                val isPurchased = purchasedItems?.split(",")?.contains(itemId.toString()) ?: false
                callback(isPurchased)
            }

            override fun onCancelled(error: DatabaseError) {
                // обработка ошибки
            }
        })
    }

}