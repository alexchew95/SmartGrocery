package fyp.chewtsyrming.smartgrocery.viewholder;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.Goods;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class GoodsViewHolder extends GroupViewHolder {
    private TextView categoryName;
    private ImageView arrow;
    private ImageView icon;

    public GoodsViewHolder(View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.list_item_goods_name);
        icon = (ImageView) itemView.findViewById(R.id.list_item_genre_icon);
    }

    public void setcategoryTitle(ExpandableGroup goods) {
        categoryName.setText(goods.getTitle());
        icon.setBackgroundResource(((Goods) goods).getIconResId());
    }
    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}