package com.tb2g.inventory.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tb2g.inventory.R;
import com.tb2g.inventory.activity.adapter.ScanResultAdapter;
import com.tb2g.inventory.model.InventoryTransaction;
import com.tb2g.inventory.model.UPCSearchProduct;
import com.tb2g.inventory.util.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScanResultActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.scanlist)
    ListView scanList;
    @Bind(R.id.scanmsg)
    TextView scanMsg;
    @Bind(R.id.scanprompttext)
    TextView scanprompttext;
    @Bind(R.id.editqty)
    EditText editQuantity;
    @Bind(R.id.invqty)
    TextView quantityOnHand;
    @Bind(R.id.btncancel)
    Button btnCancel;
    @Bind(R.id.btnok)
    Button btnOk;

    private List<UPCSearchProduct> scanItems;
    private ScanResultAdapter adapter;

    private String mCurrentLocation;
    private InventoryTransaction mPendingTran;

    int mSelectedProductPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ButterKnife.bind(this);

        scanItems = getIntent().getParcelableArrayListExtra(Constants.EXTRA_SCAN_RESULT_LIST);
        mCurrentLocation = getIntent().getStringExtra(Constants.EXTRA_LOCATION_NAME);
        mPendingTran = getIntent().getParcelableExtra(Constants.EXTRA_PENDING_INVENTORY_TRAN);

        int qtyOnHand = mPendingTran.getItem().getQuantity();
        quantityOnHand.setText(String.valueOf(qtyOnHand));
        scanprompttext.setText("Quantity " + mPendingTran.getTransactionType());
        adapter = new ScanResultAdapter(this, R.layout.scanresult_item, scanItems);
        scanList.setAdapter(adapter);

        scanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedProductPosition = position;
            }
        });

        String msg = "";
        if (scanItems.size() > 1) {
            scanList.setItemChecked(0, true);
            msg = "There are more than one product found with this UPC code. Please pick the right one below and adjust quantity if needed:";
        } else if (scanItems.size() == 1) {
            scanList.setItemChecked(0, true);
            msg = "Found the product. Please adjust the quantity if needed:";
        } else {
            msg = "No product found with this UPC!";
            btnOk.setEnabled(false);

        }

        scanMsg.setText(msg);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

    }


    private void updateInventory() {

        UPCSearchProduct selectedProduct = scanItems.get(mSelectedProductPosition);

        mPendingTran.getItem().setName(selectedProduct.getProductname());
        mPendingTran.getItem().setProductUrl(selectedProduct.getProducturl());
        mPendingTran.getItem().setImgUrl(selectedProduct.getImageurl());
        //mPendingTran.getItem().setPrice(Integer.valueOf(selectedProduct.getSaleprice()));
        //mPendingTran.getItem().setCost(Integer.valueOf(selectedProduct.getPrice());

        mPendingTran.setTransactionQuantity(Integer.valueOf(editQuantity.getText().toString()));

        //IntentService.updateInventory(this, mReceiver, mCurrentLocation, mPendingTran);
        //return to parent activity to do the invantory update
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_PENDING_INVENTORY_TRAN, mPendingTran);
        setResult(RESULT_OK, data);
        finish();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnok:
                updateInventory();
                break;
            case R.id.btncancel:
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
