package com.createsapp.kotlineatitv2server.callback

import com.createsapp.kotlineatitv2server.model.CategoryModel

interface ICategoryCallbackListener {

    fun onCategoryLoadSuccess(categoriesList: List<CategoryModel>)
    fun onCategoryLoadFailed(message: String)

}