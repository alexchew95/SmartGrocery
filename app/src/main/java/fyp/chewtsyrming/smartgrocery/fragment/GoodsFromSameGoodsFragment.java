package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListAdapter;
import fyp.chewtsyrming.smartgrocery.nestedRv.Goods;
import fyp.chewtsyrming.smartgrocery.object.GoodsList;
import fyp.chewtsyrming.smartgrocery.object.UserModel;

public class GoodsFromSameGoodsFragment extends Fragment implements View.OnClickListener {
    private static GoodsListAdapter adapter;

    private ArrayList<GoodsList> goodsListArrayList;
    private ArrayList<Goods> goodsArrayList;

    private ListView listGoods;
    private TextView tvGoodsName, tv_itemStatus;
    private String barcode, goodsCategory, goodsID, imageURL, goodsName;
    private UserModel um;
    private ImageView ivGoods;
    private ImageButton favBtn, settingButton, ib_back;
    private Boolean goodsFav;
    private FirebaseDatabase database;
    private FragmentHandler fragmentHandler;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_goods_from_same_goods, null);
        listGoods = fragmentView.findViewById(R.id.listGoods);
        ivGoods = fragmentView.findViewById(R.id.ivGoods);
        tvGoodsName = fragmentView.findViewById(R.id.tvGoodsName);
        tv_itemStatus = fragmentView.findViewById(R.id.tv_itemStatus);

        database = FirebaseDatabase.getInstance();
        fragmentHandler = new FragmentHandler();
        Bundle cate = this.getArguments();
        barcode = cate.getString("barcode");
        goodsCategory = cate.getString("goodsCategory");
        imageURL = cate.getString("imageURL");
        goodsName = cate.getString("goodsName");
        goodsArrayList = new ArrayList<>();
        tvGoodsName.setText(goodsName);
        listAllGoods();
        checkFavStatus();
        // Picasso.get().load(imageURL).fit().into(ivGoods);
        Glide.with(getContext())
                .load(imageURL)
                .centerCrop()
                .placeholder(R.drawable.ic_loading_static)
                .dontAnimate()
                .into(ivGoods);
        favBtn = fragmentView.findViewById(R.id.favBtn);
        settingButton = fragmentView.findViewById(R.id.settingButton);
        ib_back = fragmentView.findViewById(R.id.ib_back);

        settingButton.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        favBtn.setOnClickListener(this);

        goodsFav = false;
        return fragmentView;
    }

    private void listAllGoods() {
        um = new UserModel();
        String userID = um.getUserIDFromDataBase();
        DatabaseReference goodsReference = database.getReference().child("user").child(userID).
                child("goods").child(goodsCategory).child(barcode);
        goodsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    tv_itemStatus.setVisibility(View.GONE);
                    listGoods.setVisibility(View.VISIBLE);
                } else {
                    tv_itemStatus.setVisibility(View.VISIBLE);
                    listGoods.setVisibility(View.GONE);

                }
                goodsArrayList.clear();

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String goodsID = snapshot.getKey();
                    String buyDate = (String) snapshot.child("insertDate").getValue();
                    String expirationDate = (String) snapshot.child("expirationDate").getValue();
                    String quantity = (String) snapshot.child("quantity").getValue();
                    String goodsLocation = (String) snapshot.child("goodsLocation").getValue();
                    Goods goods = snapshot.getValue(Goods.class);

                    goodsArrayList.add(goods);
                    Collections.sort(goodsArrayList, Goods.sortExpDateAsc);
                    adapter = new GoodsListAdapter(goodsArrayList, getContext());
                    listGoods.setAdapter(adapter);

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
                    favBtn.setImageResource(R.drawable.ic_favorite_red_24dp);
                    goodsFav = true;
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
        // Toast.makeText(getContext(), goodsCategory, Toast.LENGTH_LONG).show();
        if (goodsFav) {//task to do:remove from fav list
            goodsFavReff.child(barcode).removeValue();
            favBtn.setImageResource(R.drawable.ic_favorite_white_24dp);
            goodsFav = false;
            Toast.makeText(getContext(), "Item remove from favorite list!", Toast.LENGTH_LONG).show();
        } else {
            //add to fav list
            goodsFavReff.child(barcode).setValue(goodsCategory);
            favBtn.setImageResource(R.drawable.ic_favorite_red_24dp);
            Toast.makeText(getContext(), "Item added to favorite list!", Toast.LENGTH_LONG).show();
            goodsFav = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                fragmentHandler.prevFragment(getContext());
                break;
            case R.id.favBtn:
                changeFavStatus();

                break;
            case R.id.settingButton:
                openSetting();

                break;


        }
    }

    private void openSetting() {
        String barcode = getArguments().getString("barcode");
        String goodsName = getArguments().getString("goodsName");

        ItemSettingFragment itemSettingFragment = new ItemSettingFragment();
        Bundle cate = new Bundle();
        cate.putString("barcode", barcode);
        cate.putString("goodsName", goodsName);

        itemSettingFragment.setArguments(cate);
        FragmentHandler fh = new FragmentHandler();
        fh.loadFragment(itemSettingFragment, getContext());
    }

}
