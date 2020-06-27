package com.ehealthinformatics.odoorxcore.data.dao;

import android.content.Context;

import com.ehealthinformatics.odoorxcore.core.orm.OModel;
import com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.odoorxcore.core.support.OUser;

public class ResPartnerCategory extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public ResPartnerCategory(Context context, OUser user) {
        super(context, "res.partner.category", user);
    }
}
