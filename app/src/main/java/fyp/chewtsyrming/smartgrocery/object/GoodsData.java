package fyp.chewtsyrming.smartgrocery.object;

public class GoodsData {
    private String activeDays;
    private String category;
    private String expiringSoon;
    private float rate;
    private String status;
    private Integer totalUsed;

    public GoodsData() {
    }

    public GoodsData(String activeDays, String category, String expiringSoon, float rate, String status, Integer totalUsed) {
        this.activeDays = activeDays;
        this.category = category;
        this.expiringSoon = expiringSoon;
        this.rate = rate;
        this.status = status;
        this.totalUsed = totalUsed;
    }

    public String getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(String activeDays) {
        this.activeDays = activeDays;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpiringSoon() {
        return expiringSoon;
    }

    public void setExpiringSoon(String expiringSoon) {
        this.expiringSoon = expiringSoon;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalUsed() {
        return totalUsed;
    }

    public void setTotalUsed(Integer totalUsed) {
        this.totalUsed = totalUsed;
    }
}