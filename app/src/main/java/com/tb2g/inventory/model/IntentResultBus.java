package com.tb2g.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.tb2g.inventory.model.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Cuong on 12/11/2015.
 */
public class IntentResultBus implements Parcelable {

    private List<String> locations;
    private List<InventoryItem> items;
    private HashMap<Integer, UPCSearchProduct> upcProducts;
    private ErrorCode error;

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }

    public HashMap<Integer, UPCSearchProduct> getUpcProducts() {
        return upcProducts;
    }

    public void setUpcProducts(HashMap<Integer, UPCSearchProduct> upcProducts) {
        this.upcProducts = upcProducts;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }

    public IntentResultBus() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.locations);
        dest.writeTypedList(items);
        dest.writeSerializable(this.upcProducts);
        dest.writeInt(this.error == null ? -1 : this.error.ordinal());
    }

    protected IntentResultBus(Parcel in) {
        this.locations = in.createStringArrayList();
        this.items = in.createTypedArrayList(InventoryItem.CREATOR);
        this.upcProducts = (HashMap<Integer, UPCSearchProduct>)in.readSerializable();
        int tmpError = in.readInt();
        this.error = tmpError == -1 ? null : ErrorCode.values()[tmpError];
    }

    public static final Creator<IntentResultBus> CREATOR = new Creator<IntentResultBus>() {
        public IntentResultBus createFromParcel(Parcel source) {
            return new IntentResultBus(source);
        }

        public IntentResultBus[] newArray(int size) {
            return new IntentResultBus[size];
        }
    };
}
