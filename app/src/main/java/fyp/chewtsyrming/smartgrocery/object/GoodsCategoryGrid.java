package fyp.chewtsyrming.smartgrocery.object;

import java.util.Comparator;

public class GoodsCategoryGrid {
    private final String goodsCategory;

    private final int imageResource;


    public GoodsCategoryGrid(String goodsCategory, int imageResource) {
        this.goodsCategory = goodsCategory;

        this.imageResource = imageResource;

    }

    public String getGoodsCategory() {
        return goodsCategory;
    }


    public int getImageResource() {
        return imageResource;
    }


    public static Comparator<GoodsCategoryGrid> sortCategoryAlphabetAZ = new Comparator<GoodsCategoryGrid>() {
        @Override
        public int compare(GoodsCategoryGrid o1, GoodsCategoryGrid o2) {
            return o1.getGoodsCategory().compareToIgnoreCase(o2.getGoodsCategory());
            //return o2.getGoodsCategory().compareToIgnoreCase(o1.getGoodsCategory());
        }
    };

}
