package com.tb2g.spreadsheet;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.tb2g.spreadsheet.action.FOO";
    private static final String ACTION_BAZ = "com.tb2g.spreadsheet.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.tb2g.spreadsheet.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.tb2g.spreadsheet.extra.PARAM2";

    private int MY_ACTIVITYS_AUTH_REQUEST_CODE = 1000;


    public MyIntentService() {
        super("MyIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Activity activity, String param1, String param2) {
        Intent intent = new Intent(activity, MyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        activity.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionFoo(param1);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String token) {
        listFiles(token);
    }


    private void listFiles(String token){

        Log.d(this.getClass().getSimpleName(), token);

        SpreadsheetService service =
                new SpreadsheetService("MySpreadsheetIntegration");
        service.setProtocolVersion(SpreadsheetService.Versions.V3);
        token = getGoogleSheetToken();
        service.setHeader("Authorization", "Bearer " + token);

        try {
            // Define the URL to request.  This should never change.
            URL SPREADSHEET_FEED_URL = new URL(
                    "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

            // Make a request to the API and get all spreadsheets.
            SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            List<SpreadsheetEntry> spreadsheets = feed.getEntries();

            // Iterate through all of the spreadsheets returned
            for (SpreadsheetEntry spreadsheet : spreadsheets) {
                // Print the title of this spreadsheet to the screen
                System.out.println(spreadsheet.getTitle().getPlainText());
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }



    private String getGoogleSheetToken(){

        String SCOPE = "oauth2:https://docs.google.com/feeds/ https://docs.googleusercontent.com/ https://spreadsheets.google.com/feeds/";
        Account accountAdmin = new Account("cuongutd@gmail.com", "com.google");
        String token = null;
        try{
            token = GoogleAuthUtil.getToken(this, accountAdmin.name, SCOPE);
        } catch (GooglePlayServicesAvailabilityException playEx) {
            playEx.printStackTrace();
        } catch (UserRecoverableAuthException userAuthEx) {
            // Start the user recoverable action using the intent returned by
            // getIntent()
            startActivity(
                    userAuthEx.getIntent());
        } catch (IOException transientEx) {
            transientEx.printStackTrace();
        } catch (GoogleAuthException authEx) {
            authEx.printStackTrace();
        }

        return token;
    }
    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
