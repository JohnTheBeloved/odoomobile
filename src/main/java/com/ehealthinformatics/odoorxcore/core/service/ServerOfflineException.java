package com.ehealthinformatics.odoorxcore.core.service;

public class ServerOfflineException extends Exception {

    String message;
    public ServerOfflineException(String message) {
       this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
