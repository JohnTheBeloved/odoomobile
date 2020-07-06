package com.ehealthinformatics.odoorx.core.data.dto

import com.ehealthinformatics.odoorx.core.base.orm.OValues

open class User(var id: Int, var serverId: Int?, var partner: Partner?, var company: Company) : DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }

}