package fyp.chewtsyrming.smartgrocery.nestedRv;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private String userId = user.getUid();

    private List<Goods> goodsList;
    private Context context;

    public GoodsAdapter(List<Goods> list, Context context) {
        this.goodsList = list;
        this.context = context;


    }


    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new GoodsViewHolder(LayoutInflater.from(context).inflate(R.layout.inner_goods_rv_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodsViewHolder holder, final int position) {

        final Goods goods = goodsList.get(position);
        //holder.tv_earliestExpDate.setText(goods.getExpiredSoon());
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        final String currentDate = dateFormat.format(date);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference barcodeReference = database.getReference().child("barcode").child(goods.getId());

        barcodeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("goodsName").getValue();
                String imageUrl = (String) dataSnapshot.child("imageURL").getValue();
                goods.setName(name);
                goods.setImageUrl(imageUrl);
                holder.textViewTitle.setText(goods.getName());
              //  Glide.with(context).asGif().load(R.drawable.loading_spinner).into(holder.imageViewMovie);
                Glide.with(context)
                        .load(goods.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading_static)
                        .into(holder.imageViewMovie);
                // Picasso.get().load(goods.getImageUrl()).fit().into(holder.imageViewMovie);
                holder.pb_item.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String barcode = goods.getId();
        DatabaseReference databaseReference = database.getReference().child("user").child(userId).child("goodsData");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(barcode)) {
                    String expired = dataSnapshot.child(barcode).child("expiringSoon").getValue().toString();
                    goods.setExpiredSoon(expired);
                    try {
                        Date date1 = dateFormat.parse(currentDate);
                        Date date2 = dateFormat.parse(expired);
                        long diff = date2.getTime() - date1.getTime();
                        float daysF = (diff / (1000 * 60 * 60 * 24));
                        int days = Math.round(daysF);
                        String d = String.valueOf(days);
                        String daysLeft = d + " days left.";
                        goods.setRemainingDays(d);

                        if (days < 1) {
                            holder.tv_earliestExpDate.setText("EXPIRED");
                            holder.tv_earliestExpDate.setTextColor(Color.parseColor("#FFFF0000"));

                        } else {
                            holder.tv_earliestExpDate.setText(daysLeft);
                            holder.tv_earliestExpDate.setTextColor(Color.parseColor("#FF1A1A1A"));

                        }


                        //Toast.makeText(context,Float.toString(days), Toast.LENGTH_SHORT).show();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Collections.sort(goodsList, new Comparator<Goods>() {
                        public int compare(Goods o1, Goods o2) {

                            return o1.getRemainingDays().compareTo(o2.getRemainingDays());
                        }
                    });
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

      /*  Collections.sort(goodsList, new Comparator<Goods>() {
            public int compare(Goods o1, Goods o2) {
                Toast.makeText(context, o2.getExpiredSoon(), Toast.LENGTH_SHORT).show();

                return o2.getExpiredSoon().compareTo(o1.getExpiredSoon());
            }
        });*/
    }

    @Override
    public int getItemCount() {

        return goodsList.size();

    }


    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle, tv_earliestExpDate;
        private ImageView imageViewMovie;
        private ProgressBar pb_item;

        public GoodsViewHolder(View itemView) {
            super(itemView);
            pb_item = itemView.findViewById(R.id.pb_item);
            tv_earliestExpDate = itemView.findViewById(R.id.tv_earliestExpDate);
            textViewTitle = itemView.findViewById(R.id.tv_title);
            imageViewMovie = itemView.findViewById(R.id.image_view_movie);

        }


    }
}
