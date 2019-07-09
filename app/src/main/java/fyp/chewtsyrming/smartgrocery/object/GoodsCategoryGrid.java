package fyp.chewtsyrming.smartgrocery.object;

public class GoodsCategoryGrid  {
    private final String name;

    private final int imageResource;


    public GoodsCategoryGrid(String name, int imageResource) {
        this.name = name;

        this.imageResource = imageResource;

    }

    public String getName() {
        return name;
    }


    public int getImageResource() {
        return imageResource;
    }


}
