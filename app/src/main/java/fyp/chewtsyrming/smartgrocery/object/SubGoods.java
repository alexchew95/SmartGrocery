package fyp.chewtsyrming.smartgrocery.object;

import android.os.Parcel;
import android.os.Parcelable;

public class SubGoods implements Parcelable {
    String quantity, expirationDate, category, masterExpirationQuantityID, barcode, goodsName;


    public SubGoods() {

    }

    public SubGoods(String expirationDate,String quantity
    ) {
        this.expirationDate = expirationDate;
        this.quantity = quantity;

    }

    public SubGoods(String barcode, String category, String expirationDate,
                    String masterExpirationQuantityID, String quantity,String goodsName
    ) {
        this.category = category;
        this.expirationDate = expirationDate;
        this.masterExpirationQuantityID = masterExpirationQuantityID;
        this.quantity = quantity;
        this.barcode = barcode;
        this.goodsName=goodsName;
    }

    protected SubGoods(Parcel in) {
        barcode = in.readString();
        category = in.readString();
        expirationDate = in.readString();
        masterExpirationQuantityID = in.readString();
        quantity = in.readString();
        goodsName = in.readString();
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
