package com.createsapp.kotlineatitv2server.eventbus

import com.createsapp.kotlineatitv2server.model.AddonModel
import com.createsapp.kotlineatitv2server.model.SizeModel

class UpdateAddonModel {
    var addonModelList: List<AddonModel>? = null
    constructor()
    constructor(addonModelList: List<AddonModel>?){
        this.addonModelList = addonModelList
    }

}