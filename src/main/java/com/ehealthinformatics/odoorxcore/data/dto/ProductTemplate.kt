package com.ehealthinformatics.odoorxcore.data.dto

import com.ehealthinformatics.odoorxcore.core.orm.OValues

class ProductTemplate(var id: Int?, var  name: String?, var active: Boolean?, 
var isMedicine: Boolean?, var description: String?, 
var uom: Uom?, var category: Category?, var websiteImages: List<ProductImage>) : DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}
