package com.tb2g.inventory.business;

import android.util.Log;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.tb2g.inventory.model.InventoryItem;
import com.tb2g.inventory.model.InventoryTransaction;
import com.tb2g.inventory.util.Constants;
import com.tb2g.inventory.util.DateUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cuong on 12/10/2015.
 */
public class SpreadSheetManager {

    private final String LOG_TAG = SpreadSheetManager.class.getSimpleName();


    private SpreadsheetService mService;
    private String mToken;

    private static SpreadSheetManager instance = new SpreadSheetManager();

    public static SpreadSheetManager getInstance(String token) {
        instance.mToken = token;
        return instance;
    }

    private SpreadSheetManager() {
    }


//                String spreadsheet = params[0];
//                String worksheet = params[1];
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("Name", "Cuong Nguyen");
//                map.put("Age", "38");
//                map.put("Created", new java.util.Date().toString());
//
//                addNewLine(getService(), spreadsheet, worksheet, map);

    public void printFileList() throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        List<SpreadsheetEntry> spreadsheets = listFiles();

        // Iterate through all of the spreadsheets returned
        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            // Print the title of this spreadsheet to the screen
            System.out.println(spreadsheet.getTitle().getPlainText());
        }
    }

    private SpreadsheetService getService() {
        if (mService == null) {
            mService =
                    new SpreadsheetService("MySpreadsheetIntegration");
            mService.setProtocolVersion(SpreadsheetService.Versions.V3);
            Log.d("IntentService token: ", mToken);
            mService.setHeader("Authorization", "Bearer " + mToken);
        }
        return mService;
    }


    public List<SpreadsheetEntry> listFiles() throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        // Define the URL to request.  This should never change.
        URL SPREADSHEET_FEED_URL = new URL(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = getService().getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
        return feed.getEntries();

    }


    private SpreadsheetEntry findSpreadSheet(String spreadsheetTitle) throws AuthenticationException, MalformedURLException, IOException, ServiceException {
        List<SpreadsheetEntry> spreadsheets = listFiles();

        SpreadsheetEntry sheetToWorkOn = null;

        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            if (spreadsheetTitle.equals(spreadsheet.getTitle().getPlainText())) {
                sheetToWorkOn = spreadsheet;
                break;
            }
        }

        return sheetToWorkOn;
    }

    public void addNewWorkSheet(String spreadsheetTitle, String worksheetTitle, int col, int row) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        SpreadsheetEntry sheetToWorkOn = findSpreadSheet(spreadsheetTitle);

        if (sheetToWorkOn == null) {
            //TODO create new one
        }

        // Create a local representation of the new worksheet.
        WorksheetEntry worksheet = new WorksheetEntry();
        worksheet.setTitle(new PlainTextConstruct(worksheetTitle));
        worksheet.setColCount(col);
        worksheet.setRowCount(row);

        // Send the local representation of the worksheet to the API for
        // creation.  The URL to use here is the worksheet feed URL of our
        // spreadsheet.
        URL worksheetFeedUrl = sheetToWorkOn.getWorksheetFeedUrl();
        getService().insert(worksheetFeedUrl, worksheet);
    }

    public List<String> listWorkSheetTitles(String spreadsheetTitle) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        List<String> list = new ArrayList<String>();

        List<WorksheetEntry> worksheets = listWorkSheets(spreadsheetTitle);
        for (WorksheetEntry entry : worksheets)
            list.add(entry.getTitle().getPlainText());

        return list;
    }


    private List<WorksheetEntry> listWorkSheets(String spreadsheetTitle) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        List<WorksheetEntry> worksheets = null;
        SpreadsheetEntry spreadsheet = findSpreadSheet(spreadsheetTitle);
        WorksheetFeed worksheetFeed = getService().getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        worksheets = worksheetFeed.getEntries();

        return worksheets;
    }

    private WorksheetEntry findWorkSheet(String spreadsheetTitle, String worksheetTitle) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        WorksheetEntry returnWorkSheet = null;
        List<WorksheetEntry> worksheets = listWorkSheets(spreadsheetTitle);

        for (WorksheetEntry entry : worksheets)
            if (worksheetTitle.equals(entry.getTitle().getPlainText())) {

                returnWorkSheet = entry;
                break;
            }
        return returnWorkSheet;

    }

    public List<InventoryItem> getRowsFromInventoryWorkSheet(String invSpreadSheetTitle, String invWorksheetTitle) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        List<InventoryItem> items = new ArrayList<InventoryItem>();

        List<ListEntry> rows = getWorkSheetRows(invSpreadSheetTitle, invWorksheetTitle);

        for (ListEntry row : rows) {

            InventoryItem item = new InventoryItem();
            for (String tag : row.getCustomElements().getTags()) {
                switch (tag.toUpperCase()) {
                    case "SKU":
                        item.setSku(row.getCustomElements().getValue(tag).replace("'", ""));
                        break;
                    case "NAME":
                        item.setName(row.getCustomElements().getValue(tag));
                        break;
                    case "IMGURL":
                        item.setImgUrl(row.getCustomElements().getValue(tag));
                        break;
                    case "PRODUCTURL":
                        item.setProductUrl(row.getCustomElements().getValue(tag));
                        break;
                    case "QUANTITY":
                        item.setQuantity(Integer.valueOf(row.getCustomElements().getValue(tag)));
                        break;
                    case "LASTUPDATE":
                        item.setLastUpdate(DateUtil.formatStringToTimestamp(row.getCustomElements().getValue(tag)));
                        break;
                }

            }
            items.add(item);
        }

        return items;
    }

    public List<ListEntry> getWorkSheetRows(String spreadsheetTitle, String worksheetTitle) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        List<ListEntry> rows = new ArrayList<ListEntry>();

        WorksheetEntry worksheet = findWorkSheet(spreadsheetTitle, worksheetTitle);

        // Fetch the list feed of the worksheet.
        URL listFeedUrl = worksheet.getListFeedUrl();

        ListFeed listFeed = getService().getFeed(listFeedUrl, ListFeed.class);
        rows = listFeed.getEntries();

        return rows;
    }


    public void printWorkSheet(String spreadsheetTitle, String worksheetTitle) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        // Iterate through each row, printing its cell values.
        for (ListEntry row : getWorkSheetRows(spreadsheetTitle, worksheetTitle)) {
            // Print the first column's cell value
            System.out.print(row.getTitle().getPlainText() + "\t");
            // Iterate over the remaining columns, and print each cell value
            for (String tag : row.getCustomElements().getTags()) {
                System.out.print(tag + "\t");
                System.out.print(row.getCustomElements().getValue(tag) + "\t");
            }
            System.out.println();
        }
    }

    private ListEntry findEntry(String spreadsheetTitle, String worksheetTitle, String firstColKeyValue) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        // Iterate through each row, printing its cell values.
        for (ListEntry row : getWorkSheetRows(spreadsheetTitle, worksheetTitle)) {
            Log.d("Google sheet: ", row.getTitle().getPlainText());
            if ((firstColKeyValue).equals(row.getTitle().getPlainText()))
                return row;
        }

        return null;
    }


    public void updateInventory(String spreadsheetTitle, String worksheetTitle, InventoryTransaction transaction) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        ListEntry entry = findEntry(spreadsheetTitle, worksheetTitle, transaction.getItem().getSku());

        if (entry == null) {

            HashMap<String, String> row = new HashMap<String, String>();
            for (String col : Constants.INVENTORY_COLUMNS) {
                String value = "";
                switch (col) {
                    case "SKU":
                        value = "'" + transaction.getItem().getSku();
                        break;
                    case "NAME":
                        value = transaction.getItem().getName();
                        break;
                    case "IMGURL":
                        value = transaction.getItem().getImgUrl();
                        break;
                    case "PRODUCTURL":
                        value = transaction.getItem().getProductUrl();
                        break;
                    case "QUANTITY":
                        value = String.valueOf(transaction.getTransactionQuantity());
                        break;
                    case "LASTUPDATE":
                        value = DateUtil.getCurrentTimestampAsString();
                        break;
                }
                row.put(col, value);
            }

            addNewLine(spreadsheetTitle, worksheetTitle, row);
        } else {

            int newQty = transaction.getItem().getQuantity() + transaction.getTransactionQuantity() * ("IN".equals(transaction.getTransactionType()) ? 1 : -1);

            entry.getCustomElements().setValueLocal("QUANTITY", String.valueOf(newQty));
            entry.update();

        }


    }


    public void addNewLine(String spreadsheetTitle, String worksheetTitle, HashMap<String, String> value) throws AuthenticationException, MalformedURLException, IOException, ServiceException {
        WorksheetEntry worksheet = findWorkSheet(spreadsheetTitle, worksheetTitle);

        URL listFeedUrl = worksheet.getListFeedUrl();
        // Create a local representation of the new row.
        ListEntry row = new ListEntry();
        for (Map.Entry<String, String> entry : value.entrySet()) {
            row.getCustomElements().setValueLocal(entry.getKey(), entry.getValue());
        }

        // Send the new row to the API for insertion.
        row = getService().insert(listFeedUrl, row);
    }

    public void createTransaction(String spreadsheetTitle, String worksheetTitle, InventoryTransaction transaction) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        HashMap<String, String> row = new HashMap<String, String>();
        for (String col : Constants.TRANSACTION_COLUMNS) {
            String value = "";
            switch (col) {
                case "SKU":
                    value = "'" + transaction.getItem().getSku(); //google sheet will remove leading 0 adding this to avoid it. remember to remove when retrieving.
                    break;
                case "NAME":
                    value = transaction.getItem().getName();
                    break;
                case "IMGURL":
                    value = transaction.getItem().getImgUrl();
                    break;
                case "INOROUT":
                    value = transaction.getTransactionType();
                    break;
                case "QUANTITY":
                    value = String.valueOf(transaction.getTransactionQuantity());
                    break;
                case "LASTUPDATE":
                    value = DateUtil.getCurrentTimestampAsString();
                    break;
            }
            row.put(col, value);

        }
        addNewLine(spreadsheetTitle, worksheetTitle, row);
    }
}