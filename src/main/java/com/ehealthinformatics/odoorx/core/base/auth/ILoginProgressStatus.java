package com.ehealthinformatics.odoorx.core.base.auth;

import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError;
import com.ehealthinformatics.odoorx.core.base.support.OUser;

import java.util.List;

public interface ILoginProgressStatus {

    public void onProgressMessage(String message);
    public void onProgressPercentage(Integer percentage);
    public void onProgressStatus(LoginProgressStatus loginProgressStatus);

    public void onConnect(List<String> databases);
    public void onSuccess(OUser oUser);
    public void onError(OdooError error);

}
