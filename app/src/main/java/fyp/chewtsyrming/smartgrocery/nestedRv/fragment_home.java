package fyp.chewtsyrming.smartgrocery.nestedRv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.fragment.BarcodeReaderFragment;
import fyp.chewtsyrming.smartgrocery.fragmentHandler;

public class fragment_home extends Fragment implements View.OnClickListener {
    String name, imageUrl;
    Goods goods;
    int i = 0;
    FirebaseDatabase database;
    fragmentHandler h = new fragmentHandler();
    View view;
    ContentLoadingProgressBar pb_main;
    private List<Category> dataList;
    private List<Goods> goodsList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseUser user;
    private String userId;
    private Button ib_scan_barcode, ib_search_goods, ib_add_goods;
    private Button ib_all_item, ib_category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null);
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        dataList = new ArrayList<>();
        adapter = new CategoryAdapter(dataList, v.getContext());
        layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = v.findViewById(R.id.rv_main);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        recyclerView.setLayoutAnimation(animation);
        view = getActivity().findViewById(R.id.pb_main);
        if (view instanceof ContentLoadingProgressBar) {
            pb_main = (ContentLoadingProgressBar) view;
            //Do your stuff
        }

        ib_scan_barcode = v.findViewById(R.id.ib_scan_barcode);
        ib_search_goods = v.findViewById(R.id.ib_search_goods);
        ib_add_goods = v.findViewById(R.id.ib_add_goods);
        ib_all_item = v.findViewById(R.id.ib_all_item);
        ib_category = v.findViewById(R.id.ib_category);
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/smart-grocery-f41a7.appspot.com/o/goods%2FUntitled.png?alt=media&token=0acec7a9-c70f-49d0-94fe-b3666b9df7f9";
        ib_all_item.setOnClickListener(this);
        ib_category.setOnClickListener(this);
        ib_scan_barcode.setOnClickListener(this);
        ib_search_goods.setOnClickListener(this);
        ib_add_goods.setOnClickListener(this);
        getInventoryData();

        return v;
    }

    public void getAllData() {
        //consist of fifo, expiring soon, and reminder alert
        //fifo
        dataList.clear();

        adapter.notifyDataSetChanged();

        DatabaseReference databaseReference = database.getReference().child("user").child(userId).child("goods");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goodsList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();
                    String ParentKey = snapshot.getKey();

                    if (ParentKey != null && !ParentKey.equals("fav")) {

                        if (ParentKey != null && !ParentKey.equals("recent")) {

                            for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                final String barcode = snapshot2.getKey();
                                //Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();
                                goods = new Goods(barcode);
                                goodsList.add(goods);


                            }

                        }
                    }
                }
                Category category = new Category(goodsList, "All Goods", "tat");
                dataList.add(category);
                //Toast.makeText(getContext(), dataList.get(0).getGenre(), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getHomeData() {

        //consist of fifo, expiring soon, and reminder alert
        //fifo
        DatabaseReference databaseReference = database.getReference().child("user").child(userId).child("goods");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goodsList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();
                    String ParentKey = snapshot.getKey();

                    if (ParentKey != null && !ParentKey.equals("fav")) {

                        if (ParentKey != null && !ParentKey.equals("recent")) {

                            for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                String barcode = snapshot2.getKey();
                                //Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();


                                goods = new Goods(barcode, barcode, "as", barcode, imageUrl, "Unknown", "0");

                                goodsList.add(goods);
                            }

                        }
                    }
                }
                Category category = new Category(goodsList, "All Goods", "tat");
                dataList.add(category);
                //Toast.makeText(getContext(), dataList.get(0).getGenre(), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getInventoryData() {

        dataList.clear();
        adapter.notifyDataSetChanged();

        DatabaseReference databaseReference = database.getReference().child("user").child(userId).child("goods");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pb_main.hide();
                if (dataSnapshot.hasChild("fav")) {
                    //Toast.makeText(getContext(), dataSnapshot.child("fav").getKey(), Toast.LENGTH_SHORT).show();

                    String ParentKey = dataSnapshot.child("fav").getKey();
                    goodsList = new ArrayList<>();


                    for (DataSnapshot snapshot2 : dataSnapshot.child("fav").getChildren()) {
                        String barcode = snapshot2.getKey();
                        String category = snapshot2.getValue().toString();
                        //Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();

                        if (dataSnapshot.hasChild(category)) {
                            if (dataSnapshot.child(category).hasChild(barcode)) {
                                goods = new Goods(barcode);
                                goodsList.add(goods);
                            }
                        }

                        // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();
                    }
                    if (!goodsList.isEmpty()) {
                        Category category = new Category(goodsList, ParentKey, "tat");
                        dataList.add(category);
                        //Toast.makeText(getContext(), dataList.get(0).getGenre(), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }


                }
                if (dataSnapshot.hasChild("recent")) {
                    //Toast.makeText(getContext(), dataSnapshot.child("fav").getKey(), Toast.LENGTH_SHORT).show();

                    String ParentKey = dataSnapshot.child("recent").getKey();
                    goodsList = new ArrayList<>();


                    for (DataSnapshot snapshot2 : dataSnapshot.child("recent").getChildren()) {
                        String barcode = snapshot2.getKey();
                        String category = snapshot2.child("goodsCategory").getValue(String.class);

                        String timeStamp = snapshot2.child("timeStamp").getValue(String.class);
                        //Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();
                        if (dataSnapshot.hasChild(category)) {
                            if (dataSnapshot.child(category).hasChild(barcode)) {
                                goods = new Goods(barcode, timeStamp);

                                goodsList.add(goods);
                            }
                        }

                        // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();


                    }
                    //  Toast.makeText(getContext(), String.valueOf(goodsList.size()), Toast.LENGTH_SHORT).show();

                    if (goodsList.size() > 1) {
                        Collections.sort(goodsList, Goods.sortRecentItem);

                    }
                    Category category = new Category(goodsList, ParentKey, "tat");
                    dataList.add(category);
                    //Toast.makeText(getContext(), dataList.get(0).getGenre(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();

                }
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {//loop all category
                    //Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();

                    String ParentKey = snapshot.getKey();
                    goodsList = new ArrayList<>();

                    if (ParentKey != null && !ParentKey.equals("fav")) {

                        if (ParentKey != null && !ParentKey.equals("recent")) {
                            for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                String barcode = snapshot2.getKey();

                                //Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();

                                goods = new Goods(barcode);

                                goodsList.add(goods);
                                // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();


                            }
                            Category category = new Category(goodsList, ParentKey, "tat");
                            dataList.add(category);
                            //Toast.makeText(getContext(), dataList.get(0).getGenre(), Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_search_goods:

                break;
            case R.id.ib_scan_barcode:
                scanBarcode();

                break;

            case R.id.ib_add_goods:

                addGoods();
                break;
            case R.id.ib_category:
                getInventoryData();

                break;
            case R.id.ib_all_item:

                getAllData();
                break;
        }
    }

    public void addGoods() {
        Bundle bundle = new Bundle();
        bundle.putString("message", "Add Goods");
        bundle.putString("code", "9002");//9002 indicate add goods
        Fragment fragment = null;
        fragment = new BarcodeReaderFragment();
        fragment.setArguments(bundle);
        h.loadFragment(fragment, getContext());
    }

    public void scanBarcode() {

        Bundle bundle = new Bundle();
        bundle.putString("message", "Search Goods with barcode");
        bundle.putString("code", "9001");//9001 indicate search goods
        Fragment fragment = new BarcodeReaderFragment();
        fragment.setArguments(bundle);
        h.loadFragment(fragment, getContext());
    }

}
