package com.createsapp.kotlineatitv2server.eventbus

import com.createsapp.kotlineatitv2server.model.SizeModel

class UpdateSizeModel {
    var sizeModelList: List<SizeModel>? = null
    constructor()
    constructor(sizeMdelList: List<SizeModel>?){
        this.sizeModelList = sizeModelList
    }

}