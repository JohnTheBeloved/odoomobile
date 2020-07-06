package com.ehealthinformatics.odoorx.core.data.dao

import com.ehealthinformatics.odoorx.core.base.orm.ODataRow
import com.ehealthinformatics.odoorx.core.data.dto.DTO

interface Dao {

    fun <T> fromRow(oDataRow: ODataRow): T
    fun <T> get(id: Int) : T
    fun <T> put(dto: DTO) : Boolean

}
