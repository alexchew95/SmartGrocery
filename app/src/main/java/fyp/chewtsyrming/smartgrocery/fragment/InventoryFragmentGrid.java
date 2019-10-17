package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsCategoryGridAdapter;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListGridAdapter;
import fyp.chewtsyrming.smartgrocery.object.GoodsCategoryGrid;
import fyp.chewtsyrming.smartgrocery.object.GoodsGrid;

public class InventoryFragmentGrid extends Fragment implements View.OnClickListener {

    @Nullable
    List<GoodsCategoryGrid> categoryList = new ArrayList<>();
    GridView gridView, gridViewFav, gridViewRecent;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    DatabaseReference parentReference;
    List<GoodsGrid> goodsList = new ArrayList<>();
    List<GoodsGrid> filterGoodsList = new ArrayList<>();
    List<GoodsGrid> goodsFavList = new ArrayList<>();
    ListGoodsFragmentGrid listGoodsFragmentGrid;
    SearchView svGoods;
    Boolean favFlag, recentFlag, categoryFlag;
    TextView tvFav, tvInventory, tvRecentAdd;
    GoodsCategoryGridAdapter goodsAdapter;
    GoodsListGridAdapter goodsFavAdapter;
    ImageButton arrowRecent, arrowFav, arrowCategory;
    LinearLayout llRecent;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_grid_inventory, null);
        favFlag = true;
        recentFlag = true;
        categoryFlag = false;

        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//original is (this), fragment need to use getActivity

        gridView = fragmentView.findViewById(R.id.gridviewInventory);
        gridViewFav = fragmentView.findViewById(R.id.gridViewFav);
        svGoods = fragmentView.findViewById(R.id.searchView1);
        tvFav = fragmentView.findViewById(R.id.tvFavorite);
        tvInventory = fragmentView.findViewById(R.id.tvMyInventory);
        tvRecentAdd = fragmentView.findViewById(R.id.tvRecentlyAdded);
        arrowRecent = fragmentView.findViewById(R.id.arrowRecent);
        arrowFav = fragmentView.findViewById(R.id.arrowFav);
        arrowCategory = fragmentView.findViewById(R.id.arrowCategory);
        llRecent = fragmentView.findViewById(R.id.llRecent);
        //list all category based on user db

        searchGoods();
        listFavGoods();
        //retrieve all user goods to be use in searchview
        // listAllGoods();
        tvFav.setOnClickListener(this);
        tvInventory.setOnClickListener(this);

        llRecent.setOnClickListener(this);
        return fragmentView;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llRecent:
                if (recentFlag) {
                    arrowRecent.animate().rotation(360);
                    recentFlag = false;
                } else {
                    arrowRecent.animate().rotation(180);
                    recentFlag = true;
                }

                break;
            case R.id.tvFavorite:
                if (favFlag) {//fav list is loaded. task to do is clear list
                    goodsFavList.clear();
                    goodsFavAdapter = new GoodsListGridAdapter(getActivity(), goodsFavList);
                    gridViewFav.setAdapter(goodsFavAdapter);
                    arrowFav.animate().rotation(360);
                    favFlag = false;
                } else {//fav list is empty. task to do: load the fav list
                    listFavGoods();
                    favFlag = true;
                }
                break;
            case R.id.tvMyInventory:
                if (categoryFlag) {//inv list is loaded. task to do is clear list
                    categoryList.clear();
                    goodsAdapter = new GoodsCategoryGridAdapter(getActivity(), categoryList);
                    gridView.setAdapter(goodsAdapter);
                    arrowCategory.animate().rotation(360);
                    categoryFlag = false;
                } else {//fav list is empty. task to do: load the fav list
                    listAllCategory();
                    categoryFlag = true;

                }

                break;
            default:
                break;
        }

    }

    private void listAllCategory() {
        // Reference to your entire Firebase database
        final String userId = user.getUid();
        parentReference = database.getReference().child("user").child(userId).child("goods");
        arrowCategory.animate().rotation(180);


        parentReference.addValueEventListener(new ValueEventListener() {
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
                                goodsCategoryIcon = R.drawable.ic_vege_fruit;
                            } else if (ParentKey.equals("Grain Product")) {
                                goodsCategoryIcon = R.drawable.ic_grains_products;

                            }
                            GoodsCategoryGrid goodsCategory = new GoodsCategoryGrid(ParentKey, goodsCategoryIcon);
                            categoryList.add(goodsCategory);


                            goodsAdapter = new GoodsCategoryGridAdapter(getActivity(), categoryList);

                            gridView.setAdapter(goodsAdapter);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final GoodsCategoryGrid book = categoryList.get(position);
                                    Bundle cate = new Bundle();
                                    cate.putString("Key", book.getName());
                                    ListGoodsFragmentGrid frag = new ListGoodsFragmentGrid();
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
        parentReference = database.getReference().child("user").child(userId).child("goods");
        parentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                goodsList.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String category = snapshot.getKey();
                    if (!category.equals("fav")) {
                        final DatabaseReference categoryReference = parentReference.child(category);
                        categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    long c = dataSnapshot.getChildrenCount();
                                    String barcode = snapshot.getKey();
                                    DatabaseReference goodsReference = database.getReference().child("barcode").child(barcode);
                                    goodsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String goodsName = dataSnapshot.child("goodsName").getValue().toString();
                                            String imageURL = dataSnapshot.child("imageURL").getValue().toString();
                                            GoodsGrid goodsGrid = new GoodsGrid(goodsName, imageURL);
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

    private void listFavGoods() {
        goodsFavList.clear();
        arrowFav.animate().rotation(180);

        parentReference = database.getReference().child("user").child(userId).child("goods").child("fav");
        parentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String barcode = snapshot.getKey();
                    String category = snapshot.getValue().toString();
                    DatabaseReference favReference = database.getReference().child("barcode").child(barcode);
                    favReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String goodsName = dataSnapshot.child("goodsName").getValue().toString();
                            String imageURL = dataSnapshot.child("imageURL").getValue().toString();
                            Toast.makeText(getContext(), goodsName, Toast.LENGTH_SHORT).show();
                            GoodsGrid goodsFav = new GoodsGrid(goodsName, imageURL);
                            goodsFavList.add(goodsFav);
                            goodsFavAdapter = new GoodsListGridAdapter(getActivity(), goodsFavList);
                            gridViewFav.setAdapter(goodsFavAdapter);

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
                    final GoodsListGridAdapter goodsAdapter = new GoodsListGridAdapter(getActivity(), filterGoodsList);

                    gridView.setAdapter(goodsAdapter);
                }

                return false;
            }
        });
    }
}
