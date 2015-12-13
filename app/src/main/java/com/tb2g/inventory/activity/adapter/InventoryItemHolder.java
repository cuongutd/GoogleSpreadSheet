package com.tb2g.inventory.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tb2g.inventory.R;

/**
 * Created by Cuong on 12/10/2015.
 */
public class InventoryItemHolder extends RecyclerView.ViewHolder {

    TextView sku;
    TextView name;
    TextView qty;
    ImageView productImg;


    public InventoryItemHolder(View itemView) {
        super(itemView);
        sku = (TextView) itemView.findViewById(R.id.sku);
        name = (TextView) itemView.findViewById(R.id.productname);
        qty = (TextView) itemView.findViewById(R.id.quantity);
        productImg = (ImageView) itemView.findViewById(R.id.productimg);
    }




}
