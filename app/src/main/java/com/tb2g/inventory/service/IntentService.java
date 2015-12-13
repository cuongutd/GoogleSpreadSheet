package com.tb2g.inventory.service;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.os.ResultReceiver;

import com.google.gdata.util.AuthenticationException;
import com.tb2g.inventory.R;
import com.tb2g.inventory.business.SpreadSheetManager;
import com.tb2g.inventory.model.IntentResultBus;
import com.tb2g.inventory.model.InventoryItem;
import com.tb2g.inventory.model.InventoryTransaction;
import com.tb2g.inventory.model.exception.ErrorCode;
import com.tb2g.inventory.service.restclient.RestClient;
import com.tb2g.inventory.model.UPCSearchProduct;
import com.tb2g.inventory.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntentService extends android.app.IntentService {

    private String mSpreadSheetInventoryTitle;
    private String mSpreadSheetTransactionTitle;
    private String mSpreadSheetToken;
    private String mUPCSearchAccessToken;

    private void loadKeys() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSpreadSheetInventoryTitle = sharedPref.getString(getString(R.string.shared_pref_inv_filename), getString(R.string.shared_pref_inv_filename_default_value));
        mSpreadSheetTransactionTitle = sharedPref.getString(getString(R.string.shared_pref_tran_filename), getString(R.string.shared_pref_tran_filename_default_value));
        mSpreadSheetToken = sharedPref.getString(Constants.SHARED_PREF_SHEET_TOKEN, "");
        mUPCSearchAccessToken = sharedPref.getString(getString(R.string.shared_pref_upcdb_key), getString(R.string.shared_pref_upcdb_default_value));
    }


    public IntentService() {
        super("IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            loadKeys();
            final String action = intent.getAction();
            final ResultReceiver receiver = intent.getParcelableExtra(Constants.EXTRA_RECEIVER);

            if (Constants.ACTION_INV_LOCATION.equals(action)) {
                handleInventoryLocations(receiver);
            }else if (Constants.ACTION_INV_ITEM.equals(action)) {
                String location = intent.getStringExtra(Constants.EXTRA_LOCATION_NAME);
                handleInventoryItems(receiver, location);
            }else if (Constants.ACTION_UPC_LOOKUP.equals(action)) {

                String upc = intent.getStringExtra(Constants.EXTRA_UPC);
                handleUPCLookup(receiver, upc);
            }else if (Constants.ACTION_INV_PRINT_ITEMS.equals(action)) {

                String location = intent.getStringExtra(Constants.EXTRA_LOCATION_NAME);
                handlPrintInv(location);
            }else if (Constants.ACTION_INV_UPDATE.equals(action)) {
                InventoryTransaction pendingTran = intent.getParcelableExtra(Constants.EXTRA_PENDING_INVENTORY_TRAN);
                String location = intent.getStringExtra(Constants.EXTRA_LOCATION_NAME);
                handleUpdateInventory(receiver, location, pendingTran);
            }
        }
    }

    public static void getWebProductInfo(ResultReceiver receiver, Context context, String upc){

        IntentServiceTask.with(context)
                .setResultReceiver(receiver)
                .goingToDo(Constants.ACTION_UPC_LOOKUP)
                .putExtra(Constants.EXTRA_UPC, upc)
                .start();

    }

    private void handleUPCLookup(ResultReceiver receiver, String upc){

        //UPCProduct product = RestClient.getUPCService().lookupProduct(mUPCDatabaseKey, upc);
        HashMap<Integer, UPCSearchProduct> products = new HashMap<Integer, UPCSearchProduct>();
        IntentResultBus result = new IntentResultBus();

        try {
            products = RestClient.getUPCSearchService().lookupProduct(mUPCSearchAccessToken, upc);
            for (UPCSearchProduct product : products.values())
                product.setUpc(upc);
            result.setUpcProducts(products);
        }catch(Exception e){
            result.setError(ErrorCode.INVALID_SEARCHUPC_TOKEN);
        }


        Bundle b = new Bundle();
        b.putParcelable(Constants.EXTRA_INTENT_SERVICE_RESULT, result);
        receiver.send(Constants.RESULT_CODE_WEB_PRODUCT, b);

    }

    public static void getInventoryLocations(Context context, ResultReceiver receiver) {

        IntentServiceTask.with(context)
                .setResultReceiver(receiver)
                .goingToDo(Constants.ACTION_INV_LOCATION)
                .start();
    }

    private void handleInventoryLocations(ResultReceiver receiver){

        IntentResultBus result = new IntentResultBus();
        List<String> locations = new ArrayList<>();
        try {
            locations = SpreadSheetManager.getInstance(mSpreadSheetToken).listWorkSheetTitles(mSpreadSheetInventoryTitle);
        }catch(NullPointerException e){
            //more likely 2 files are not setup in google drive
            result.setError(ErrorCode.GOOGLE_SHEET_FILES_NOT_SETUP);
        }catch(AuthenticationException e){
            result.setError(ErrorCode.INVALID_GOOGLE_SHEET_TOKEN);
        }catch(Exception e){
            e.printStackTrace();
        }
        result.setLocations(locations);

        Bundle b = new Bundle();
        b.putParcelable(Constants.EXTRA_INTENT_SERVICE_RESULT, result);
        receiver.send(Constants.RESULT_CODE_INV_LOCATION, b);

    }

    public static void getInventoryItems(Context context, ResultReceiver receiver, String location){
        IntentServiceTask.with(context)
                .setResultReceiver(receiver)
                .goingToDo(Constants.ACTION_INV_ITEM)
                .putExtra(Constants.EXTRA_LOCATION_NAME, location)
                .start();
    }

    private void handleInventoryItems(ResultReceiver receiver, String location){

        IntentResultBus result = new IntentResultBus();
        List<InventoryItem> items = new ArrayList<>();

        try {
            items = SpreadSheetManager.getInstance(mSpreadSheetToken).getRowsFromInventoryWorkSheet(mSpreadSheetInventoryTitle, location);
        }catch (Exception e){
            e.printStackTrace();

        }
        result.setItems(items);

        Bundle b = new Bundle();
        b.putParcelable(Constants.EXTRA_INTENT_SERVICE_RESULT, result);
        receiver.send(Constants.RESULT_CODE_INV_ITEM, b);

    }

    public static void updateInventory(Context context, ResultReceiver receiver, String location, InventoryTransaction pendingTran){

        IntentServiceTask.with(context)
                .setResultReceiver(receiver)
                .goingToDo(Constants.ACTION_INV_UPDATE)
                .putExtra(Constants.EXTRA_LOCATION_NAME, location)
                .putExtra(Constants.EXTRA_PENDING_INVENTORY_TRAN, pendingTran)
                .start();

    }

    private void handleUpdateInventory(ResultReceiver receiver, String location, InventoryTransaction pendingTran){
        try {
            SpreadSheetManager.getInstance(mSpreadSheetToken).updateInventory(mSpreadSheetInventoryTitle, location, pendingTran);
            SpreadSheetManager.getInstance(mSpreadSheetToken).createTransaction(mSpreadSheetTransactionTitle, location, pendingTran);
        }catch (Exception e){
            e.printStackTrace();
        }

    Bundle b = new Bundle();
        receiver.send(Constants.RESULT_CODE_INV_UPDATE, b);
    }

    public static void printInventory(Context context, ResultReceiver receiver, String location){
        IntentServiceTask.with(context)
                .setResultReceiver(receiver)
                .goingToDo(Constants.ACTION_INV_PRINT_ITEMS)
                .putExtra(Constants.EXTRA_LOCATION_NAME, location)
                .start();
    }

    private void handlPrintInv(String location){
        try {
            SpreadSheetManager.getInstance(mSpreadSheetToken).printWorkSheet(mSpreadSheetInventoryTitle, location);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
