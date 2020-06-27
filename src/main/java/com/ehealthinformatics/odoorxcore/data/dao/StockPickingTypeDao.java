package com.ehealthinformatics.odoorxcore.data.dao;

import android.content.Context;

import com.ehealthinformatics.odoorxcore.core.orm.ODataRow;
import com.ehealthinformatics.odoorxcore.core.orm.OModel;
import com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.odoorxcore.core.support.OUser;
import com.ehealthinformatics.odoorxcore.data.db.Columns;
import com.ehealthinformatics.odoorxcore.data.db.ModelNames;
import com.ehealthinformatics.odoorxcore.data.dto.StockPickingType;

public class StockPickingTypeDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public StockPickingTypeDao(Context context, OUser user) {
        super(context, ModelNames.STOCK_PICKING_TYPE, user);
    }

    public StockPickingType get(int id, QueryFields queryFields){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, queryFields);

    }

    @Override
    public StockPickingType fromRow(ODataRow row, QueryFields qf){
        Integer id = null, serverId = null;
        String name = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.name)) name = row.getString(Columns.name);
        return new StockPickingType(id, name);
    }

}
