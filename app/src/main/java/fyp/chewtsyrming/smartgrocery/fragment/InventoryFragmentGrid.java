package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsCategoryGridAdapter;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListGridAdapter;
import fyp.chewtsyrming.smartgrocery.object.GoodsCategoryGrid;
import fyp.chewtsyrming.smartgrocery.object.GoodsGrid;

public class InventoryFragmentGrid extends Fragment implements View.OnClickListener {

    @Nullable
    private ArrayList<GoodsCategoryGrid> categoryList = new ArrayList<>();
    private GridView gridView;
    private LinearLayout favInventory, myInventory, recentInventory;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    private DatabaseReference userReference;
    private ArrayList<GoodsGrid> goodsList, goodsFavList, recentGoodsList, filterGoodsList;

    private SearchView svGoods;
    private Boolean favFlag, recentFlag, myInventoryFlag;
    private GoodsCategoryGridAdapter categoryAdapter;
    private GoodsListGridAdapter goodsAdapter ;

    private ImageButton arrowRecent, arrowFav, arrowCategory;
    private LinearLayout llRecent;
    private Button sortRecentAscBtn, sortRecentDescBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userReference = database.getReference().child("user").child(userId);

        View fragmentView = inflater.inflate(R.layout.fragment__inventory, null);
        favFlag = false;
        recentFlag = false;
        myInventoryFlag = false;
        svGoods = fragmentView.findViewById(R.id.searchView1);
        gridView = fragmentView.findViewById(R.id.gridView);
        favInventory = fragmentView.findViewById(R.id.favInventory);
        myInventory = fragmentView.findViewById(R.id.myInventory);
        recentInventory = fragmentView.findViewById(R.id.recentInventory);
        favInventory.setOnClickListener(this);
        myInventory.setOnClickListener(this);
        recentInventory.setOnClickListener(this);

    /*    sortRecentAscBtn = fragmentView.findViewById(R.id.sortRecentAscBtn);
        sortRecentDescBtn = fragmentView.findViewById(R.id.sortRecentDescBtn);

        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//original is (this), fragment need to use getActivity
        gridView = fragmentView.findViewById(R.id.gridView);
        gridViewFav = fragmentView.findViewById(R.id.gridViewFav);
        gridViewRecent = fragmentView.findViewById(R.id.gridViewRecent);
        TextView tvFav = fragmentView.findViewById(R.id.tvFavorite);
        TextView tvInventory = fragmentView.findViewById(R.id.tvMyInventory);
        TextView tvRecentAdd = fragmentView.findViewById(R.id.tvRecentlyAdded);
        arrowRecent = fragmentView.findViewById(R.id.arrowRecent);
        arrowFav = fragmentView.findViewById(R.id.arrowFav);
        arrowCategory = fragmentView.findViewById(R.id.arrowCategory);
        llRecent = fragmentView.findViewById(R.id.llRecent);*/
        //list all category based on user db
        goodsList = new ArrayList<>();
        filterGoodsList = new ArrayList<>();
        goodsFavList = new ArrayList<>();
        recentGoodsList = new ArrayList<>();
        // searchGoods();

        //retrieve all user goods to be use in searchview
        // listAllGoods();
       /* tvFav.setOnClickListener(this);
        tvInventory.setOnClickListener(this);
        sortRecentAscBtn.setOnClickListener(this);
        sortRecentDescBtn.setOnClickListener(this);
        llRecent.setOnClickListener(this);*/
        return fragmentView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.favInventory:
                listFavGoods();

                break;
            case R.id.myInventory:

                listAllCategory();
                break;
            case R.id.recentInventory:

                listRecentGoods();
                break;
            /*
            case R.id.sortRecentAscBtn:
                if (!recentGoodsList.isEmpty()){
                    Collections.sort(recentGoodsList, GoodsGrid.sortNameAZ);
                    goodsRecentAdapter.notifyDataSetChanged();
                }

                break;case R.id.sortRecentDescBtn:
                if (!recentGoodsList.isEmpty()){
                    Collections.sort(recentGoodsList, Collections.<GoodsGrid>reverseOrder(GoodsGrid.sortNameAZ));
                    goodsRecentAdapter.notifyDataSetChanged();
                }

                break;
            case R.id.llRecent:
                if (recentFlag) {
                    if (!recentGoodsList.isEmpty()) {
                        recentGoodsList.clear();
                        goodsRecentAdapter.notifyDataSetChanged();
                    }


                    arrowRecent.animate().rotation(360);
                    recentFlag = false;
                } else {
                    listRecentGoods();
                    arrowRecent.animate().rotation(180);
                    recentFlag = true;
                }

                break;
            case R.id.tvFavorite:
                if (favFlag) {//fav list is loaded. task to do is clear list
                    if (!goodsFavList.isEmpty()) {
                        goodsFavList.clear();

                        goodsFavAdapter.notifyDataSetChanged();
                    }

                  *//*  goodsFavAdapter = new GoodsListGridAdapter(getActivity(), goodsFavList);
                    gridViewFav.setAdapter(goodsFavAdapter);*//*
                    arrowFav.animate().rotation(360);
                    favFlag = false;
                } else {//fav list is empty. task to do: load the fav list
                    listFavGoods();
                    favFlag = true;
                }
                break;
            case R.id.tvMyInventory:
                if (categoryFlag) {//inv list is loaded. task to do is clear list
                    if (!categoryList.isEmpty()) {
                        categoryList.clear();
                        categoryAdapter.notifyDataSetChanged();
                    }
                    arrowCategory.animate().rotation(360);
                    categoryFlag = false;
                } else {//fav list is empty. task to do: load the fav list
                    listAllCategory();
                    categoryFlag = true;

                }

                break;*/
            default:
                break;
        }

    }

    private void listAllCategory() {
        // Reference to your entire Firebase database
        final String userId = user.getUid();
        DatabaseReference categoryReff;
        categoryReff = userReference.child("goods");
        recentGoodsList.clear();
        goodsList.clear();
        goodsFavList.clear();
        if(favFlag | recentFlag){
            goodsAdapter.notifyDataSetChanged();

        }

        categoryReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assert categoryList != null;
                categoryList.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final String ParentKey = snapshot.getKey();
                    if ((ParentKey != null && !ParentKey.equals("fav"))) {

                        if (!ParentKey.equals("recent")) {
                            int goodsCategoryIcon = R.drawable.ic_add_goods;
                            if (ParentKey.equals("Fruit & Vegetables")) {
                                goodsCategoryIcon = R.drawable.ic_category_vege_fruit;
                            } else if (ParentKey.equals("Grain Product")) {
                                goodsCategoryIcon = R.drawable.ic_category_vege_fruit;

                            }
                            GoodsCategoryGrid goodsCategory = new GoodsCategoryGrid(ParentKey, goodsCategoryIcon);
                            categoryList.add(goodsCategory);


                            categoryAdapter = new GoodsCategoryGridAdapter(getActivity(), categoryList);

                            gridView.setAdapter(categoryAdapter);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final GoodsCategoryGrid goodsCategoryGrid = categoryList.get(position);
                                    Bundle cate = new Bundle();
                                    cate.putString("goodsCategory", goodsCategoryGrid.getGoodsCategory());
                                    GoodsFromSameCategoryFragment frag = new GoodsFromSameCategoryFragment();
                                    frag.setArguments(cate);
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_container, frag);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        }


                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void listAllGoods() {
        final DatabaseReference allGoodsReff;
        allGoodsReff = userReference.child("goods");
        allGoodsReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                goodsList.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String category = snapshot.getKey();
                    if (!category.equals("fav")) {
                        final DatabaseReference categoryReference = allGoodsReff.child(category);
                        categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    long c = dataSnapshot.getChildrenCount();
                                    final String barcode = snapshot.getKey();
                                    DatabaseReference goodsReference = database.getReference().child("barcode").child(barcode);
                                    goodsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String goodsName = dataSnapshot.child("goodsName").getValue().toString();
                                            String imageURL = dataSnapshot.child("imageURL").getValue().toString();
                                            String goodsCategory = dataSnapshot.child("goodsCategory").getValue().toString();
                                            GoodsGrid goodsGrid = new GoodsGrid(goodsName, imageURL, barcode, goodsCategory);
                                            goodsList.add(goodsGrid);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
private void listGoods(String goodsType){

    DatabaseReference favReff;
    favReff = userReference.child("goods").child(goodsType);
    favReff.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                final String barcode = snapshot.getKey();
                String category = snapshot.getValue().toString();
                DatabaseReference favReference = database.getReference().child("barcode").child(barcode);
                favReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String goodsName = dataSnapshot.child("goodsName").getValue().toString();
                        final String imageURL = dataSnapshot.child("imageURL").getValue().toString();
                        //Toast.makeText(getContext(), goodsName, Toast.LENGTH_SHORT).show();
                        String goodsCategory = dataSnapshot.child("goodsCategory").getValue().toString();
                        GoodsGrid goodsFav = new GoodsGrid(goodsName, imageURL, barcode, goodsCategory);
                        goodsFavList.add(goodsFav);
                        goodsAdapter = new GoodsListGridAdapter(getActivity(), goodsFavList);
                        gridView.setAdapter(goodsAdapter);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                GoodsGrid goodsGrid1 = goodsFavList.get(position);
                                Bundle cate = new Bundle();
                                cate.putString("barcode", goodsGrid1.getBarcode());
                                cate.putString("goodsCategory", goodsGrid1.getCategory());
                                cate.putString("imageURL", goodsGrid1.getImageUrl());
                                cate.putString("goodsName", goodsGrid1.getName());
                                GoodsFromSameGoodsFragment frag = new GoodsFromSameGoodsFragment();
                                frag = new GoodsFromSameGoodsFragment();
                                frag.setArguments(cate);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, frag);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
    private void listFavGoods() {
        favFlag=true;
        recentGoodsList.clear();
        goodsList.clear();
        goodsFavList.clear();
        DatabaseReference favReff;
        favReff = userReference.child("goods").child("fav");
        favReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String barcode = snapshot.getKey();
                    String category = snapshot.getValue().toString();
                    DatabaseReference favReference = database.getReference().child("barcode").child(barcode);
                    favReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String goodsName = dataSnapshot.child("goodsName").getValue().toString();
                            final String imageURL = dataSnapshot.child("imageURL").getValue().toString();
                            //Toast.makeText(getContext(), goodsName, Toast.LENGTH_SHORT).show();
                            String goodsCategory = dataSnapshot.child("goodsCategory").getValue().toString();
                            GoodsGrid goodsFav = new GoodsGrid(goodsName, imageURL, barcode, goodsCategory);
                            goodsFavList.add(goodsFav);
                            goodsAdapter = new GoodsListGridAdapter(getActivity(), goodsFavList);
                            gridView.setAdapter(goodsAdapter);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    GoodsGrid goodsGrid1 = goodsFavList.get(position);
                                    Bundle cate = new Bundle();
                                    cate.putString("barcode", goodsGrid1.getBarcode());
                                    cate.putString("goodsCategory", goodsGrid1.getCategory());
                                    cate.putString("imageURL", goodsGrid1.getImageUrl());
                                    cate.putString("goodsName", goodsGrid1.getName());
                                    GoodsFromSameGoodsFragment frag = new GoodsFromSameGoodsFragment();
                                    frag = new GoodsFromSameGoodsFragment();
                                    frag.setArguments(cate);
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_container, frag);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void listRecentGoods() {
        recentFlag=true;
        recentGoodsList.clear();
        goodsList.clear();
        goodsFavList.clear();

        DatabaseReference recentReff = userReference.child("goods").child("recent");
        recentReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String barcode = String.valueOf(snapshot.child("barcode").getValue());
                    String goodsID = String.valueOf(snapshot.child("goodsID").getValue());
                    //  Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();

                    DatabaseReference barcodeReff = database.getReference().child("barcode").child(barcode);
                    barcodeReff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String goodsCategory, goodsName, imageURL;

                            goodsCategory = String.valueOf(dataSnapshot.child("goodsCategory").getValue());
                            goodsName = String.valueOf(dataSnapshot.child("goodsName").getValue());
                            imageURL = String.valueOf(dataSnapshot.child("imageURL").getValue());
                            GoodsGrid goodsGrid = new GoodsGrid(goodsName, imageURL, barcode, goodsCategory);
                            recentGoodsList.add(goodsGrid);
                            goodsAdapter = new GoodsListGridAdapter(getActivity(), recentGoodsList);
                            gridView.setAdapter(goodsAdapter);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    GoodsGrid goodsGrid1 = recentGoodsList.get(position);
                                    Bundle cate = new Bundle();
                                    cate.putString("barcode", goodsGrid1.getBarcode());
                                    cate.putString("goodsCategory", goodsGrid1.getCategory());
                                    cate.putString("imageURL", goodsGrid1.getImageUrl());
                                    cate.putString("goodsName", goodsGrid1.getName());
                                    GoodsFromSameGoodsFragment frag = new GoodsFromSameGoodsFragment();
                                    frag.setArguments(cate);
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_container, frag);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    //Toast.makeText(getContext(), goodsGrid1.getBarcode(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchGoods() {
        svGoods.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if (newText.isEmpty()) {
                    listAllCategory();
                } else {
                    listAllGoods();

                    filterGoodsList.clear();
                    for (int x = 0; x < goodsList.size(); x++) {
                        if (goodsList.get(x).getName().contains(newText)) {
                            filterGoodsList.add(goodsList.get(x));
                        }

                    }
                    goodsAdapter = new GoodsListGridAdapter(getActivity(), filterGoodsList);

                    gridView.setAdapter(goodsAdapter);
                }

                return false;
            }
        });
    }
}
