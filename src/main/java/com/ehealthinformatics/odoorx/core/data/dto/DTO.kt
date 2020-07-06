package com.ehealthinformatics.odoorx.core.data.dto

import com.ehealthinformatics.odoorx.core.base.orm.OValues

interface DTO {
    fun toOValues(): OValues
}
