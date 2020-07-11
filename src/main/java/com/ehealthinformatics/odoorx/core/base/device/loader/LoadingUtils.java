package com.ehealthinformatics.odoorx.core.base.device.loader;

import com.ehealthinformatics.odoorx.core.base.auth.OUserAccount;
import com.ehealthinformatics.odoorx.core.base.auth.ServerDefaultsService;
import com.ehealthinformatics.odoorx.core.base.support.OUser;
import com.ehealthinformatics.odoorx.core.data.dao.DaoRepoBase;
import com.ehealthinformatics.odoorx.core.data.dto.AccountBankStatement;
import com.ehealthinformatics.odoorx.core.data.dto.PosSession;
import com.ehealthinformatics.odoorx.core.data.dto.SyncConfig;
import com.ehealthinformatics.odoorx.core.data.dto.User;

import java.util.List;

public abstract class LoadingUtils {

    public static class ArtifactsLoader {

        private OUserAccount oUserAccount;
        private OUser oUser;

        public ArtifactsLoader(final OUserAccount oUserAccount) {
            this.oUserAccount = oUserAccount;
            this.oUser = oUserAccount.getOUser();
        }

        public SyncConfig load() {
            User user;
            PosSession posSession;
            List<AccountBankStatement> accountBankStatements;

            ServerDefaultsService serverDefaultsService = new ServerDefaultsService(oUserAccount);
            user = serverDefaultsService.syncUser(oUser.getUserId());
            if (user != null) {
                posSession = serverDefaultsService.syncCurrentOpenSession(user);
                if (posSession != null) {
                    oUser.setPosSessionId(posSession.getId());
                    accountBankStatements = serverDefaultsService.syncSessionStatement(posSession);
                    if (accountBankStatements != null) {
                        return new SyncConfig(user, posSession);
                    }
                }
            }
            return null;
        }
    }

}
