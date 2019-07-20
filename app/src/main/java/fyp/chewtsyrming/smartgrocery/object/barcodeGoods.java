package fyp.chewtsyrming.smartgrocery.object;

public class barcodeGoods {

    String barCode;
    String goodsCategory;
    String goodsName;

    public barcodeGoods() {
    }

    public barcodeGoods(String barCode, String goodsCategory, String goodsName) {
        this.barCode = barCode;
        this.goodsCategory = goodsCategory;
        this.goodsName = goodsName;
    }

    public String getBarcode() {
        return barCode;
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

    public void setBarcode(String barCode) {
        this.barCode = barCode;
    }
}
