package com.tb2g.spreadsheet;

import android.accounts.Account;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private int MY_ACTIVITYS_AUTH_REQUEST_CODE = 1000;

    private ImageView mStudentProfileImg;
    private TextView mUserName;
    private TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //http://stackoverflow.com/questions/33199764/android-api-23-change-navigation-view-headerlayout-textview
        View headerView = navigationView.getHeaderView(0);

        mStudentProfileImg = (ImageView)headerView.findViewById(R.id.imageView);
        mUserName = (TextView)headerView.findViewById(R.id.username);
        mEmail = (TextView)headerView.findViewById(R.id.email);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.listfile).setOnClickListener(this);
        SignInButton signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(mGoogleSignInOptions.getScopeArray());
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateViewData();
    }


    private void populateViewData() {
        if (mAccount == null)
            return;
        mUserName.setText(mAccount.getDisplayName());
        mEmail.setText(mAccount.getEmail());

        Glide.with(this).load(mAccount.getPhotoUrl()).asBitmap().override(96, 96).centerCrop().into(new BitmapImageViewTarget(mStudentProfileImg) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(MainActivity.this.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                mStudentProfileImg.setImageDrawable(circularBitmapDrawable);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            signIn();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.listfile:
                new SpreadSheetTask().execute("");
                break;
        }
    }



    public class SpreadSheetTask extends AsyncTask<String, Integer, String> {

        private final String LOG_TAG = SpreadSheetTask.class.getSimpleName();

        private void listFiles(){

            SpreadsheetService service =
                    new SpreadsheetService("MySpreadsheetIntegration");
            service.setProtocolVersion(SpreadsheetService.Versions.V3);
            String token = getGoogleSheetToken();

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
            Account accountAdmin = new Account(mAccount.getEmail(), "com.google");
            String token = null;
            try{
                token = GoogleAuthUtil.getToken(MainActivity.this, accountAdmin.name, SCOPE);
            } catch (GooglePlayServicesAvailabilityException playEx) {
                playEx.printStackTrace();
            } catch (UserRecoverableAuthException userAuthEx) {
                // Start the user recoverable action using the intent returned by
                // getIntent()
                MainActivity.this.startActivityForResult(
                        userAuthEx.getIntent(), MY_ACTIVITYS_AUTH_REQUEST_CODE);
            } catch (IOException transientEx) {
                transientEx.printStackTrace();
            } catch (GoogleAuthException authEx) {
                authEx.printStackTrace();
            }

            return token;
        }

        @Override
        protected String doInBackground(String... artistName){
            listFiles();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}
