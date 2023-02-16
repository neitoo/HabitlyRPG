package ru.neito.habitlyrpg.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.shop_layout.view.*
import ru.neito.habitlyrpg.R

class ShopAdapter(private var shopItems: MutableList<DataShop>) :
    RecyclerView.Adapter<ShopAdapter.ShopHolder>(){

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
        Picasso.get().load(item.image).into(holder.image)
    }

    override fun getItemCount() = shopItems.size


}