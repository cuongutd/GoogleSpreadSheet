package com.tb2g.inventory.model.exception;

/**
 * Created by Cuong on 12/12/2015.
 */
public enum ErrorCode {
    GOOGLE_SHEET_FILES_NOT_SETUP("Google spreadsheets not setup correctly. Please check file names in setting")
    , INVALID_GOOGLE_SHEET_TOKEN("Invalid google auth token. Please check your login and make sure you have Google Drive enabled")
    , INVALID_SEARCHUPC_TOKEN("Invalid searchupc.com access token. Please check settings. token can be acquired from searchupc.com");

    public String errorMsg;

    private ErrorCode(String msg) {
        this.errorMsg = msg;
    }
}
