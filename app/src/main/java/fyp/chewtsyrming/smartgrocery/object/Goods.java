package fyp.chewtsyrming.smartgrocery.object;



import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;


public class Goods extends ExpandableGroup<SubGoods> {
    private int iconResId;

    public Goods(String title, List<SubGoods> items,int iconResId) {
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

        Goods goods = (Goods) o;

        return getIconResId() == goods.getIconResId();

    }

    @Override
    public int hashCode() {
        return getIconResId();
    }

}
