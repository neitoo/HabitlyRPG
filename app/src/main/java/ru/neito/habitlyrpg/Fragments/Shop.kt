package ru.neito.habitlyrpg.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import ru.neito.habitlyrpg.Activity.MainMenuActivity
import ru.neito.habitlyrpg.Model.DataShop
import ru.neito.habitlyrpg.Model.ShopAdapter
import ru.neito.habitlyrpg.R


class Shop : Fragment() {
    private var items: MutableList<DataShop> = mutableListOf()
    private lateinit var recViewShop: RecyclerView
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var moneyValue: () -> String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)
        moneyValue = { (activity as MainMenuActivity).moneyValue }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recViewShop = view.findViewById(R.id.shopRecycler)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        recViewShop.layoutManager = staggeredGridLayoutManager

        parseJson()
        shopAdapter = ShopAdapter(requireContext(),items,moneyValue)
        recViewShop.adapter = shopAdapter

    }

    private fun parseJson() {
        try {
            val assetManager = context?.assets
            val inputStream = assetManager?.open("shop.json")
            val jsonString = inputStream?.bufferedReader().use { it?.readText() }
            val gson = Gson()
            val itemShop: List<DataShop> = gson.fromJson(jsonString, JsonObject::class.java)
                .get("items")
                .asJsonArray
                .map {
                    gson.fromJson(it, DataShop::class.java)
                }
            items.addAll(itemShop)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


}