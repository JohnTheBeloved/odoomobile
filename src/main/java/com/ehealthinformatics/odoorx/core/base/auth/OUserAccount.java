package com.ehealthinformatics.odoorx.core.base.auth;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ehealthinformatics.odoorx.core.R;
import com.ehealthinformatics.odoorx.core.config.IArtifactsLoader;
import com.ehealthinformatics.odoorx.core.config.OConstants;
import com.ehealthinformatics.odoorx.core.base.rpc.Odoo;
import com.ehealthinformatics.odoorx.core.base.rpc.handler.OdooVersionException;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.IDatabaseListListener;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.IOdooConnectionListener;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.IOdooLoginCallback;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError;
import com.ehealthinformatics.odoorx.core.base.support.OUser;
import com.ehealthinformatics.odoorx.core.base.utils.OResource;
import com.ehealthinformatics.odoorx.core.data.dto.SyncConfig;

import java.util.ArrayList;
import java.util.List;

public class OUserAccount implements IOdooLoginCallback, IOdooConnectionListener {
    private String TAG = OUserAccount.class.getSimpleName();
    Context context;
    String hostUrl;
    String database;
    OUser user;
    Odoo odoo;
    AccountCreator accountCreator;
    OdooError odooError;
    List<String> databases;

    ILoginProgressStatus loginProgressStatus;
    IArtifactsLoader artifactsLoader;

    private OUserAccount(Context context, String hostUrl, ILoginProgressStatus loginProgressStatus, IArtifactsLoader artifactsLoader){
        accountCreator = new AccountCreator();
        this.context = context;
        this.loginProgressStatus = loginProgressStatus;
        this.artifactsLoader = artifactsLoader;
        this.hostUrl = hostUrl;
        databases = new ArrayList<>();
    }

    public static OUserAccount getInstance(Context context, OUser oUser, ILoginProgressStatus loginProgressStatus, IArtifactsLoader artifactsLoader) throws OdooVersionException {
        String hostUrl = oUser.getHost();
        OUserAccount oUserAccount = new OUserAccount(context,  hostUrl, loginProgressStatus, artifactsLoader);
        oUserAccount.setOdoo(Odoo.createInstance(context, hostUrl).setOnConnect(oUserAccount));
        return oUserAccount;
    }

    public static OUserAccount getInstance(Context context, String hostUrl, ILoginProgressStatus iLoginProgressStatus, IArtifactsLoader artifactsLoader)  throws OdooVersionException{
        OUserAccount oUserAccount = new OUserAccount(context,  hostUrl, iLoginProgressStatus, artifactsLoader);
        Odoo odoo = Odoo.createInstance(context, hostUrl).setOnConnect(oUserAccount);
        oUserAccount.setOdoo(odoo);
        return oUserAccount;
    }

    public void authenticate(String username, String password, String database){
        odoo.authenticate(username, password, database, this);
    }

    @Override
    public void onLoginSuccess(Odoo odoo, OUser user) {
        setOdoo(odoo);
        this.user = user;
        if (accountCreator != null) {
            accountCreator.cancel(true);
        }
        accountCreator = new OUserAccount.AccountCreator();
        accountCreator.execute(user);
        loginProgressStatus.onSuccess(user);
    }

    @Override
    public void onLoginFail(OdooError error) {
        loginProgressStatus.onError(error);
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
    public void onError(OdooError error) {
        this.odooError = error;
        loginProgressStatus.onError(error);
    }

    private class AccountCreator extends AsyncTask<OUser, String, SyncConfig> {

        private OUser mUser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginProgressStatus.onProgressMessage("Syncing User Configuration");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            loginProgressStatus.onProgressMessage(values[0]);
        }

        @Override
        protected SyncConfig doInBackground(OUser... params) {
            mUser = params[0];
            try {
                if (OdooAccountManager.createAccount(context, mUser)) {
                    mUser = OdooAccountManager.getDetails(context, mUser.getAndroidName());
                    OdooAccountManager.login(context, mUser.getAndroidName());
                    return artifactsLoader.load();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                removeUser(mUser);
            }
            return null;
        }

        @Override
        protected void onPostExecute(SyncConfig syncConfig) {
            super.onPostExecute(syncConfig);
            if (syncConfig == null || syncConfig.getUser() == null){
                loginProgressStatus.onProgressMessage("Unable to complete syncing configuration with server");
                loginProgressStatus.onProgressStatus(LoginProgressStatus.CONFIG_DATA_SYNC_FAILED);
            } else if(syncConfig.getPosSession() == null){
                loginProgressStatus.onProgressMessage("Unable to sync open session to Device");
                loginProgressStatus.onProgressStatus(LoginProgressStatus.SESSION_SYNC_FAILED);
            } else {
                loginProgressStatus.onProgressMessage(OResource.string(context, R.string.status_redirecting));
                OConstants.CURRENCY_SYMBOL = syncConfig.getPosSession().getConfig().getPriceList().getCurrency().getSymbol();
                mUser.setPosSessionId(syncConfig.getPosSession().getServerId());
                mUser.setCurrencySymbol(OConstants.CURRENCY_SYMBOL);
                user = OdooAccountManager.updateUserData(context, mUser);
            }
            onLoginSuccess(odoo, user);
        }
    }

    protected void setOdoo(Odoo odoo) {
        this.odoo = odoo;
    }

    public Odoo getOdoo() {
        return odoo;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public OdooError getOdooError() {
        return odooError;
    }

    public OUser getOdooUser() {
        return user;
    }

    private void removeUser(OUser oUser){
        OdooAccountManager.logout(context, oUser.getAndroidName());
        OdooAccountManager.removeAccount(context, oUser.getAndroidName());
        OdooAccountManager.dropDatabase(context, oUser);
    }

}
