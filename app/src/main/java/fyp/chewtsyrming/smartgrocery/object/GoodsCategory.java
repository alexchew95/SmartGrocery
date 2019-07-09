package fyp.chewtsyrming.smartgrocery.object;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GoodsCategory extends ExpandableGroup<Goods> {
    private int iconResId;

    public GoodsCategory(String title, List<Goods> items,int iconResId) {
        super(title, items);
        this.iconResId = iconResId;

    }


    public int getIconResId() {
        return iconResId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoodsCategory)) return false;

        GoodsCategory goodsCategory = (GoodsCategory) o;

        return getIconResId() == goodsCategory.getIconResId();

    }

    @Override
    public int hashCode() {
        return getIconResId();
    }

}
