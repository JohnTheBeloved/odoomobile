package com.ehealthinformatics.odoorx.core.data.dto

import com.ehealthinformatics.odoorx.core.base.orm.OValues

open class Partner(var id: Int, var serverId: Int?, var displayName: String?, var email: String, var phone: String, var company: Company?
                   , var address: String, var state: State?, var locality: String) : DTO {

    var fullAddress: String = ""
    var fullContact = ""
    var accountRececivable: Account? = null
    override fun toOValues(): OValues {
        return OValues()
    }

}
