package com.ehealthinformatics.odoorx.core.data.dto

import com.ehealthinformatics.odoorx.core.base.orm.OValues

class StockPicking(var id: Int?, var name: String?) : DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}