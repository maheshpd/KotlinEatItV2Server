package com.createsapp.kotlineatitv2server.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import com.createsapp.kotlineatitv2server.model.CategoryModel
import com.createsapp.kotlineatitv2server.model.FoodModel
import com.createsapp.kotlineatitv2server.model.ServerUserModel

object Common {


    val ORDER_REF: String = "Order"
    var foodSelected: FoodModel? = null
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUM_COUNT: Int = 0
    var categorySelected: CategoryModel? = null
    val SERVER_REF = "Server"
    var currentServerUser: ServerUserModel? = null
    const val CATEGORY_REF: String = "Category"


    fun setSpanString(welcome: String, name: String?, txtUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun setSpanStringColor(welcome: String, name: String?, txtUser: TextView?, color: Int) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtSpannable.setSpan(
            ForegroundColorSpan(color),
            0,
            name!!.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun convertStatusToString(orderStatus: Int): String? =
        when (orderStatus) {
            0 -> "Placed"
            1 -> "Shipping"
            2 -> "Shipped"
            -1 -> "Cancelled"
            else -> "Error"
        }

}