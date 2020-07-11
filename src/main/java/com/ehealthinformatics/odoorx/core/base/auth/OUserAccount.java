package com.ehealthinformatics.odoorx.core.base.auth;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ehealthinformatics.odoorx.core.base.rpc.helper.utils.OdooLog;
import com.ehealthinformatics.odoorx.core.config.OConstants;
import com.ehealthinformatics.odoorx.core.base.rpc.Odoo;
import com.ehealthinformatics.odoorx.core.base.rpc.handler.OdooVersionException;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.IDatabaseListListener;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.IOdooConnectionListener;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.IOdooLoginCallback;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError;
import com.ehealthinformatics.odoorx.core.base.support.OUser;
import com.ehealthinformatics.odoorx.core.data.dto.SyncConfig;

import java.util.ArrayList;
import java.util.List;

public class OUserAccount implements IOdooLoginCallback, IOdooConnectionListener {
    private String TAG = OUserAccount.class.getSimpleName();
    Context context;
    String hostUrl;
    String username;
    String password;
    String database;
    OUser user;
    Odoo odoo;
    AccountCreator accountCreator;
    OdooError odooError;
    List<String> databases;

    ILoginProgressStatus loginProgressStatus;
    IConfigLoadListener configLoadListener;

    private OUserAccount(Context context, String hostUrl, String username, String password, String database,  ILoginProgressStatus loginProgressStatus, IConfigLoadListener configLoadListener){
        accountCreator = new AccountCreator();
        this.context = context;
        this.loginProgressStatus = loginProgressStatus;
        this.configLoadListener = configLoadListener;
        this.hostUrl = hostUrl;
        this.database = database;
        this.username = username;
        this.password = password;
        databases = new ArrayList<>();
    }

    public static OUserAccount getInstance(Context context, OUser oUser, ILoginProgressStatus loginProgressStatus,  IConfigLoadListener configLoadListener) throws OdooVersionException {
        String hostUrl = oUser.getHost();
        OUserAccount oUserAccount = new OUserAccount(context,  hostUrl, oUser.getUsername(), oUser.getPassword(),oUser.getDatabase(), loginProgressStatus, configLoadListener);
        oUserAccount.setOdoo(Odoo.createInstance(context, hostUrl).setOnConnect(oUserAccount));
        return oUserAccount;
    }

    public static OUserAccount getInstance(Context context,String username, String password, String database, String hostUrl, ILoginProgressStatus iLoginProgressStatus, IConfigLoadListener configLoadListener)  throws OdooVersionException{
        OUserAccount oUserAccount = new OUserAccount(context,  hostUrl, username, password, database, iLoginProgressStatus, configLoadListener);
        oUserAccount.setOdoo(Odoo.createInstance(context, hostUrl).setOnConnect(oUserAccount));
        return oUserAccount;
    }

    public static OUserAccount getInstance(Context context, String hostUrl, ILoginProgressStatus iLoginProgressStatus, IConfigLoadListener configLoadListener)  throws OdooVersionException{
        OUserAccount oUserAccount = new OUserAccount(context,  hostUrl, "", "", "", iLoginProgressStatus, configLoadListener);
        oUserAccount.setOdoo(Odoo.createInstance(context, hostUrl).setOnConnect(oUserAccount));
        return oUserAccount;
    }


    @Override
    public void onConnect(Odoo odoo) {
        this.odoo = odoo;
        odoo.getDatabaseList(new IDatabaseListListener() {
            @Override
            public void onDatabasesLoad(List<String> strings) {
                databases = strings;
                loginProgressStatus.onConnect(databases);
            }
        });
    }

    @Override
    public void onConnectError(OdooError error) {
        OdooLog.e(error);
        this.odooError = error;
        loginProgressStatus.onConnectionError(error);
    }

    public void authenticate(){
        odoo.authenticate(username, password, database, this);
    }

    public void authenticate(String username, String password, String database){
        odoo.authenticate(username, password, database, this);
    }

    @Override
    public void onAuthenticateSuccess(Odoo odoo, OUser user) {
        setOdoo(odoo);
        this.user = user;
        if (accountCreator != null) {
            accountCreator.cancel(true);
        }
        accountCreator = new OUserAccount.AccountCreator();
        accountCreator.execute(user);
        loginProgressStatus.onLoginSuccess(user);
    }

    @Override
    public void onAuthenticateError(OdooError error) {
        OdooLog.e(error);
        loginProgressStatus.onConnectionError(error);
    }

    private class AccountCreator extends AsyncTask<OUser, String, SyncConfig> {

        private OUser loggedInUser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected SyncConfig doInBackground(OUser... params) {
            OUser authenticatedUser = params[0];
            try {
                 loggedInUser = OdooAccountManager.getDetails(context, authenticatedUser.getAndroidName());
                 if(loggedInUser == null || !loggedInUser.isActive()) {
                     boolean createdNewUserAccount = OdooAccountManager.createAccount(context, authenticatedUser);
                     if(!createdNewUserAccount) {
                         throw new Exception("Unable to create user account " + authenticatedUser.getUsername());
                     }
                 }
                OdooAccountManager.login(context, authenticatedUser.getAndroidName());
                loggedInUser = OdooAccountManager.getDetails(context, authenticatedUser.getAndroidName());
                return configLoadListener.onStartConfigLoad();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                removeUser(loggedInUser);
                configLoadListener.onConfigLoadError(new OdooError(e.getMessage(), e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(SyncConfig syncConfig) {
            super.onPostExecute(syncConfig);
            try {
                if (syncConfig == null || syncConfig.getUser() == null || syncConfig.getPosSession() == null) {
                    throw new Exception("Unable to complete syncing configuration with server, SyncConfig is " + (syncConfig != null ? syncConfig.toString() : "null"));
                }
                OConstants.CURRENCY_SYMBOL = syncConfig.getPosSession().getConfig().getPriceList().getCurrency().getSymbol();
                loggedInUser.setPosSessionId(syncConfig.getPosSession().getServerId());
                loggedInUser.setCurrencySymbol(OConstants.CURRENCY_SYMBOL);
                user = OdooAccountManager.updateUserData(context, loggedInUser);
                configLoadListener.onConfigLoadSuccess(syncConfig);
            } catch (Exception e) {
                OdooLog.e(e);
                configLoadListener.onConfigLoadError(new OdooError(e.getMessage(), e));
            }
        }
    }

    private void setOdoo(Odoo odoo) {
        this.odoo = odoo;
    }

    public Odoo getOdoo() {
        return odoo;
    }


    public OUser getOUser() {
        return  user;
    }

    public List<String> getDatabases() {
        return databases;
    }

    private void removeUser(OUser oUser){
        OdooAccountManager.logout(context, oUser.getAndroidName());
        OdooAccountManager.removeAccount(context, oUser.getAndroidName());
        OdooAccountManager.dropDatabase(context, oUser);
    }

}
