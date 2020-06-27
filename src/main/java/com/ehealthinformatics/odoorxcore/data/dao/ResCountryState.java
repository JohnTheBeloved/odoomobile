package com.ehealthinformatics.odoorxcore.data.dao;

import android.content.Context;


import com.ehealthinformatics.odoorxcore.core.orm.ODataRow;
import com.ehealthinformatics.odoorxcore.core.orm.OModel;
import com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.odoorxcore.core.support.OUser;
import com.ehealthinformatics.odoorxcore.data.db.Columns;
import com.ehealthinformatics.odoorxcore.data.dto.State;

public class ResCountryState extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn code = new OColumn("Code", OVarchar.class);
    OColumn country_id = new OColumn("Country", ResCountry.class, OColumn.RelationType.ManyToOne);

    public ResCountryState(Context context, OUser user) {
        super(context, "res.country.state", user);
    }

    public ResCountry countryDao;

    public void initDaos(){
        DaoRepo daoRepo = DaoRepo.getInstance(getContext());
        countryDao = daoRepo.getDao(ResCountry.class);
    }

    public State fromRow(final ODataRow row) {
        if(row == null) return  null;
        State state = new State(
                row.getInt(Columns.id),
                row.getInt(Columns.server_id),
                row.getString(Columns.Partner.name),
                countryDao.fromRow(row.getM2ORecord(Columns.Partner.country_id).browse()));
        return state;
    }
}
