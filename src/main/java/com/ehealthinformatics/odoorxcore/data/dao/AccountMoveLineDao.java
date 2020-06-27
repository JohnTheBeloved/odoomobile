package com.ehealthinformatics.odoorxcore.data.dao;

import android.content.Context;

import com.ehealthinformatics.odoorxcore.core.orm.OModel;
import com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OBoolean;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.odoorxcore.core.support.OUser;

import static com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn.RelationType.ManyToOne;

public class AccountMoveLineDao extends OModel {

    OColumn name = new OColumn ("Name", OVarchar.class);
    OColumn user_id = new OColumn (null, ResUsers.class, ManyToOne);
    OColumn is_difference_zero =  new OColumn ("Difference", OBoolean.class);

    public AccountMoveLineDao(Context context, OUser user) {
        super(context, "account.move.line", user);
    }

}
