package fyp.chewtsyrming.smartgrocery.object;

import android.os.Parcel;
import android.os.Parcelable;

public class SubGoods implements Parcelable {
   private String quantity, expirationDate, category, masterExpirationQuantityID, barcode, goodsName, alertType, alertData, insertDate,goodsLocation;


    public SubGoods() {

    }

    public SubGoods(String expirationDate, String quantity,String alertType,String alertData,String insertDate,String goodsLocation
    ) {
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.alertType = alertType;
        this.alertData = alertData;
        this.insertDate = insertDate;
        this.goodsLocation=goodsLocation;
    }



    protected SubGoods(Parcel in) {
        barcode = in.readString();
        category = in.readString();
        expirationDate = in.readString();
        masterExpirationQuantityID = in.readString();
        quantity = in.readString();
        goodsName = in.readString();
        alertType = in.readString();
        alertData = in.readString();
        insertDate = in.readString();
        goodsLocation = in.readString();
    }

    public static final Creator<SubGoods> CREATOR = new Creator<SubGoods>() {
        @Override
        public SubGoods createFromParcel(Parcel in) {
            return new SubGoods(in);
        }

        @Override
        public SubGoods[] newArray(int size) {
            return new SubGoods[size];
        }
    };

    public String getQuantity() {
        return quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getGoodsLocation() {
        return goodsLocation;
    }

    public void setGoodsLocation(String goodsLocation) {
        this.goodsLocation = goodsLocation;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertData() {
        return alertData;
    }

    public void setAlertData(String alertData) {
        this.alertData = alertData;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quantity);
        dest.writeString(expirationDate);
    }

    public String getCategory() {
        return category;
    }

    public String getMasterExpirationQuantityID() {
        return masterExpirationQuantityID;
    }

    public String getBarcode() {
        return barcode;
    }


    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMasterExpirationQuantityID(String masterExpirationQuantityID) {
        this.quantity = masterExpirationQuantityID;
    }


}
