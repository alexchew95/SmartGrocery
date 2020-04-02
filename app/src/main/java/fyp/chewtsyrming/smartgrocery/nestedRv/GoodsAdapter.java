package fyp.chewtsyrming.smartgrocery.nestedRv;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.FirebaseHandler;

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
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        final String currentDate = dateFormat.format(date);
        final String barcode = goods.getBarcode();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference barcodeReference = database.getReference().child("barcode").child(barcode);

        barcodeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String name = (String) dataSnapshot.child("goodsName").getValue();
                final String imageUrl = (String) dataSnapshot.child("imageURL").getValue();
                final String goodsCategory = (String) dataSnapshot.child("goodsCategory").getValue();


                holder.pb_item.setVisibility(View.GONE);
                FirebaseHandler fh = new FirebaseHandler();
                DatabaseReference expStatusRef = fh.getUserRef().child("goods").child(goodsCategory).child(barcode);

                expStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean earlieastExpDateExist = false;
                        String earliestexpirationDate = "asdsa  ";
                        int x = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            x++;

                            String expirationDate = snapshot.child("expirationDate").getValue(String.class);
                            if (!earlieastExpDateExist) {
                                earlieastExpDateExist = true;
                                earliestexpirationDate = expirationDate;
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                Date currentEarliestExpDate = null;
                                Date newExpDate = null;

                                try {
                                    currentEarliestExpDate = sdf.parse(earliestexpirationDate);
                                    newExpDate = sdf.parse(expirationDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (newExpDate.compareTo(currentEarliestExpDate) < 0) {

                                    earliestexpirationDate = expirationDate;

                                }


                            }
                            //when the last row is checked. calculate the remaining days
                            if (x ==  dataSnapshot.getChildrenCount()) {
                                Date date1=null;
                                Date date2 =null;
                                try {
                                    date1 = dateFormat.parse(currentDate);
                                    date2 = dateFormat.parse(earliestexpirationDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long diff = date2.getTime() - date1.getTime();
                                float daysF = (diff / (1000 * 60 * 60 * 24));
                                int days = Math.round(daysF);
                                String d = String.valueOf(days);
                                goodsList.get(position).setRemainingDays(d);
                                String statusMessage="";
                                if(days<0){
                                    days= Math.abs(days);
                                    String sDays= String.valueOf(days);
                                    statusMessage= "Expired "+sDays+ " days ago.";
                                    holder.tv_earliestExpDate.setText(statusMessage);
                                    holder.tv_earliestExpDate.setTextColor(Color.RED);
                                }
                                else if(days==0){
                                    days= Math.abs(days);
                                    String sDays= String.valueOf(days);
                                    statusMessage= "Expired  today";
                                    holder.tv_earliestExpDate.setText(statusMessage);
                                    holder.tv_earliestExpDate.setTextColor(Color.RED);
                                }
                                else{
                                    String sDays= String.valueOf(days);
                                    statusMessage= "Expiring in "+sDays+ " days.";
                                    holder.tv_earliestExpDate.setText(statusMessage);
                                    holder.tv_earliestExpDate.setTextColor(Color.GREEN);
                                }

                                holder.textViewTitle.setText(name);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_loading_static)
                                        .dontAnimate()
                                        .into(holder.imageViewMovie);

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
