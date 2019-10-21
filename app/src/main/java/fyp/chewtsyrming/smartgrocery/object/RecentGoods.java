package fyp.chewtsyrming.smartgrocery.object;

public class RecentGoods {
    String barcode, goodsID ,recentID;

    public  RecentGoods(){

    };
    public RecentGoods(String barcode, String goodsID){
        this.barcode=barcode;
        this.goodsID=goodsID;
    }
    public RecentGoods(String barcode, String goodsID, String recentID){
        this.barcode=barcode;
        this.goodsID=goodsID;
        this.recentID=recentID;
    }
    public String getBarcode(){
        return barcode;
    } public String getGoodsID(){
        return goodsID;
    } public String getRecentID(){
        return recentID;
    }
}
