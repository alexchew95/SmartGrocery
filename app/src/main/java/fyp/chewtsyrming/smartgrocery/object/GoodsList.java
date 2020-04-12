package fyp.chewtsyrming.smartgrocery.object;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class GoodsList {
    public static Comparator<GoodsList> sortQuantityAsc = new Comparator<GoodsList>() {
        @Override
        public int compare(GoodsList o1, GoodsList o2) {
            int q1 = Integer.parseInt(o1.getQuantity());
            int q2 = Integer.parseInt(o2.getQuantity());

            return q1 - q2;
        }
    };
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
    private String goodsId, expiryDate, buyDate, quantity, category, barcode, goodsLocation;

    public GoodsList(String goodsId, String expiryDate, String buyDate, String quantity
            , String category, String barcode) {
        this.goodsId = goodsId;
        this.expiryDate = expiryDate;
        this.buyDate = buyDate;
        this.quantity = quantity;
        this.category = category;
        this.barcode = barcode;
    }

    public GoodsList(String goodsId, String expiryDate, String buyDate, String quantity,
                     String category, String barcode, String goodsLocation) {
        this.goodsId = goodsId;
        this.expiryDate = expiryDate;
        this.buyDate = buyDate;
        this.quantity = quantity;
        this.category = category;
        this.barcode = barcode;
        this.goodsLocation = goodsLocation;
    }

    public static Comparator<GoodsList> getSortQuantityAsc() {
        return sortQuantityAsc;
    }

    public static void setSortQuantityAsc(Comparator<GoodsList> sortQuantityAsc) {
        GoodsList.sortQuantityAsc = sortQuantityAsc;
    }

    public static Comparator<GoodsList> getSortBuyDateAsc() {
        return sortBuyDateAsc;
    }

    public static void setSortBuyDateAsc(Comparator<GoodsList> sortBuyDateAsc) {
        GoodsList.sortBuyDateAsc = sortBuyDateAsc;
    }

    public static Comparator<GoodsList> getSortExpDateAsc() {
        return sortExpDateAsc;
    }

    public static void setSortExpDateAsc(Comparator<GoodsList> sortExpDateAsc) {
        GoodsList.sortExpDateAsc = sortExpDateAsc;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getGoodsLocation() {
        return goodsLocation;
    }

    public void setGoodsLocation(String goodsLocation) {
        this.goodsLocation = goodsLocation;
    }
}
