package com.ehealthinformatics.odoorxcore.data.dto

import com.ehealthinformatics.odoorxcore.core.orm.OValues

interface DTO {
    fun toOValues(): OValues
}
