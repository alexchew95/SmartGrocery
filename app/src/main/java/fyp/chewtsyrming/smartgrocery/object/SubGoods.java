package fyp.chewtsyrming.smartgrocery.object;

import android.os.Parcel;
import android.os.Parcelable;

public class SubGoods  implements Parcelable {
    String quantity;
    String expirationDate;

    public SubGoods() {

    }public SubGoods(String quantity, String expirationDate) {
        this.quantity=quantity;
this.expirationDate=expirationDate;

    }

    protected SubGoods(Parcel in) {
        quantity = in.readString();
        expirationDate = in.readString();
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

    public void setQuantity(String quantity){
        this.quantity= quantity;
    }
    public void setExpirationDate(String expirationDate){
        this.expirationDate=expirationDate;
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
}
