package com.ehealthinformatics.odoorxcore.data.dto

import com.ehealthinformatics.odoorxcore.core.orm.OValues
import com.ehealthinformatics.odoorxcore.data.db.Columns

class Location(var id: Int, var name: String) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.name, name)
        return oValues
    }
}