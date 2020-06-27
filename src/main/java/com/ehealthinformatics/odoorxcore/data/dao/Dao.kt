package com.ehealthinformatics.odoorxcore.data.dao

import com.ehealthinformatics.odoorxcore.core.orm.ODataRow
import com.ehealthinformatics.odoorxcore.data.dto.DTO

interface Dao {

    fun <T> fromRow(oDataRow: ODataRow): T
    fun <T> get(id: Int) : T
    fun <T> put(dto: DTO) : Boolean

}
