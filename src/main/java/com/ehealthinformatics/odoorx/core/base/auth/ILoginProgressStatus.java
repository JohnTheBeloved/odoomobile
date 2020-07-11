package com.ehealthinformatics.odoorx.core.base.auth;

import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError;
import com.ehealthinformatics.odoorx.core.base.support.OUser;

import java.util.List;

public interface ILoginProgressStatus {

    void onConnect(List<String> databases);
    void onLoginSuccess(OUser oUser);
    void onConnectionError(OdooError error);

}
