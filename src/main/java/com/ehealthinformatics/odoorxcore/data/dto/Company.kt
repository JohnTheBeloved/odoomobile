package com.ehealthinformatics.odoorxcore.data.dto

import com.ehealthinformatics.odoorxcore.core.orm.OValues
import com.ehealthinformatics.odoorxcore.data.db.Columns

class Company(id: Int, serverId: Int?, var name: String?, var currency: Currency?):
        Partner(id ,  serverId ,  name ,  "" ,  "" ,  null,
        "" ,  null, "" ) {

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        oValues.put(Columns.name, name)
        oValues.put(Columns.Company.currency_id, currency?.id)
        return oValues
    }

}