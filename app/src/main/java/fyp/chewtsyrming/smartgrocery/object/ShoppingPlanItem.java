package fyp.chewtsyrming.smartgrocery.object;

public class ShoppingPlanItem {
    String shoppingPlanID,itemID, buyStatus,goodsBarcode,goodsCategory,goodsName,inventoryStatus,quantity;

    public ShoppingPlanItem(String shoppingPlanID, String itemID, String buyStatus, String goodsBarcode, String goodsCategory, String goodsName, String inventoryStatus, String quantity) {
        this.shoppingPlanID = shoppingPlanID;
        this.itemID = itemID;
        this.buyStatus = buyStatus;
        this.goodsBarcode = goodsBarcode;
        this.goodsCategory = goodsCategory;
        this.goodsName = goodsName;
        this.inventoryStatus = inventoryStatus;
        this.quantity = quantity;
    }

    public ShoppingPlanItem(String shoppingPlanID, String buyStatus, String goodsBarcode, String goodsCategory, String goodsName, String inventoryStatus, String quantity) {
        this.shoppingPlanID = shoppingPlanID;
        this.buyStatus = buyStatus;
        this.goodsBarcode = goodsBarcode;
        this.goodsCategory = goodsCategory;
        this.goodsName = goodsName;
        this.inventoryStatus = inventoryStatus;
        this.quantity = quantity;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getShoppingPlanID() {
        return shoppingPlanID;
    }

    public void setShoppingPlanID(String shoppingPlanID) {
        this.shoppingPlanID = shoppingPlanID;
    }

    public String getGoodsBarcode() {
        return goodsBarcode;
    }

    public void setGoodsBarcode(String goodsBarcode) {
        this.goodsBarcode = goodsBarcode;
    }

    public String getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(String buyStatus) {
        this.buyStatus = buyStatus;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public ShoppingPlanItem() {

    }

}
