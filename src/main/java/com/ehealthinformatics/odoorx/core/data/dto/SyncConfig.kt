package com.ehealthinformatics.odoorx.core.data.dto

data class SyncConfig(var user: User?, var posSession: PosSession?)
{

   public override fun toString(): String {
        return "{ { user: " + user.toString() + "}, { posSession: " + posSession.toString() + "}";
    }
}
