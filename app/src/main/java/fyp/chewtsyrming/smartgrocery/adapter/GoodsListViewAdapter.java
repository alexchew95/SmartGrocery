package fyp.chewtsyrming.smartgrocery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import fyp.chewtsyrming.smartgrocery.object.Goods;
import fyp.chewtsyrming.smartgrocery.viewholder.GoodsSubViewHolder;
import fyp.chewtsyrming.smartgrocery.viewholder.GoodsViewHolder;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.SubGoods;

public class GoodsListViewAdapter extends ExpandableRecyclerViewAdapter<GoodsViewHolder, GoodsSubViewHolder> {
    public GoodsListViewAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public GoodsViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_goods, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public GoodsSubViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subgoods, parent, false);
        return new GoodsSubViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(GoodsSubViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final SubGoods goodsSubView = ((Goods) group).getItems().get(childIndex);
        holder.setGoodsExpirationDate(goodsSubView.getExpirationDate());
        holder.setGoodsQuantity(goodsSubView.getQuantity());
    }

    @Override
    public void onBindGroupViewHolder(GoodsViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setcategoryTitle(group);
    }


}
