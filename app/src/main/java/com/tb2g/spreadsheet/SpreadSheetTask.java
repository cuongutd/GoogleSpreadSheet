package com.tb2g.spreadsheet;

import android.accounts.Account;
import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cuong on 12/10/2015.
 */
public class SpreadSheetTask extends AsyncTask<String, Integer, Void> {

    private final String LOG_TAG = SpreadSheetTask.class.getSimpleName();

    private int MY_ACTIVITYS_AUTH_REQUEST_CODE = 1000;


    private Activity mActivity;
    private String mAction;
    private String mEmail;

    public SpreadSheetTask(Activity activity, String action, String email) {
        mActivity = activity;
        mAction = action;
        mEmail = email;
    }


    @Override
    protected Void doInBackground(String... params) {
        switch (mAction) {
            case "LIST_FILES":
                printFileList();
                break;
            case "ADD_LINE":
                String spreadsheet = params[0];
                String worksheet = params[1];
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Name", "Cuong Nguyen");
                map.put("Age", "38");
                map.put("Created", new java.util.Date().toString());

                addNewLine(getService(), spreadsheet, worksheet, map);

                break;

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }

    private void printFileList(){

        List<SpreadsheetEntry> spreadsheets = listFiles(getService());

        // Iterate through all of the spreadsheets returned
        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            // Print the title of this spreadsheet to the screen
            System.out.println(spreadsheet.getTitle().getPlainText());
        }
    }

    private SpreadsheetService getService(){

        SpreadsheetService service =
                new SpreadsheetService("MySpreadsheetIntegration");
        service.setProtocolVersion(SpreadsheetService.Versions.V3);
        String token = getGoogleSheetToken();

        service.setHeader("Authorization", "Bearer " + token);

        return service;
    }


    private List<SpreadsheetEntry> listFiles(SpreadsheetService service) {

        try {
            // Define the URL to request.  This should never change.
            URL SPREADSHEET_FEED_URL = new URL(
                    "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

            // Make a request to the API and get all spreadsheets.
            SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            return feed.getEntries();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    private String getGoogleSheetToken() {

        String SCOPE = "oauth2:https://docs.google.com/feeds/ https://docs.googleusercontent.com/ https://spreadsheets.google.com/feeds/";
        Account accountAdmin = new Account(mEmail, "com.google");
        String token = null;
        try {
            token = GoogleAuthUtil.getToken(mActivity, accountAdmin.name, SCOPE);
        } catch (GooglePlayServicesAvailabilityException playEx) {
            playEx.printStackTrace();
        } catch (UserRecoverableAuthException userAuthEx) {
            // Start the user recoverable action using the intent returned by
            // getIntent()
            mActivity.startActivityForResult(
                    userAuthEx.getIntent(), MY_ACTIVITYS_AUTH_REQUEST_CODE);
        } catch (IOException transientEx) {
            transientEx.printStackTrace();
        } catch (GoogleAuthException authEx) {
            authEx.printStackTrace();
        }

        return token;
    }

    private SpreadsheetEntry findSpreadSheet(SpreadsheetService service, String spreadsheetTitle){
        List<SpreadsheetEntry> spreadsheets = listFiles(service);

        SpreadsheetEntry sheetToWorkOn = null;

        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            if (spreadsheetTitle.equals(spreadsheet.getTitle().getPlainText())) {
                sheetToWorkOn = spreadsheet;
                break;
            }
        }

        return sheetToWorkOn;
    }

    private void addNewWorkSheet(SpreadsheetService service, String spreadsheetTitle, String worksheetTitle, int col, int row){

        SpreadsheetEntry sheetToWorkOn = findSpreadSheet(service, spreadsheetTitle);

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
        try {
            service.insert(worksheetFeedUrl, worksheet);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private WorksheetEntry findWorkSheet(SpreadsheetService service, String spreadsheetTitle, String worksheetTitle){

        WorksheetEntry returnWorkSheet = null;

        SpreadsheetEntry spreadsheet = findSpreadSheet(service, spreadsheetTitle);
        try {
            WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
            List<WorksheetEntry> worksheets = worksheetFeed.getEntries();


            for (WorksheetEntry entry : worksheets)
                if (worksheetTitle.equals(entry.getTitle().getPlainText())){

                    returnWorkSheet = entry;
                    break;
                }
        }catch(Exception e){
            e.printStackTrace();
        }

        return returnWorkSheet;

    }

    private void printWorkSheet(SpreadsheetService service, String spreadsheetTitle, String worksheetTitle){

        try {
            WorksheetEntry worksheet = findWorkSheet(service, spreadsheetTitle, worksheetTitle);

            // Fetch the list feed of the worksheet.
            URL listFeedUrl = worksheet.getListFeedUrl();

            ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

            // Iterate through each row, printing its cell values.
            for (ListEntry row : listFeed.getEntries()) {
                // Print the first column's cell value
                System.out.print(row.getTitle().getPlainText() + "\t");
                // Iterate over the remaining columns, and print each cell value
                for (String tag : row.getCustomElements().getTags()) {
                    System.out.print(row.getCustomElements().getValue(tag) + "\t");
                }
                System.out.println();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void addNewLine(SpreadsheetService service, String spreadsheetTitle, String worksheetTitle, HashMap<String, String> value){

        try {
            WorksheetEntry worksheet = findWorkSheet(service, spreadsheetTitle, worksheetTitle);

            URL listFeedUrl = worksheet.getListFeedUrl();
            // Create a local representation of the new row.
            ListEntry row = new ListEntry();
            for (Map.Entry<String, String> entry : value.entrySet()) {
                row.getCustomElements().setValueLocal(entry.getKey(), entry.getValue());
            }

            // Send the new row to the API for insertion.
            row = service.insert(listFeedUrl, row);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}