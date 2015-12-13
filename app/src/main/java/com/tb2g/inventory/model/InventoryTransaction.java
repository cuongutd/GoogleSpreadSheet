package com.tb2g.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Cuong on 12/11/2015.
 */
public class InventoryTransaction implements Parcelable {
    InventoryItem item;
    private int transactionQuantity;
    private String transactionType; //IN or OUT

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    public int getTransactionQuantity() {
        return transactionQuantity;
    }

    public void setTransactionQuantity(int transactionQuantity) {
        this.transactionQuantity = transactionQuantity;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.item, 0);
        dest.writeInt(this.transactionQuantity);
        dest.writeString(this.transactionType);
    }

    public InventoryTransaction() {
    }

    protected InventoryTransaction(Parcel in) {
        this.item = in.readParcelable(InventoryItem.class.getClassLoader());
        this.transactionQuantity = in.readInt();
        this.transactionType = in.readString();
    }

    public static final Creator<InventoryTransaction> CREATOR = new Creator<InventoryTransaction>() {
        public InventoryTransaction createFromParcel(Parcel source) {
            return new InventoryTransaction(source);
        }

        public InventoryTransaction[] newArray(int size) {
            return new InventoryTransaction[size];
        }
    };
}
