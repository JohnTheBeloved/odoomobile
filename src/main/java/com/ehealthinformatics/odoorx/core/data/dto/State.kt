package com.ehealthinformatics.odoorx.core.data.dto

import com.ehealthinformatics.odoorx.core.base.orm.OValues
import com.ehealthinformatics.odoorx.core.data.db.Columns

class State(var id: Int,var server_id: Int, var name: String, var country: Country) : DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        oValues.put(Columns.name, name)
        return oValues
    }
}