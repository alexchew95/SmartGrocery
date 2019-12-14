package fyp.chewtsyrming.smartgrocery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.GoodsList;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class GoodsListAdapter extends ArrayAdapter<GoodsList> implements View.OnClickListener {
    ArrayList<GoodsList> goodsLists;
    Context mContext;

    public static class ViewHolder {
        TextView tvQuantity, tvExpDate, tvBuyDate;
        ImageButton editBtn, deleteBtn;
    }

    public GoodsListAdapter(ArrayList<GoodsList> goodsLists, Context context) {
        super(context, R.layout.specific_goods_item, goodsLists);
        this.goodsLists = goodsLists;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GoodsList goodsList = getItem(position);
        ViewHolder viewHolder;
        final View view;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.specific_goods_item, parent, false);
            viewHolder.tvQuantity = convertView.findViewById(R.id.tvQuantityEdit);
            viewHolder.tvExpDate = convertView.findViewById(R.id.tvExpDateEdit);
            viewHolder.tvBuyDate = convertView.findViewById(R.id.tvBuyDateEdit);
            viewHolder.editBtn = convertView.findViewById(R.id.editBtn);
            ;
            viewHolder.deleteBtn = convertView.findViewById(R.id.deleteBtn);
            ;
            view = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            view = convertView;

        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        view.startAnimation(animation);
        lastPosition = position;
        viewHolder.tvExpDate.setText(goodsList.getExpiryDate());
        viewHolder.tvQuantity.setText(goodsList.getQuantity());
        viewHolder.tvBuyDate.setText(goodsList.getBuyDate());
        viewHolder.editBtn.setOnClickListener(this);
        viewHolder.editBtn.setTag(position);
        viewHolder.deleteBtn.setOnClickListener(this);
        viewHolder.deleteBtn.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        GoodsList goodsList = (GoodsList) object;

        switch (v.getId()) {
            case R.id.editBtn:
               // editDialog();
                break;
            case R.id.deleteBtn:
                Toast.makeText(getContext(), goodsList.getBuyDate(), Toast.LENGTH_SHORT).show();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                UserModel um = new UserModel();
                String userID = um.getUserIDFromDataBase();

                DatabaseReference goodsReference = database.getReference().child("user").child(userID).
                        child("goods").child(goodsList.getCategory()).child(goodsList.getBarcode())
                        .child(goodsList.getGoodsId());
                goodsReference.removeValue();
                break;
        }
    }

}
