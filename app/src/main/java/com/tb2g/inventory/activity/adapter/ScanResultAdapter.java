package com.tb2g.inventory.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tb2g.inventory.R;
import com.tb2g.inventory.model.UPCSearchProduct;

import java.util.List;

/**
 * Created by Cuong on 12/11/2015.
 */
public class ScanResultAdapter extends ArrayAdapter<UPCSearchProduct> {

    Context context;

    public ScanResultAdapter(Context context, int resourceId, List<UPCSearchProduct> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView name;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        UPCSearchProduct rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.scanresult_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.scanproductname);
            holder.imageView = (ImageView) convertView.findViewById(R.id.scanproductimg);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.name.setText(rowItem.getProductname());
        Glide.with(context).load(rowItem.getImageurl()).into(holder.imageView);

        return convertView;
    }


}
