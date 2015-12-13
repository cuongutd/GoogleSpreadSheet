package com.tb2g.inventory.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tb2g.inventory.R;
import com.tb2g.inventory.activity.adapter.BaseResultReceiver;
import com.tb2g.inventory.activity.adapter.InventoryAdapter;
import com.tb2g.inventory.model.IntentResultBus;
import com.tb2g.inventory.model.InventoryItem;
import com.tb2g.inventory.service.IntentService;
import com.tb2g.inventory.util.Constants;

import java.util.ArrayList;

/**
 * Created by Cuong on 12/10/2015.
 */
public class PlaceholderFragment extends Fragment  implements BaseResultReceiver.Receiver{


    private RecyclerView mRecyclerView;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private InventoryAdapter mAdapter;
    private TextView mEmptyView;
    protected BaseResultReceiver mReceiver;

    public ArrayList<InventoryItem> mItems;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_LOCATION_NAME = "ARG_LOCATION_NAME";

    public PlaceholderFragment() {
        mItems = new ArrayList<InventoryItem>();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(String locationName) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOCATION_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_INVENTORY_ITEMS))
            mItems = savedInstanceState.getParcelableArrayList(Constants.EXTRA_INVENTORY_ITEMS);


        bindRecyclerView(rootView);

        mReceiver = new BaseResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        String location = getArguments().getString(ARG_LOCATION_NAME);

        IntentService.getInventoryItems(this.getActivity(), mReceiver, location);

        return rootView;
    }

    private void bindRecyclerView(View rootView) {

        mEmptyView = (TextView)rootView.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.inventoryitems);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new InventoryAdapter(mEmptyView, this.getActivity());

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case Constants.RESULT_CODE_INV_ITEM:
                IntentResultBus result = resultData.getParcelable(Constants.EXTRA_INTENT_SERVICE_RESULT);
                mItems = (ArrayList)result.getItems();
                mAdapter.swapList(mItems);
                break;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (mReceiver.isNull()) {
            mReceiver = new BaseResultReceiver(new Handler());
            mReceiver.setReceiver(this);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mReceiver.setReceiver(null); // clear receiver so no leaks.
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(Constants.EXTRA_INVENTORY_ITEMS,  mItems);

    }

}