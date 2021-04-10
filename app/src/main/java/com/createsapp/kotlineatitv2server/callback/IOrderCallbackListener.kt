package com.createsapp.kotlineatitv2server.callback

import com.createsapp.kotlineatitv2server.model.CategoryModel
import com.createsapp.kotlineatitv2server.model.OrderModel

interface IOrderCallbackListener {
    fun onOrderLoadSuccess(orderModel: List<OrderModel>)
    fun onOrderLoadFailed(message: String)
}