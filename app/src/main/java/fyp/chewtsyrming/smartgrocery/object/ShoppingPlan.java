package fyp.chewtsyrming.smartgrocery.object;

public class ShoppingPlan {
    private String shoppingId,shoppingPlanName,dateCreated,noOfItem;

    public String getShoppingId() {
        return shoppingId;
    }

    public void setShoppingId(String shoppingId) {
        this.shoppingId = shoppingId;
    }

    public String getNoOfItem() {
        return noOfItem;
    }

    public void setNoOfItem(String noOfItem) {
        this.noOfItem = noOfItem;
    }

    public ShoppingPlan(String shoppingId, String shoppingPlanName, String dateCreated, String noOfItem ) {
        this.shoppingId = shoppingId;
        this.shoppingPlanName = shoppingPlanName;
        this.dateCreated = dateCreated;
        this.noOfItem = noOfItem;
    }

    public ShoppingPlan(String shoppingId, String shoppingPlanName, String dateCreated) {
        this.shoppingId = shoppingId;
        this.shoppingPlanName = shoppingPlanName;
        this.dateCreated = dateCreated;
    }

    public ShoppingPlan(String shoppingPlanName, String dateCreated) {
        this.shoppingPlanName = shoppingPlanName;
        this.dateCreated = dateCreated;
    }

    public String getShoppingPlanName() {
        return shoppingPlanName;
    }

    public void setShoppingPlanName(String shoppingPlanName) {
        this.shoppingPlanName = shoppingPlanName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ShoppingPlan() {
    }

    @Override
    public String toString() {
        return shoppingPlanName;
    }

    public String getID() {
        return shoppingId;
    }
}
