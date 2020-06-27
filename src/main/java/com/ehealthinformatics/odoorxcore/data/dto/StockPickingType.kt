package com.ehealthinformatics.odoorxcore.data.dto

import com.ehealthinformatics.odoorxcore.core.orm.OValues

class StockPickingType(var id: Int, var name: String) : DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}