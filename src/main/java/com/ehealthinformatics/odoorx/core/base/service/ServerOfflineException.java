package com.ehealthinformatics.odoorx.core.base.service;

public class ServerOfflineException extends Exception {

    String message;
    public ServerOfflineException(String message) {
       this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
