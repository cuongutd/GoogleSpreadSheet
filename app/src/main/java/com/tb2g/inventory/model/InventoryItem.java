package com.tb2g.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Cuong on 12/10/2015.
 */
public class InventoryItem implements Parcelable {
    private String sku;
    private String name;
    private String imgUrl;
    private String productUrl;
    private int cost;
    private int price;
    private int quantity;
    private Date lastUpdate;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sku);
        dest.writeString(this.name);
        dest.writeString(this.imgUrl);
        dest.writeString(this.productUrl);
        dest.writeInt(this.cost);
        dest.writeInt(this.price);
        dest.writeInt(this.quantity);
        dest.writeLong(lastUpdate != null ? lastUpdate.getTime() : -1);
    }

    public InventoryItem() {
    }

    protected InventoryItem(Parcel in) {
        this.sku = in.readString();
        this.name = in.readString();
        this.imgUrl = in.readString();
        this.productUrl = in.readString();
        this.cost = in.readInt();
        this.price = in.readInt();
        this.quantity = in.readInt();
        long tmpLastUpdate = in.readLong();
        this.lastUpdate = tmpLastUpdate == -1 ? null : new Date(tmpLastUpdate);
    }

    public static final Parcelable.Creator<InventoryItem> CREATOR = new Parcelable.Creator<InventoryItem>() {
        public InventoryItem createFromParcel(Parcel source) {
            return new InventoryItem(source);
        }

        public InventoryItem[] newArray(int size) {
            return new InventoryItem[size];
        }
    };
}
