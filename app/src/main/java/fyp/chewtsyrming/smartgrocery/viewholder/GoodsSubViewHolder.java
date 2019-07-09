package fyp.chewtsyrming.smartgrocery.viewholder;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import fyp.chewtsyrming.smartgrocery.R;

public class GoodsSubViewHolder extends ChildViewHolder {
    private TextView childTextView;
    private TextView childTextView2;

    public GoodsSubViewHolder(View itemView) {
        super(itemView);
        childTextView = (TextView) itemView.findViewById(R.id.list_item_subgoods_name);
        childTextView2 = (TextView) itemView.findViewById(R.id.list_item_subgoods_quantity);
    }

    public void setGoodsExpirationDate(String expirationDate) {
        childTextView.setText(expirationDate);
    }

    public void setGoodsQuantity(String quantity) {
        childTextView2.setText(quantity);
    }
}