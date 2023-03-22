package ru.neito.habitlyrpg.Model

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

class ShopAdapter(private var shopItems: MutableList<DataShop>, private var moneyValue: String) :
    RecyclerView.Adapter<ShopAdapter.ShopHolder>(){

    private val storage = FirebaseStorage.getInstance()
    class ShopHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card = itemView.shopCard
        val image = itemView.itemShop
        val price = itemView.textPrice
        val damage = itemView.textDamage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_layout, parent, false)
        return ShopHolder(view)
    }

    override fun onBindViewHolder(holder: ShopHolder, position: Int) {
        val item = shopItems[position]
        holder.price.text = "${item.price}"
        holder.damage.text = "+${item.damage} урона"
        val storageRef = storage.reference.child(item.image)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.image)
        }

        holder.card.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_dialog_buy, null)

            val currentItem = shopItems[position]
            val storageRef = storage.reference.child(currentItem.image)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get()
                    .load(uri)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(dialogView.itemBuyView)
            }

            dialogView.textNameBuy.text = currentItem.name
            dialogView.textDescBuy.text = currentItem.description
            dialogView.textDamageInfo2.text = "+${currentItem.damage}"
            dialogView.textYouBalanc.text = "Ваш баланс: ${moneyValue}"
            dialogView.textCost.text = "Цена: " + currentItem.price.toString()


            val dialog = AlertDialog.Builder(holder.itemView.context, R.style.CustomDialogTheme)
                .setView(dialogView)
                .create()
            dialog.show()
            dialogView.okBuyBtn.setOnClickListener {
                dialog.dismiss()

            }
            dialogView.cancelBuyBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun getItemCount() = shopItems.size
}