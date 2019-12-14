package fyp.chewtsyrming.smartgrocery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Locale;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.GoodsGrid;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class GoodsListGridAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<GoodsGrid> goodsSpecific;
    private List<GoodsGrid> filteredList;
UserModel um;
    public GoodsListGridAdapter(Context mContext, List<GoodsGrid> goodsSpecific) {
        this.mContext = mContext;
        this.goodsSpecific = goodsSpecific;
        this.filteredList = goodsSpecific;
    }

    @Override
    public int getCount() {
        return goodsSpecific.size();
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public Object getItem(int position) {
        return goodsSpecific.get(position);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
um = new UserModel();
String userId= um.getUserIDFromDataBase();
        final GoodsGrid book = goodsSpecific.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_goods_list, null);

            final ImageView imageView = convertView.findViewById(R.id.imageview_goods_category);
            final ImageView imageview_favorite = convertView.findViewById(R.id.imageview_favorite);

            final TextView nameTextView = convertView.findViewById(R.id.textview_goods_name);


            final ViewHolder viewHolder = new GoodsListGridAdapter.ViewHolder(nameTextView, imageView,imageview_favorite);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//    viewHolder.imageViewCoverArt.setImageResource(book.getImageResource());
        Picasso.get().load(book.getImageUrl()).fit().into( viewHolder.imageView);
        //viewHolder.imageView.setImageResource(book.getImageResource());
        viewHolder.nameTextView.setText(book.getName());
        DatabaseReference favReff= FirebaseDatabase.getInstance().getReference().
                child("user").child(userId).child("goods").child("fav");
        favReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(book.getBarcode())){
viewHolder.imageview_favorite.setImageResource(R.drawable.ic_enabled_star);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return convertView;
    }

    private class ViewHolder {
        private final TextView nameTextView;
        private final ImageView imageView;
        private final ImageView imageview_favorite;


        public ViewHolder(TextView nameTextView, ImageView imageView, ImageView imageview_favorite) {
            this.nameTextView = nameTextView;

            this.imageView = imageView;
            this.imageview_favorite = imageview_favorite;

        }
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        goodsSpecific.clear();
        if (charText.length() == 0) {
            goodsSpecific.addAll(filteredList);
        } else {
            for (GoodsGrid wp : filteredList) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    goodsSpecific.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
