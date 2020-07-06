package com.ehealthinformatics.odoorx.core.data.dto

import com.ehealthinformatics.odoorx.core.base.orm.OValues
import com.ehealthinformatics.odoorx.core.data.db.Columns

class Uom(var id: Int, var name: String) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.name, name)
        return oValues
    }
}