package com.createsapp.kotlineatitv2server.ui.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.createsapp.kotlineatitv2server.callback.IOrderCallbackListener
import com.createsapp.kotlineatitv2server.common.Common
import com.createsapp.kotlineatitv2server.model.FoodModel
import com.createsapp.kotlineatitv2server.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class OrderViewModel : ViewModel(), IOrderCallbackListener {

    private val orderModelList = MutableLiveData<List<OrderModel>>()
    val messageError = MutableLiveData<String>()
    private val orderCallbackListener: IOrderCallbackListener

    init {
        orderCallbackListener = this
    }

    fun getOrderModelList(): MutableLiveData<List<OrderModel>> {
        loadOrder(0)
        return orderModelList
    }

    private fun loadOrder(status: Int) {
        val tempList: MutableList<OrderModel> = ArrayList()
        val orderRef = FirebaseDatabase.getInstance()
            .getReference(Common.ORDER_REF)
            .orderByChild("orderStatus")
            .equalTo(status.toDouble())
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val orderModel = itemSnapShot.getValue(OrderModel::class.java)
                    orderModel!!.key = itemSnapShot.key
                    tempList.add(orderModel)
                }

                orderCallbackListener.onOrderLoadSuccess(tempList)

            }

            override fun onCancelled(error: DatabaseError) {
                orderCallbackListener.onOrderLoadFailed(error.message)
            }

        })

    }

    override fun onOrderLoadSuccess(orderModel: List<OrderModel>) {
        if (orderModel.size > 0) {
            Collections.sort(orderModel){t1,t2 ->
                if (t1.createDate<t2.createDate) return@sort-1
                if (t1.createDate == t2.createDate) 0 else 1
            }
            orderModelList.value = orderModel
        }
    }

    override fun onOrderLoadFailed(message: String) {
        messageError.value = message
    }

}