package ru.neito.habitlyrpg.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_shop.view.*
import kotlinx.android.synthetic.main.shop_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.neito.habitlyrpg.R

class ShopAdapter(private var shopItems: MutableList<DataShop>) :
    RecyclerView.Adapter<ShopAdapter.ShopHolder>(){

    private val storage = FirebaseStorage.getInstance()
    class ShopHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card = itemView.shopCard
        val image = itemView.itemShop
        val price = itemView.textPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_layout, parent, false)
        return ShopHolder(view)
    }

    override fun onBindViewHolder(holder: ShopHolder, position: Int) {
        val item = shopItems[position]
        holder.price.text = "${item.price}"
        val storageRef = storage.reference.child(item.image)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.image)
        }
    }

    override fun getItemCount() = shopItems.size


}