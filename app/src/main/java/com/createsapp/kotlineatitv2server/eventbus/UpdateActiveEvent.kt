package com.createsapp.kotlineatitv2server.eventbus

import com.createsapp.kotlineatitv2server.model.ShipperModel

class UpdateActiveEvent(val shipperModel: ShipperModel, var active: Boolean) {
}