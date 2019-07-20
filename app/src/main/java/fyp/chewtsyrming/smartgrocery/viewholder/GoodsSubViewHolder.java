package fyp.chewtsyrming.smartgrocery.viewholder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import fyp.chewtsyrming.smartgrocery.R;

public class GoodsSubViewHolder extends ChildViewHolder {
    public TextView list_item_subgoods_quantity;
    private TextView list_item_subgoods_expirationDate;
    public Button button1,button2,button3;


    public GoodsSubViewHolder(View itemView) {
        super(itemView);
        list_item_subgoods_quantity = (TextView) itemView.findViewById(R.id.list_item_subgoods_quantity);
        list_item_subgoods_expirationDate = (TextView) itemView.findViewById(R.id.list_item_subgoods_expirationDate);
        button1=itemView.findViewById(R.id.button1);
        button2=itemView.findViewById(R.id.button2);
        button3=itemView.findViewById(R.id.button3);



    }

    public void setGoodsExpirationDate(String expirationDate) {
        list_item_subgoods_expirationDate.setText("Expiration Date = "+expirationDate);
    }

    @SuppressLint("SetTextI18n")
    public void setGoodsQuantity(String quantity) {
        list_item_subgoods_quantity.setText("Quantity = "+quantity);
    }
}