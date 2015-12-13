package com.tb2g.inventory.activity;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.tb2g.inventory.R;
import com.tb2g.inventory.activity.adapter.BaseResultReceiver;
import com.tb2g.inventory.util.Constants;

import java.io.IOException;

/**
 * Created by Cuong on 9/10/2015.
 * To track activity's lifecycle. Other activities in the project extend this base class
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
        , BaseResultReceiver.Receiver {

    private String LOG_TAG = this.getClass().getSimpleName();

    protected BaseResultReceiver mReceiver;

    protected GoogleApiClient mGoogleApiClient;

    GoogleSignInOptions mGoogleSignInOptions;

    protected ProgressDialog mProgressDialog;

    protected GoogleSignInAccount mAccount;

    // [START handleSignInResult]
    protected void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            mAccount = result.getSignInAccount();

        } else {
            // make sure all data is wiped out
            //Signed out, show unauthenticated UI. redirect user to login screen
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
    }

//    private void setGoogleSheetToken() {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        String token = sharedPref.getString(Constants.SHARED_PREF_SHEET_TOKEN, "");
//        if (TextUtils.isEmpty(token))
//            new GoogleSheetTokenHelper().execute(mAccount.getEmail());
//
//    }

    // [END handleSignInResult]
    protected boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading.....");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logd("onCreate");

        mReceiver = new BaseResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        logd("onConfigurationChanged");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        logd("onPostCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logd("onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logd("onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logd("onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mReceiver.isNull()) {
            mReceiver = new BaseResultReceiver(new Handler());
            mReceiver.setReceiver(this);
        }

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            logd("Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        logd("onStart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logd("onSaveInstanceState");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        logd("onResumeFragments");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logd("onStop");
        mReceiver.setReceiver(null); // clear receiver so no leaks.

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        if (!isConnectedToInternet()) {
            Toast.makeText(this, "Please internet connection!", Toast.LENGTH_LONG);
        }
        logd("onConnectionFailed:" + connectionResult);
    }



    protected void logd(String msg) {
        Log.d(LOG_TAG, msg);
    }

}
