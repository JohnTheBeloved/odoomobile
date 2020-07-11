package com.ehealthinformatics.odoorx.core.base.auth;

import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError;
import com.ehealthinformatics.odoorx.core.data.dto.SyncConfig;

public interface IConfigLoadListener {
    SyncConfig onStartConfigLoad();
    void onConfigLoadError(OdooError e);
    void onConfigLoadSuccess(SyncConfig syncConfig);

}
