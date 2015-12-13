package com.tb2g.inventory.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.tb2g.inventory.R;
import com.tb2g.inventory.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */

public class LoginActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private static final int RC_GET_TOKEN = 9002;

    @Bind(R.id.imageView)
    ImageView mStudentProfileImg;
    @Bind(R.id.username)
    TextView mUserName;
    @Bind(R.id.email)
    TextView mEmail;

    boolean mReturnImmediately;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mReturnImmediately = getIntent().getBooleanExtra(Constants.EXTRA_LOGIN_RETURN_FLAG, true);
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_LOGIN_RETURN_FLAG))
            mReturnImmediately = savedInstanceState.getBoolean(Constants.EXTRA_LOGIN_RETURN_FLAG);


        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        SignInButton signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(mGoogleSignInOptions.getScopeArray());
        // [END customize_button]
    }

    @Override
    public void onStart() {
        super.onStart(); //this will silently trying to connect, good or bad it will call handleSignInResult()
    }

    /**
     * For login activity, the signIn needs to be handled differently
     */
    @Override
    protected void handleSignInResult(GoogleSignInResult result) {
        logd("handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, set googleAccount
            mAccount = result.getSignInAccount();

            //going back to calling activity
            if (mReturnImmediately)
                finish();
            else
                updateUI(true);
        } else {
            //show unauth screen so user can login
            updateUI(false);

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }
    protected void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // make sure all data is wiped out
                        mAccount = null;
                        updateUI(false);
                    }
                });
    }

    // [START revokeAccess]
    protected void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

            mUserName.setText(mAccount.getDisplayName());
            mEmail.setText(mAccount.getEmail());

            Glide.with(this).load(mAccount.getPhotoUrl()).asBitmap().override(200, 200).centerCrop().into(new BitmapImageViewTarget(mStudentProfileImg) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(LoginActivity.this.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mStudentProfileImg.setImageDrawable(circularBitmapDrawable);
                }
            });

        } else {

            mUserName.setText("");
            mEmail.setText("");
            mStudentProfileImg.setImageResource(R.mipmap.ic_launcher);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.EXTRA_LOGIN_RETURN_FLAG, mReturnImmediately);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
    }

}