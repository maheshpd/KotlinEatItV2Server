package com.createsapp.kotlineatitv2server.common

import com.createsapp.kotlineatitv2server.model.CategoryModel
import com.createsapp.kotlineatitv2server.model.ServerUserModel

object Common {

    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUM_COUNT: Int = 0
    var categorySelected: CategoryModel?=null
    val SERVER_REF = "Server"
    var currentServerUser: ServerUserModel?=null
    const val CATEGORY_REF: String = "Category"
}