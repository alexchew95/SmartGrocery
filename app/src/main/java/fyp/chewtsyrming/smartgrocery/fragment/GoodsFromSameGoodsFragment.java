package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListAdapter;
import fyp.chewtsyrming.smartgrocery.object.GoodsList;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class GoodsFromSameGoodsFragment extends Fragment implements View.OnClickListener {
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();;
    private String userId= user.getUid();;
    ArrayList<GoodsList> goodsListArrayList;
    ListView listView;
    private static GoodsListAdapter adapter;
    TextView tvGoodsName;
    String barcode, goodsCategory, goodsID, imageURL, goodsName;
    UserModel um;
    ImageView ivGoods;
    Button sortQuantityAscBtn, sortQuantityDescBtn;
    ImageButton favBtn;
    Boolean goodsFav;
    FirebaseDatabase database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_goods_from_same_goods, null);
        listView = fragmentView.findViewById(R.id.listGoods);
        ivGoods = fragmentView.findViewById(R.id.ivGoods);
        tvGoodsName = fragmentView.findViewById(R.id.tvGoodsName);
        database = FirebaseDatabase.getInstance();

        Bundle cate = this.getArguments();
        barcode = cate.getString("barcode");
        goodsCategory = cate.getString("goodsCategory");
        imageURL = cate.getString("imageURL");
        goodsName = cate.getString("goodsName");
        goodsListArrayList = new ArrayList<>();
        tvGoodsName.setText(goodsName);
        listAllGoods();
        checkFavStatus();
        Picasso.get().load(imageURL).fit().into(ivGoods);
        sortQuantityAscBtn = fragmentView.findViewById(R.id.sortQuantityAscBtn);
        sortQuantityDescBtn = fragmentView.findViewById(R.id.sortQuantityDescBtn);
        favBtn = fragmentView.findViewById(R.id.favBtn);
        sortQuantityAscBtn.setOnClickListener(this);
        sortQuantityDescBtn.setOnClickListener(this);
        favBtn.setOnClickListener(this);
        goodsFav = false;

        return fragmentView;
    }

    private void listAllGoods() {
        um = new UserModel();
        String userID = um.getUserIDFromDataBase();
        DatabaseReference goodsReference = database.getReference().child("user").child(um.getUserIDFromDataBase()).
                child("goods").child(goodsCategory).child(barcode);
        goodsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goodsListArrayList.clear();

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String goodsID = snapshot.getKey();
                    String buyDate = (String) snapshot.child("insertDate").getValue();
                    String expirationDate = (String) snapshot.child("expirationDate").getValue();
                    String quantity = (String) snapshot.child("quantity").getValue();
                    final GoodsList goodsList = new GoodsList(goodsID, expirationDate, buyDate,
                            quantity, goodsCategory, barcode);
                    goodsListArrayList.add(goodsList);

                    adapter = new GoodsListAdapter(goodsListArrayList, getContext());
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            GoodsList goodsList1 = goodsListArrayList.get(position);
                            //Toast.makeText(getContext(), goodsList1.getGoodsId(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                goodsListArrayList.clear();

            }
        });

        //Toast.makeText(getContext(), imageURL  , Toast.LENGTH_LONG).show();

    }

    private void checkFavStatus() {
        DatabaseReference favCheckReff = database.getReference().child("user").child(um.getUserIDFromDataBase()).child("goods").child("fav");
        favCheckReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(barcode)) {
                    favBtn.setImageResource(R.drawable.ic_enabled_star);
                    goodsFav=true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void changeFavStatus() {
        DatabaseReference goodsFavReff = database.getReference().child("user").child(um.getUserIDFromDataBase()).
                child("goods").child("fav");
        if (goodsFav) {//task to do:remove from fav list
            goodsFavReff.child(barcode).removeValue();
            favBtn.setImageResource(R.drawable.ic_disabled_star);
            goodsFav = false;
        } else {
            //add to fav list
            goodsFavReff.child(barcode).setValue(goodsCategory);
            favBtn.setImageResource(R.drawable.ic_enabled_star);
            goodsFav = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sortQuantityAscBtn:
                Collections.sort(goodsListArrayList, GoodsList.sortExpDateAsc);
                adapter.notifyDataSetChanged();
                break;
            case R.id.sortQuantityDescBtn:
                Collections.sort(goodsListArrayList, Collections.reverseOrder(GoodsList.sortExpDateAsc));
                adapter.notifyDataSetChanged();
                break;
            case R.id.favBtn:
                changeFavStatus();

                break;
        }
    }

}
