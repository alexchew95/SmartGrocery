package fyp.chewtsyrming.smartgrocery.nestedRv;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import fyp.chewtsyrming.smartgrocery.FirebaseHandler;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class Goods {
    public static Comparator<Goods> sortRecentItem = new Comparator<Goods>() {
        @Override
        public int compare(Goods o1, Goods o2) {
            return o2.getTimeStamp().compareTo(o1.getTimeStamp());
        }
    };
    public static Comparator<Goods> sortExpDateAsc = new Comparator<Goods>() {
        @Override
        public int compare(Goods o1, Goods o2) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(o1.getExpirationDate());
                d2 = sdf.parse(o2.getExpirationDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return (d1.compareTo(d2));
        }
    };
    private String expiredSoon;
    private String barcode;
    private String goodsId;
    private String remainingDays;
    private String goodsName;
    private String imageURL;
    private String goodsCategory;
    private String expirationDate;
    private String quantity;
    private String goodsLocation;
    private String alertData;
    private String consumedRateStatus;
    private String alertDaysStatus;
    private String insertDate;
    private String timeStamp;
    private String activeDays;
    private String status;
    private String totalUsed;
    private String rate;

    public Goods(String barcode, String timeStamp) {
        this.barcode = barcode;
        this.timeStamp = timeStamp;
    }

    public Goods(String goodsCategory, String goodsLocation, String alertData, String consumedRateStatus, String alertDaysStatus, String activeDays, String status, String totalUsed, String rate) {
        this.goodsCategory = goodsCategory;
        this.goodsLocation = goodsLocation;
        this.alertData = alertData;
        this.consumedRateStatus = consumedRateStatus;
        this.alertDaysStatus = alertDaysStatus;
        this.activeDays = activeDays;
        this.status = status;
        this.totalUsed = totalUsed;
        this.rate = rate;
    }

    public Goods() {
    }

    public Goods(String barcode) {
        this.barcode = barcode;
    }

    public Goods(String barcode, String goodsName, String imageURL, String goodsCategory) {
        this.barcode = barcode;
        this.goodsName = goodsName;
        this.imageURL = imageURL;
        this.goodsCategory = goodsCategory;
    }

    public Goods(String barcode, String goodsName, String imageURL, String goodsCategory, String timeStamp) {
        this.barcode = barcode;
        this.goodsName = goodsName;
        this.imageURL = imageURL;
        this.goodsCategory = goodsCategory;
        this.timeStamp = timeStamp;
    }

    public Goods(String goodsName, String imageURL, String goodsCategory) {
        this.goodsName = goodsName;
        this.imageURL = imageURL;
        this.goodsCategory = goodsCategory;
    }

    public Goods(String goodsId, String goodsName, String imageURL, String goodsCategory, String expirationDate, String quantity, String goodsLocation) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.imageURL = imageURL;
        this.goodsCategory = goodsCategory;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.goodsLocation = goodsLocation;

    }

    public Goods(String barcode, String goodsId, String goodsName, String imageURL, String goodsCategory,
                 String expirationDate, String quantity, String goodsLocation, String alertData, String consumedRateStatus,
                 String alertDaysStatus, String insertDate) {
        this.barcode = barcode;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.imageURL = imageURL;
        this.goodsCategory = goodsCategory;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.goodsLocation = goodsLocation;
        this.alertData = alertData;
        this.consumedRateStatus = consumedRateStatus;
        this.alertDaysStatus = alertDaysStatus;
        this.insertDate = insertDate;
    }

    public String getAlertDaysStatus() {
        return alertDaysStatus;
    }

    public void setAlertDaysStatus(String alertDaysStatus) {
        this.alertDaysStatus = alertDaysStatus;
    }

    public String getConsumedRateStatus() {
        return consumedRateStatus;
    }

    public void setConsumedRateStatus(String consumedRateStatus) {
        this.consumedRateStatus = consumedRateStatus;
    }

    public String getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(String activeDays) {
        this.activeDays = activeDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalUsed() {
        return totalUsed;
    }

    public void setTotalUsed(String totalUsed) {
        this.totalUsed = totalUsed;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getAlertData() {
        return alertData;
    }

    public void setAlertData(String alertData) {
        this.alertData = alertData;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getExpiredSoon() {
        return expiredSoon;
    }

    public void setExpiredSoon(String expiredSoon) {
        this.expiredSoon = expiredSoon;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(String remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getGoodsLocation() {
        return goodsLocation;
    }

    public void setGoodsLocation(String goodsLocation) {
        this.goodsLocation = goodsLocation;
    }

    public void addGoods(Goods good) {
        UserModel um = new UserModel();
        String userId = um.getUserIDFromDataBase();
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                .child(goodsCategory).child(barcode);
        goodsId = reff.push().getKey();
        final DatabaseReference mainReff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods");

        reff = FirebaseDatabase.getInstance().getReference().child("user").child(userId).child("goods")
                .child(goodsCategory).child(barcode).child(goodsId);
        reff.setValue(good).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final DatabaseReference recentReff = mainReff.child("recent");
                final DatabaseReference recentReff2 = mainReff.child("recent").child(barcode);
                recentReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        Goods recentGoods = new Goods(barcode, goodsName, imageURL, goodsCategory, sdf.format(timestamp));
                        recentReff2.setValue(recentGoods);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void addNewBarcode() {
        final Goods goods = new Goods(barcode, goodsName, imageURL, goodsCategory);
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("barcode").child(barcode);
        reff.setValue(goods);
    }

    public void addGoods() {

        FirebaseHandler fh = new FirebaseHandler();

    }

}
