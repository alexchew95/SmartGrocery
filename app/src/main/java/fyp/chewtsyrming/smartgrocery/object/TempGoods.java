package fyp.chewtsyrming.smartgrocery.object;

public class TempGoods {
    String goodsName;
    String barCode;
    String goodsCategory;
    public TempGoods(){

    }

    public TempGoods(String goodsName) {

        this.goodsName=goodsName;

    }  public TempGoods(String barCode, String goodsCategory,String goodsName) {
        this.barCode=barCode;
        this.goodsCategory=goodsCategory;
        this.goodsName=goodsName;

    }

    public void setBarCode(String barCode){
        this.barCode=barCode;
    }
    public void setGoodsName(String goodsName){
        this.goodsName=goodsName;
    }
    public void setGoodsCategory(String goodsCategory){
        this.goodsCategory=goodsCategory;
    }

}
