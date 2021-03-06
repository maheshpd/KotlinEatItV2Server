package com.createsapp.kotlineatitv2server.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.createsapp.kotlineatitv2server.R
import com.createsapp.kotlineatitv2server.callback.IRecyclerItemClickListener
import com.createsapp.kotlineatitv2server.common.Common
import com.createsapp.kotlineatitv2server.model.FoodModel
import org.greenrobot.eventbus.EventBus

class MyFoodListaAdapter(
    internal var context: Context,
    internal var foodList: List<FoodModel>
) :
    RecyclerView.Adapter<MyFoodListaAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        var txt_food_name: TextView? = null
        var txt_food_price: TextView? = null

        var img_food_image: ImageView? = null
        var img_fav: ImageView? = null
        var img_cart: ImageView? = null


        internal var listener: IRecyclerItemClickListener? = null

        fun setListener(listener: IRecyclerItemClickListener) {
            this.listener = listener
        }


        init {
            txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
            txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView
            img_food_image = itemView.findViewById(R.id.img_food_image) as ImageView

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!, adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_food_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodList.get(position).image).into(holder.img_food_image!!)
        holder.txt_food_name!!.text = foodList.get(position).name
        holder.txt_food_price!!.setText(StringBuilder("$").append(foodList.get(position).price.toString()))

        //Event
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = foodList.get(position)
                Common.foodSelected!!.key = pos.toString()
            }

        })

    }

    fun getItemAtPosition(pos: Int): FoodModel {
        return foodList.get(pos)
    }

}