package com.tb2g.inventory.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tb2g.inventory.R;
import com.tb2g.inventory.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cuong on 12/10/2015.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryItemHolder> {

    private List<InventoryItem> items;
    private TextView mEmptyView;
    private Context mContext;

    public InventoryAdapter(TextView emptyView, Context context) {
        mEmptyView = emptyView;
        mContext = context;
        items = new ArrayList<InventoryItem>();
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    @Override
    public InventoryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (parent instanceof RecyclerView) {

            int layoutId = R.layout.item;

            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

            final InventoryItemHolder holder = new InventoryItemHolder(view);
            return holder;

        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(InventoryItemHolder holder, int position) {
        InventoryItem item = items.get(position);

        holder.name.setText(item.getName());
        holder.sku.setText(item.getSku());
        holder.qty.setText(String.valueOf(item.getQuantity()));
        Glide.with(mContext).load(item.getImgUrl()).into(holder.productImg);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void swapList(List<InventoryItem> newItems){
        items = newItems;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
