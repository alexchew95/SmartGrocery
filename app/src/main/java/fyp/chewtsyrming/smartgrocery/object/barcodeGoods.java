package fyp.chewtsyrming.smartgrocery.object;

public class barcodeGoods {

    String barcode;
    String goodsCategory;
    String goodsName;

    public barcodeGoods() {
    }

    public barcodeGoods(String barcode, String goodsCategory, String goodsName) {
        this.barcode = barcode;
        this.goodsCategory = goodsCategory;
        this.goodsName = goodsName;
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
