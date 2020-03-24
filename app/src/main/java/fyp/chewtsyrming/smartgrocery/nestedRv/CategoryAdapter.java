package fyp.chewtsyrming.smartgrocery.nestedRv;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragment.GoodsFromSameCategoryFragment;
import fyp.chewtsyrming.smartgrocery.fragment.GoodsFromSameGoodsFragment;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.HomeViewHolder> {
    private Context context;
    private List<Category> data;
    private RecyclerView.RecycledViewPool recycledViewPool;
    String barcode;
    String goodsCategory;
    String imageUrl;
    String goodsName;

    public CategoryAdapter(List<Category> data, Context context) {
        this.data = data;
        this.context = context;

        recycledViewPool = new RecyclerView.RecycledViewPool();

    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View theView = LayoutInflater.from(context).inflate(R.layout.row_home, parent, false);


        return new HomeViewHolder(theView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {

        String category = data.get(position).getGenre();
        if (category.equals("fav")) {
            category = "Favorite";
        }
        if (category.equals("recent")) {
            category = "Recently Added";
        }
        holder.textViewCategory.setText(category);
        GoodsAdapter horizontalAdapter = new GoodsAdapter(data.get(position).getList(), context);
        holder.recyclerViewHorizontal.setAdapter(horizontalAdapter);
        holder.recyclerViewHorizontal.setRecycledViewPool(recycledViewPool);

        holder.recyclerViewHorizontal.
                addOnItemTouchListener(new RecyclerTouchListener(context,
                        holder.recyclerViewHorizontal, new RecyclerTouchListener.ClickListener() {
                    List<Goods> goodsList = data.get(position).getList();

                    @Override
                    public void onClick(View view, int position) {
                        Goods goods = goodsList.get(position);
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference barcodeReference = database.getReference().child("barcode").child(goods.getId());

                        barcodeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                barcode = (String) dataSnapshot.child("barcode").getValue();
                                goodsCategory = (String) dataSnapshot.child("goodsCategory").getValue();
                                imageUrl = (String) dataSnapshot.child("imageURL").getValue();
                                goodsName = (String) dataSnapshot.child("goodsName").getValue();
                                Bundle cate = new Bundle();
                                cate.putString("barcode", barcode);
                                cate.putString("goodsCategory", goodsCategory);
                                cate.putString("imageURL", imageUrl);
                                cate.putString("goodsName", goodsName);
                                GoodsFromSameGoodsFragment frag = new GoodsFromSameGoodsFragment();
                                frag.setArguments(cate);
                                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, frag);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        Toast.makeText(context, goods.getName() + " is selected!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
        holder.tv_show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle cate = new Bundle();
                String category = data.get(position).getGenre();

                cate.putString("goodsCategory", category);

                GoodsFromSameCategoryFragment frag = new GoodsFromSameCategoryFragment();
                frag.setArguments(cate);
                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //  Collections.sort(goodsList, Goods.sortExpDateAsc);

    }


    @Override
    public int getItemCount() {
        return data.size();

    }


    public class HomeViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerViewHorizontal;
        private TextView textViewCategory, tv_show_all;
        private RelativeLayout relativeLayout;

        private LinearLayoutManager horizontalManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        public HomeViewHolder(View itemView) {
            super(itemView);

            recyclerViewHorizontal = itemView.findViewById(R.id.home_recycler_view_horizontal);
            recyclerViewHorizontal.setHasFixedSize(true);
            recyclerViewHorizontal.setNestedScrollingEnabled(false);
            recyclerViewHorizontal.setLayoutManager(horizontalManager);
            recyclerViewHorizontal.setItemAnimator(new DefaultItemAnimator());
            textViewCategory = itemView.findViewById(R.id.tv_movie_category);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            tv_show_all = itemView.findViewById(R.id.tv_show_all);
        }


    }


}
