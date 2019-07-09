package fyp.chewtsyrming.smartgrocery.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.Goods;

public class GoodsViewHolder extends GroupViewHolder {
    private TextView categoryName;
    private ImageView arrow;
    private ImageView icon;

    public GoodsViewHolder(View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.list_item_goods_name);
        arrow = (ImageView) itemView.findViewById(R.id.list_item_genre_arrow);
        icon = (ImageView) itemView.findViewById(R.id.list_item_genre_icon);
    }

    public void setcategoryTitle(ExpandableGroup goods) {
        categoryName.setText(goods.getTitle());
        icon.setBackgroundResource(((Goods) goods).getIconResId());
    }

}