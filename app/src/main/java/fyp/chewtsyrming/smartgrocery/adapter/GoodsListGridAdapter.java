package fyp.chewtsyrming.smartgrocery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.GoodsGrid;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class GoodsListGridAdapter extends BaseAdapter {
    private final Context mContext;
    UserModel um;
    private List<GoodsGrid> goodsList;
    private List<GoodsGrid> tempList;
    private List<GoodsGrid> originalList = new ArrayList<>();

    public GoodsListGridAdapter(Context mContext, List<GoodsGrid> goodsList) {
        this.mContext = mContext;
        this.goodsList = goodsList;
        this.tempList = goodsList;
        //     this.tempList=new ArrayList<>();
        // this.tempList.addAll(goodsList);
    }

    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        um = new UserModel();
        String userId = um.getUserIDFromDataBase();
        final GoodsGrid book = goodsList.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_goods_list, null);

            final ImageView imageView = convertView.findViewById(R.id.imageview_goods_category);
            final ImageView imageview_favorite = convertView.findViewById(R.id.imageview_favorite);

            final TextView nameTextView = convertView.findViewById(R.id.textview_goods_name);


            final ViewHolder viewHolder = new GoodsListGridAdapter.ViewHolder(nameTextView, imageView, imageview_favorite);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//    viewHolder.imageViewCoverArt.setImageResource(book.getImageResource());
        //Picasso.get().load(book.getImageUrl()).fit().into(viewHolder.imageView);
        Glide.with(mContext)
                .load(book.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_loading_static)
                .dontAnimate()
                .into(viewHolder.imageView);
        //viewHolder.imageView.setImageResource(book.getImageResource());
        viewHolder.nameTextView.setText(book.getName());
        DatabaseReference favReff = FirebaseDatabase.getInstance().getReference().
                child("user").child(userId).child("goods").child("fav");
        favReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(book.getBarcode())) {
                    // Toast.makeText(mContext,book.getBarcode(),Toast.LENGTH_SHORT).show();
                    viewHolder.imageview_favorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                } else {
                    viewHolder.imageview_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return convertView;
    }

    //filter
    public void filter(String charText) {
        if (originalList.isEmpty()) {
            originalList.addAll(goodsList);
        }

        goodsList.clear();

        // filteredList.clear();
        if (charText.length() == 0) {
            goodsList.addAll(originalList);
            notifyDataSetChanged();

        } else {
            for (GoodsGrid goodsGrid : originalList) {
                if (containsIgnoreCase(goodsGrid.getName(), charText)) {

                    goodsList.add(goodsGrid);


                }
            }
        }
        notifyDataSetChanged();
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


}
