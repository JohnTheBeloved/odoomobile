package com.ehealthinformatics.odoorx.core.base.auth;

import android.content.Context;

import com.ehealthinformatics.odoorx.core.base.rpc.handler.OdooVersionException;
import com.ehealthinformatics.odoorx.core.base.utils.LinkedTreeMapWraper;
import com.ehealthinformatics.odoorx.core.data.dao.AccountBankStatementDao;
import com.ehealthinformatics.odoorx.core.data.dao.DaoRepoBase;
import com.ehealthinformatics.odoorx.core.data.dao.PosSessionDao;
import com.ehealthinformatics.odoorx.core.data.dao.QueryFields;
import com.ehealthinformatics.odoorx.core.data.dao.ResUsers;
import com.ehealthinformatics.odoorx.core.data.db.ModelNames;
import com.ehealthinformatics.odoorx.core.data.dto.AccountBankStatement;
import com.ehealthinformatics.odoorx.core.data.dto.DTO;
import com.ehealthinformatics.odoorx.core.data.dto.PosSession;
import com.ehealthinformatics.odoorx.core.base.orm.ODataRow;
import com.ehealthinformatics.odoorx.core.base.orm.OModel;
import com.ehealthinformatics.odoorx.core.base.orm.OValues;
import com.ehealthinformatics.odoorx.core.base.rpc.Odoo;
import com.ehealthinformatics.odoorx.core.base.rpc.helper.ODomain;
import com.ehealthinformatics.odoorx.core.base.rpc.helper.OdooFields;
import com.ehealthinformatics.odoorx.core.base.rpc.helper.utils.gson.OdooRecord;
import com.ehealthinformatics.odoorx.core.base.rpc.helper.utils.gson.OdooResult;
import com.ehealthinformatics.odoorx.core.base.support.OUser;
import com.ehealthinformatics.odoorx.core.data.db.Columns;
import com.ehealthinformatics.odoorx.core.data.dto.User;

import java.util.List;

public class ServerDefaultsService {

    private OUserAccount oUserAccount;
    private PosSessionDao posSessionDao;
    private ResUsers userDao;
    private AccountBankStatementDao accountBankStatementDao;
    OdooFields readFields = new OdooFields();
    DaoRepoBase daoRepo;

    public ServerDefaultsService(OUserAccount oUserAccount) {
        this.oUserAccount = oUserAccount;
        this.daoRepo = DaoRepoBase.getInstance();
        posSessionDao = daoRepo.getDao(PosSessionDao.class);
        userDao = daoRepo.getDao(ResUsers.class);
        accountBankStatementDao = daoRepo.getDao(AccountBankStatementDao.class);
        readFields.addAll(new String[] {Columns.server_id, Columns.name});
    }

    public User syncUser(Integer userServerId) {
        Integer userId = userDao.selectRowId(userServerId);
        User user;
        if (userId == null || userId <= 0) {
            ODomain userDomain = new ODomain();
            userDomain.add(Columns.server_id , "=", userServerId);
            //TODO: If error
             user = (User) validateResult(userDao, oUserAccount.getOdoo().searchRead(ModelNames.USER, readFields, userDomain, 0, 1, "id desc"));
          } else user = userDao.get(userId, QueryFields.all());

        return user;
    }

    public List<AccountBankStatement> syncSessionStatement (PosSession posSession) {
        List<AccountBankStatement> accountBankStatements = accountBankStatementDao.posSessionStatements(posSession);
        if(accountBankStatements.isEmpty()) {
            ODomain userDomain = new ODomain();
            userDomain.add(Columns.AccountBankStatementCol.pos_session_id , "=", posSession.getServerId());
            OdooResult odooResult = oUserAccount.getOdoo().searchRead(ModelNames.ACCOUNT_BANK_STATEMENT, readFields, userDomain, 0, 1, "id desc");

            ODataRow row = new ODataRow();
            OValues oValues = new OValues();
            if(odooResult.getTotalRecords() > 0){
                // OdooRecord odooRecord = odooResult.getRecords().get(0);
                for (OdooRecord odooRecord:  odooResult.getRecords()) {
                    int serverId = odooRecord.getInt(Columns.server_id);
                    row.put(Columns.server_id, serverId);
                    oValues.put(Columns.server_id, serverId);
                    ODataRow existingRow = accountBankStatementDao.browseServerId(serverId);
                    if (existingRow != null) {
                        accountBankStatements.add(accountBankStatementDao.fromRow(existingRow, QueryFields.all(), posSession));
                    } else {
                        int _id = accountBankStatementDao.insert(oValues);
                        row.put(Columns.id, _id);
                        ODataRow createdRow = accountBankStatementDao.quickCreateRecord(row);
                        Integer rowId = createdRow.getInt(Columns.id);
                        accountBankStatements.add(accountBankStatementDao.get(rowId, QueryFields.all(), posSession));
                    }
                }
            }
        }
       return accountBankStatements;
    }

    public PosSession syncCurrentOpenSession(User user){
        PosSession posSession = posSessionDao.current();
        if (posSession == null) {
            ODomain userDomain = new ODomain();
            userDomain.add("&");
            userDomain.add(Columns.PosSession.user_id, "=", user.getServerId());
            ODomain stateDomain = new ODomain();
            userDomain.add("|");
            stateDomain.add(Columns.PosSession.state , "=", Columns.PosSession.State.opened);
            stateDomain.add(Columns.PosSession.state , "=", Columns.PosSession.State.opening_control);
            userDomain.append(stateDomain);
            posSession = (PosSession) validateResult(posSessionDao, oUserAccount.getOdoo().searchRead(ModelNames.POS_SESSION, null, userDomain, 0, 1, "id desc"));
        }
         return posSession;
    }

    private DTO validateResult(OModel oModel, OdooResult odooResult) {
        ODataRow row = new ODataRow();
        OValues oValues = new OValues();
        if(odooResult.getTotalRecords() > 0){
             OdooRecord odooRecord = new LinkedTreeMapWraper(odooResult.getRecords().get(0));
             int serverId = odooRecord.getInt(Columns.server_id);
             row.put(Columns.server_id, serverId);
             oValues.put(Columns.server_id, serverId);
             ODataRow existingRow = oModel.browseServerId(serverId);
             if (existingRow != null) {
                 return oModel.fromRow(existingRow, QueryFields.all());
             } else {
                 int _id = oModel.insert(oValues);
                 row.put(Columns.id, _id);
                 ODataRow createdRow = oModel.quickCreateRecord(row);
                 Integer rowId = createdRow.getInt(Columns.id);
                 DTO dto = oModel.get(rowId, QueryFields.all());
                 return dto;
             }
        }
        return null;
    }

}
