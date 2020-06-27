package com.ehealthinformatics.odoorxcore.data.dao;

import android.content.Context;


import com.ehealthinformatics.odoorxcore.core.orm.ODataRow;
import com.ehealthinformatics.odoorxcore.core.orm.OModel;
import com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.odoorxcore.core.support.OUser;
import com.ehealthinformatics.odoorxcore.data.db.Columns;
import com.ehealthinformatics.odoorxcore.data.dto.Account;
import com.ehealthinformatics.odoorxcore.data.dto.AccountJournal;

import static com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn.RelationType.ManyToOne;

public class AccountJournalDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn default_debit_account_id = new OColumn (null, AccountDao.class, ManyToOne);
    OColumn default_credit_account_id = new OColumn (null, AccountDao.class, ManyToOne);

    public AccountJournalDao(Context context, OUser user) {
        super(context, "account.journal", user);
    }

     private  AccountDao accountDao;


    @Override
     public void initDaos() {
        DaoRepo daoRepo = DaoRepo.getInstance(getContext());
        accountDao = daoRepo.getDao(AccountDao.class);
    }


    public AccountJournal get(int id, QueryFields queryFields){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, queryFields);

    }

    @Override
    public AccountJournal fromRow(ODataRow row, QueryFields qt){
        if(row  == null) return null;
        Integer id = null, serverId = null;
        String name = null, reference = null;
        Account debitAccount = null, creditAccount = null;
        if(qt.contains(Columns.id)) id = row.getInt(OColumn.ROW_ID);
        if(qt.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qt.contains(Columns.name)) name = row.getString(this.name.getName());
        if(qt.contains(Columns.AccountJournal.default_debit_account_id)) debitAccount = accountDao.fromRow(row.getM2ORecord(Columns.AccountJournal.default_debit_account_id).browse(), qt.childField(Columns.AccountJournal.default_debit_account_id));
        if(qt.contains(Columns.AccountJournal.default_credit_account_id)) creditAccount = accountDao.fromRow(row.getM2ORecord(Columns.AccountJournal.default_credit_account_id).browse(), qt.childField(Columns.AccountJournal.default_debit_account_id));
        if(qt.contains(Columns.AccountJournal.reference))  reference = row.getString(this.name.getName());
        return new AccountJournal(id, serverId, name, debitAccount, creditAccount);
    }

}
