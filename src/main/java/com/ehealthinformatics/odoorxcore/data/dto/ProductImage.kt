package com.ehealthinformatics.odoorxcore.data.dto

import android.graphics.Bitmap
import com.ehealthinformatics.odoorxcore.core.orm.OValues
import com.ehealthinformatics.odoorxcore.core.utils.BitmapUtils
import com.ehealthinformatics.odoorxcore.data.db.Columns
import java.io.Serializable

data class ProductImage(var id: Int, var serverId: Int, var image: Bitmap) : DTO, Serializable {

    override fun toString(): String {
        return BitmapUtils.toBase64(image)
    }

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.ProductCol.image, BitmapUtils.toBase64(image))
       
        return oValues
    }

}