package com.ehealthinformatics.odoorx.core.base.auth;

import com.ehealthinformatics.odoorx.core.base.support.OUser;

public interface ISyncConfig {
    boolean isValid();
    boolean updateUserConfig(OUser oUser);
}
