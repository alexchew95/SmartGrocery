package fyp.chewtsyrming.smartgrocery.object;

import java.util.Comparator;

public class GoodsList {
    String goodsId, expiryDate, buyDate, quantity, category, barcode;

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

    public static Comparator<GoodsList> sortQuantityAsc=new Comparator<GoodsList>() {
        @Override
        public int compare(GoodsList o1, GoodsList o2) {
            int q1 = Integer.parseInt(o1.getQuantity());
            int q2 = Integer.parseInt(o2.getQuantity());

            return q1-q2;
        }
    };
    public static Comparator<GoodsList> sortQuantityDesc=new Comparator<GoodsList>() {
        @Override
        public int compare(GoodsList o1, GoodsList o2) {
            int q1 = Integer.parseInt(o1.getQuantity());
            int q2 = Integer.parseInt(o2.getQuantity());

            return q2-q1;
        }
    };

    //sort buy date
    //sort expired date
}
