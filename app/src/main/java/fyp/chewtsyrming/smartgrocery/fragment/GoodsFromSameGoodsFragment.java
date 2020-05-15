package fyp.chewtsyrming.smartgrocery.fragment;

import android.app.Activity;
import android.content.Intent;
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
import androidx.fragment.app.FragmentManager;

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
    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;
    private static final String DATA_TYPE = "DATA_TYPE";
    private static final String ORDER_TYPE = "ORDER_TYPE";
    private static GoodsListAdapter adapter;
    private ArrayList<GoodsList> goodsListArrayList;
    private ArrayList<Goods> goodsArrayList;
    private ListView listGoods;
    private TextView tvGoodsName, tv_itemStatus;
    private String barcode, goodsCategory, goodsID, imageURL, goodsName;
    private UserModel um;
    private ImageView ivGoods;
    private ImageButton favBtn, settingButton, ib_back, sortBtn;
    private Boolean goodsFav;
    private FirebaseDatabase database;
    private FragmentHandler fragmentHandler;

    public static Intent newIntent(String dataType, String orderChoice) {
        Intent intent = new Intent();
        intent.putExtra(DATA_TYPE, dataType);
        intent.putExtra(ORDER_TYPE, orderChoice);
        return intent;
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
                    if (getContext() != null) {
                        adapter = new GoodsListAdapter(goodsArrayList, getContext());
                        listGoods.setAdapter(adapter);
                    }


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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_goods_from_same_goods, null);
        listGoods = fragmentView.findViewById(R.id.listGoods);
        ivGoods = fragmentView.findViewById(R.id.ivGoods);
        tvGoodsName = fragmentView.findViewById(R.id.tvGoodsName);
        tv_itemStatus = fragmentView.findViewById(R.id.tv_itemStatus);

        database = FirebaseDatabase.getInstance();

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
        sortBtn = fragmentView.findViewById(R.id.sortBtn);

        settingButton.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        favBtn.setOnClickListener(this);
        sortBtn.setOnClickListener(this);

        goodsFav = false;
        return fragmentView;
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

    protected void showDialog() {
        final FragmentManager fm = (getActivity()).getSupportFragmentManager();

        SortDialogFragment dialogFragment = SortDialogFragment.newInstance();
        dialogFragment.setTargetFragment(GoodsFromSameGoodsFragment.this, TARGET_FRAGMENT_REQUEST_CODE);
        dialogFragment.show(fm, "Sort");
    }

    private void openSetting() {
        String barcode = getArguments().getString("barcode");
        String goodsName = getArguments().getString("goodsName");

        ItemSettingFragment itemSettingFragment = new ItemSettingFragment();
        Bundle cate = new Bundle();
        cate.putString("barcode", barcode);
        cate.putString("goodsName", goodsName);

        itemSettingFragment.setArguments(cate);
        FragmentHandler.loadFragment(itemSettingFragment, getContext());
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
            case R.id.sortBtn:
                if (goodsArrayList.isEmpty()) {
                    Toast.makeText(getContext(), "Nothing to filter", Toast.LENGTH_SHORT).show();
                } else {
                    showDialog();

                }
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == TARGET_FRAGMENT_REQUEST_CODE) {
            String dataType = data.getStringExtra(DATA_TYPE);
            String orderType = data.getStringExtra(ORDER_TYPE);
           // Toast.makeText(getContext(), dataType, Toast.LENGTH_SHORT).show();

            if (dataType.contains("expirationDate")) {

                if (orderType.contains("Ascending")) {
                    Collections.sort(goodsArrayList, Goods.sortExpDateAsc);

                } else {
                    Collections.sort(goodsArrayList, Goods.sortExpDateDesc);

                }
            } else if (dataType.contains("insertDate")) {
                if (orderType.contains("Ascending")) {
                    Collections.sort(goodsArrayList, Goods.sortInsertDateAsc);

                } else {
                    Collections.sort(goodsArrayList, Goods.sortInsertDateDesc);

                }

            } else if (dataType.contains("quantity")) {

                if (orderType.contains("Ascending")) {
                    Collections.sort(goodsArrayList, Goods.sortQuantityAsc);
                } else {
                    Collections.sort(goodsArrayList, Goods.sortQuantityDesc);
                }
            } else if (dataType.contains("location")) {

                if (orderType.contains("Ascending")) {
                    Collections.sort(goodsArrayList, Goods.sortGoodsLocationAsc);

                } else {
                    Collections.sort(goodsArrayList, Goods.sortGoodsLocationDesc);

                }
            }
            adapter.notifyDataSetChanged();

            //  Toast.makeText(getContext(),orderType, Toast.LENGTH_SHORT).show();

        }

    }
}
