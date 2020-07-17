package com.ehealthinformatics.odoorx.core.base.auth;

import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError;

public interface IConfigLoadListener {
    ISyncConfig onStartConfigLoad();
    void onConfigLoadError(OdooError e);
    void onConfigLoadSuccess(ISyncConfig syncConfig);
}
