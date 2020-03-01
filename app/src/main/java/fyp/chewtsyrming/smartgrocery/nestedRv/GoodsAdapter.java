package fyp.chewtsyrming.smartgrocery.nestedRv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;

public class GoodsAdapter  extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {


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

        Goods goods = goodsList.get(position);



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference barcodeReference = database.getReference().child("barcode").child(goods.getName());

        barcodeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               String name= (String) dataSnapshot.child("goodsName").getValue();
               String imageUrl= (String) dataSnapshot.child("imageURL").getValue();


                holder.textViewTitle.setText(name);
                Picasso.get().load(imageUrl).fit().into(holder.imageViewMovie);

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

        private TextView textViewTitle;
        private ImageView imageViewMovie;


        public GoodsViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.tv_title);
            imageViewMovie = itemView.findViewById(R.id.image_view_movie);

        }


    }
}
