package fyp.chewtsyrming.smartgrocery.object;

public class ShoppingPlan {
    private String shoppingName,shoppingDate;
    private Boolean shoppingStatus;

    public ShoppingPlan(String shoppingName, String shoppingDate, Boolean shoppingStatus) {
        this.shoppingName = shoppingName;
        this.shoppingDate = shoppingDate;
        this.shoppingStatus = shoppingStatus;
    }

    public String getShoppingName() {
        return shoppingName;
    }

    public void setShoppingName(String shoppingName) {
        this.shoppingName = shoppingName;
    }

    public String getShoppingDate() {
        return shoppingDate;
    }

    public void setShoppingDate(String shoppingDate) {
        this.shoppingDate = shoppingDate;
    }

    public Boolean getShoppingStatus() {
        return shoppingStatus;
    }

    public void setShoppingStatus(Boolean shoppingStatus) {
        this.shoppingStatus = shoppingStatus;
    }


}
