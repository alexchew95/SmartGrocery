package fyp.chewtsyrming.smartgrocery.object;

import java.util.Comparator;

public class GoodsGrid {
    private final String name;
    private final String imageUrl;
    private final String barcode;
    private final String category;

    // private final int imageResource;


    public GoodsGrid(String name, String imageUrl, String barcode, String category) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.barcode = barcode;
        this.category = category;
    }
    /*public GoodsGrid(String name, int imageResource) {
        this.name = name;

        this.imageResource = imageResource;

    }*/

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCategory() {
        return category;
    }

    /* public int getImageResource() {
            return imageResource;
        }*/
    public static Comparator<GoodsGrid> sortNameAZ = new Comparator<GoodsGrid>() {
        @Override
        public int compare(GoodsGrid o1, GoodsGrid o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

}
