package com.tb2g.inventory.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cuong on 12/11/2015.
 */
public class Constants {
    public static final String ACTION_INV_LOCATION = "com.tb2g.spreadsheet.ACTION_INV_LOCATION";
    public static final String ACTION_INV_ITEM = "com.tb2g.spreadsheet.ACTION_INV_ITEM";
    public static final String ACTION_UPC_LOOKUP = "com.tb2g.spreadsheet.UPC_LOOKUP";
    public static final String ACTION_INV_UPDATE = "com.tb2g.spreadsheet.ACTION_INV_UPDATE";
    public static final String ACTION_INV_PRINT_ITEMS = "com.tb2g.spreadsheet.ACTION_INV_PRINT_ITEMS";

    public static final String EXTRA_UPC = "com.tb2g.spreadsheet.UPC";
    public static final String EXTRA_TOKEN = "com.tb2g.spreadsheet.EXTRA_TOKEN";
    public static final String EXTRA_RECEIVER = "com.tb2g.spreadsheet.EXTRA_RECEIVER";
    public static final String EXTRA_LOCATION = "com.tb2g.spreadsheet.EXTRA_LOCATION";
    public static final String EXTRA_LOCATION_NAME = "com.tb2g.spreadsheet.EXTRA_LOCATION_NAME";
    public static final String EXTRA_INTENT_SERVICE_RESULT = "com.tb2g.spreadsheet.EXTRA_INTENT_SERVICE_RESULT";
    public static final String EXTRA_SCAN_RESULT_LIST = "com.tb2g.spreadsheet.EXTRA_SCAN_RESULT_LIST";
    public static final String EXTRA_QUANTITY_ON_HAND = "com.tb2g.spreadsheet.EXTRA_QUANTITY_ON_HAND";
    public static final String EXTRA_PENDING_INVENTORY_ITEM = "com.tb2g.spreadsheet.EXTRA_PENDING_INVENTORY_ITEM";
    public static final String EXTRA_PENDING_INVENTORY_TRAN = "com.tb2g.spreadsheet.EXTRA_PENDING_INVENTORY_TRAN";
    public static final String EXTRA_INVENTORY_ITEMS = "com.tb2g.spreadsheet.EXTRA_INVENTORY_ITEMS";
    public static final String EXTRA_LOGIN_RETURN_FLAG = "com.tb2g.spreadsheet.EXTRA_LOGIN_RETURN_FLAG";
    public static final String EXTRA_GOOGLE_ACCOUNT = "com.tb2g.spreadsheet.EXTRA_GOOGLE_ACCOUNT";

    public static final int RESULT_CODE_NO_CONNECTION = -1;
    public static final int RESULT_CODE_INV_LOCATION = 0;
    public static final int RESULT_CODE_INV_ITEM = 1;
    public static final int RESULT_CODE_WEB_PRODUCT = 2;
    public static final int RESULT_CODE_INV_UPDATE = 3;

    public static final int REQUEST_CODE_GET_TOKEN = 1000;
    public static final int REQUEST_CODE_AUTH_SHEET = 1001;
    public static final int REQUEST_CODE_BARCODE_SCANNER = 1002;
    public static final int REQUEST_CODE_DIALOG = 1003;

    public static final String SHARED_PREF_INVENTORY_FILE_NAME = "SHARED_PREF_INVENTORY_FILE_NAME";
    public static final String SHARED_PREF_TRANSACTION_FILE_NAME = "SHARED_PREF_TRANSACTION_FILE_NAME";
    public static final String SHARED_PREF_SHEET_TOKEN = "SHARED_PREF_SHEET_TOKEN";
    public static final String SHARED_PREF_UPCDB_KEY = "SHARED_PREF_UPCDB_KEY";

    public static final ArrayList<String> INVENTORY_COLUMNS = new ArrayList<String>();
    public static final ArrayList<String> TRANSACTION_COLUMNS = new ArrayList<String>();

    static{
        INVENTORY_COLUMNS.add("SKU");
        INVENTORY_COLUMNS.add("NAME");
        INVENTORY_COLUMNS.add("IMGURL");
        INVENTORY_COLUMNS.add("PRODUCTURL");
        INVENTORY_COLUMNS.add("QUANTITY");
        INVENTORY_COLUMNS.add("LASTUPDATE");

        TRANSACTION_COLUMNS.add("SKU");
        TRANSACTION_COLUMNS.add("NAME");
        TRANSACTION_COLUMNS.add("IMGURL");
        TRANSACTION_COLUMNS.add("QUANTITY");
        TRANSACTION_COLUMNS.add("INOROUT");
        TRANSACTION_COLUMNS.add("LASTUPDATE");
    }

}
