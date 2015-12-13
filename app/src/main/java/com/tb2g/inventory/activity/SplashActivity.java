package com.tb2g.inventory.activity;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.tb2g.inventory.model.IntentResultBus;
import com.tb2g.inventory.service.IntentService;
import com.tb2g.inventory.util.Constants;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleSignInResult(GoogleSignInResult result) {
        super.handleSignInResult(result);

        //get extra auth for google sheets
        if (mAccount != null)
            new GoogleSheetTokenHelper().execute(mAccount.getEmail());

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case Constants.RESULT_CODE_INV_LOCATION:
                IntentResultBus result = resultData.getParcelable(Constants.EXTRA_INTENT_SERVICE_RESULT);

                if (result.getError() != null)
                    Toast.makeText(this, result.getError().errorMsg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, InventoryActivity.class);
                intent.putStringArrayListExtra(Constants.EXTRA_LOCATION, (ArrayList<String>)result.getLocations());
                startActivity(intent);
                finish();

                break;
        }
    }


    public class GoogleSheetTokenHelper extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String email = params[0];

            String SCOPE = "oauth2:https://docs.google.com/feeds/ https://docs.googleusercontent.com/ https://spreadsheets.google.com/feeds/";
            Account accountAdmin = new Account(email, "com.google");
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(SplashActivity.this, accountAdmin.name, SCOPE);

            } catch (GooglePlayServicesAvailabilityException playEx) {
                playEx.printStackTrace();
            } catch (UserRecoverableAuthException userAuthEx) {
                // Start the user recoverable action using the intent returned by
                // getIntent()
                SplashActivity.this.startActivityForResult(
                        userAuthEx.getIntent(), Constants.REQUEST_CODE_AUTH_SHEET);

                userAuthEx.printStackTrace();

            } catch (IOException transientEx) {
                transientEx.printStackTrace();
            } catch (GoogleAuthException authEx) {
                authEx.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.SHARED_PREF_SHEET_TOKEN, result);
            editor.commit();

            //if things go well, load google sheet data
            IntentService.getInventoryLocations(SplashActivity.this, mReceiver);

        }
    }
}
