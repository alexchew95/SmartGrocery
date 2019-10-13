package fyp.chewtsyrming.smartgrocery.object;

public class BarcodeGoods {

    String barcode;
    String goodsCategory;
    String goodsName;
    String imageURL;

    public BarcodeGoods() {
    }

    public BarcodeGoods(String barcode, String goodsCategory, String goodsName) {
        this.barcode = barcode;
        this.goodsCategory = goodsCategory;
        this.goodsName = goodsName;
    } public BarcodeGoods(String barcode, String goodsCategory, String goodsName,String imageURL) {
        this.barcode = barcode;
        this.goodsCategory = goodsCategory;
        this.goodsName = goodsName;
        this.imageURL = imageURL;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
