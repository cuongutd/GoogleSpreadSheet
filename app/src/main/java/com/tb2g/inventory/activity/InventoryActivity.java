package com.tb2g.inventory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.tb2g.inventory.R;
import com.tb2g.inventory.activity.adapter.BaseResultReceiver;
import com.tb2g.inventory.activity.adapter.SectionsPagerAdapter;
import com.tb2g.inventory.model.IntentResultBus;
import com.tb2g.inventory.model.InventoryItem;
import com.tb2g.inventory.model.InventoryTransaction;
import com.tb2g.inventory.model.UPCSearchProduct;
import com.tb2g.inventory.service.IntentService;
import com.tb2g.inventory.util.Constants;
import com.zxing.integration.android.IntentIntegrator;
import com.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InventoryActivity extends BaseAppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private List<String> mLocations;

    private String mCurrentLocation;

    private String mScannedUPC;

    private ViewPager mViewPager;

    FloatingActionButton fab;

    @Bind(R.id.switchinout)
    Switch InvInorOut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_LOCATION))
            mLocations = savedInstanceState.getStringArrayList(Constants.EXTRA_LOCATION);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_UPC))
            mScannedUPC = savedInstanceState.getString(Constants.EXTRA_UPC);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_LOCATION_NAME))
            mCurrentLocation = savedInstanceState.getString(Constants.EXTRA_LOCATION_NAME);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupTabs();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(openScannerListener);

    }

    private void setupTabs() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mLocations = getIntent().getStringArrayListExtra(Constants.EXTRA_LOCATION);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSectionsPagerAdapter.setmInventoryLocations(mLocations);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    private View.OnClickListener openScannerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            IntentIntegrator integrator = new IntentIntegrator(InventoryActivity.this);
            integrator.addExtra("SCAN_WIDTH", 800);
            integrator.addExtra("SCAN_HEIGHT", 300);
            integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
            integrator.addExtra("PROMPT_MESSAGE", getString(R.string.scan_prompt_msg));
            integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);//scan product code type
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }else if (id == R.id.action_refresh){
            deepRefresh();
            return true;
        }else if (id == R.id.action_account){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(Constants.EXTRA_LOGIN_RETURN_FLAG, false);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_DIALOG) {
            if (resultCode == RESULT_OK) {
                InventoryTransaction pendingTran = data.getParcelableExtra(Constants.EXTRA_PENDING_INVENTORY_TRAN);
                showProgressDialog();
                IntentService.updateInventory(this, mReceiver, mCurrentLocation, pendingTran);
            }else{
            }
            return;
        }


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && "UPC_A".equals(result.getFormatName())) {
            mScannedUPC = result.getContents();
            if (mReceiver.isNull()) {
                mReceiver = new BaseResultReceiver(new Handler());
                mReceiver.setReceiver(this);
            }
            showProgressDialog();
            IntentService.getWebProductInfo(mReceiver, this, mScannedUPC);

        } else {
            showSnackbarNoAction(getString(R.string.sbar_cannot_read), true);
        }
    }

    private void showSnackbarNoAction(String msg, boolean indefinite) {
        Snackbar mSnackbar;

        int len = 0;
        if (indefinite) {
            len = Snackbar.LENGTH_INDEFINITE;
        } else
            len = Snackbar.LENGTH_LONG;
        mSnackbar = Snackbar.make(fab, msg, len);
        mSnackbar.show();
    }

    private void openDialog(HashMap<Integer, UPCSearchProduct> items){
        ArrayList<UPCSearchProduct> itemList = new ArrayList<UPCSearchProduct>();
        for (UPCSearchProduct item : items.values())
            itemList.add(item);

        Intent intent = new Intent(this, ScanResultActivity.class);
        intent.putParcelableArrayListExtra(Constants.EXTRA_SCAN_RESULT_LIST, itemList);


        int currentPage = mViewPager.getCurrentItem();

        List<InventoryItem> currentInvItems = mSectionsPagerAdapter.getFragment(currentPage).mItems;

        int qtyOnHand = 0;

        for (InventoryItem item : currentInvItems ) {
            logd(mScannedUPC);
            logd(item.getSku());
            if (mScannedUPC.equals(item.getSku())) {
                qtyOnHand = item.getQuantity();
                break;
            }
        }

        mCurrentLocation = mLocations.get(currentPage);

        CharSequence inOrOut = InvInorOut.isChecked()? InvInorOut.getTextOn() : InvInorOut.getTextOff();

        InventoryItem pendingInvItem = new InventoryItem();

        pendingInvItem.setSku(mScannedUPC);
        //pendingInvItem.setName();
        pendingInvItem.setQuantity(qtyOnHand);

        InventoryTransaction pendingTran = new InventoryTransaction();

        pendingTran.setTransactionType(inOrOut.toString());
        pendingTran.setItem(pendingInvItem);

        intent.putExtra(Constants.EXTRA_PENDING_INVENTORY_TRAN, pendingTran);
        intent.putExtra(Constants.EXTRA_LOCATION_NAME, mCurrentLocation);
        startActivityForResult(intent, Constants.REQUEST_CODE_DIALOG);

    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        hideProgressDialog();
        super.onReceiveResult(resultCode, resultData);
        IntentResultBus data = resultData.getParcelable(Constants.EXTRA_INTENT_SERVICE_RESULT);

        switch (resultCode) {
            case Constants.RESULT_CODE_WEB_PRODUCT:

                HashMap<Integer, UPCSearchProduct> items = data.getUpcProducts();

                openDialog(items);

                break;
            case Constants.RESULT_CODE_INV_UPDATE:
                refresh();
            case Constants.RESULT_CODE_INV_LOCATION: //deep refresh case
                finish();
                Intent intent = new Intent(this, InventoryActivity.class);
                intent.putStringArrayListExtra(Constants.EXTRA_LOCATION, (ArrayList<String>)data.getLocations());
                startActivity(intent);
                break;
        }
    }

    private void deepRefresh(){
        showProgressDialog();
        IntentService.getInventoryLocations(this, mReceiver);

    }

    private void refresh(){
        finish();
        startActivity(getIntent());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putStringArrayList(Constants.EXTRA_LOCATION, (ArrayList)mLocations);
        outState.putString(Constants.EXTRA_UPC, mScannedUPC);
        outState.putString(Constants.EXTRA_LOCATION_NAME, mCurrentLocation);
    }
}
