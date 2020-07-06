package com.ehealthinformatics.odoorx.core.data.dto

import com.ehealthinformatics.odoorx.core.base.orm.OValues
import com.ehealthinformatics.odoorx.core.data.db.Columns

class Medicine(var id: Int, var name: String) : DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        oValues.put(Columns.name, name)
        return oValues
    }
}