package fyp.chewtsyrming.smartgrocery.object;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class GoodsList {
    private String goodsId, expiryDate, buyDate, quantity, category, barcode;

    public GoodsList(String goodsId, String expiryDate, String buyDate, String quantity
            , String category, String barcode) {
        this.goodsId = goodsId;
        this.expiryDate = expiryDate;
        this.buyDate = buyDate;
        this.quantity = quantity;
        this.category = category;
        this.barcode = barcode;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getBarcode() {
        return barcode;
    }

    //sort quantity

    public static Comparator<GoodsList> sortQuantityAsc = new Comparator<GoodsList>() {
        @Override
        public int compare(GoodsList o1, GoodsList o2) {
            int q1 = Integer.parseInt(o1.getQuantity());
            int q2 = Integer.parseInt(o2.getQuantity());

            return q1 - q2;
        }
    };

    //sort buy date

    public static Comparator<GoodsList> sortBuyDateAsc = new Comparator<GoodsList>() {
        @Override
        public int compare(GoodsList o1, GoodsList o2) {
           SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(o1.getBuyDate());
                d2 = sdf.parse(o2.getBuyDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return (d1.compareTo(d2));
        }
    };
    //sort expired date
    public static Comparator<GoodsList> sortExpDateAsc = new Comparator<GoodsList>() {
        @Override
        public int compare(GoodsList o1, GoodsList o2) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = sdf.parse(o1.getExpiryDate());
                d2 = sdf.parse(o2.getExpiryDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return (d1.compareTo(d2));
        }
    };
   

}
