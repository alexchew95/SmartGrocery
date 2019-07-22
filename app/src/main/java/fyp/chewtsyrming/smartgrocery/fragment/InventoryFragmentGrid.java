package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsCategoryGridAdapter;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListGridAdapter;
import fyp.chewtsyrming.smartgrocery.object.GoodsCategoryGrid;
import fyp.chewtsyrming.smartgrocery.object.GoodsGrid;

public class InventoryFragmentGrid extends Fragment {

    @Nullable
    List<GoodsCategoryGrid> goodsCategoryList = new ArrayList<>();
    GridView gridView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    DatabaseReference parentReference;
    List<GoodsGrid> goodsList = new ArrayList<>();
    List<GoodsGrid> filterGoodsList = new ArrayList<>();

    SearchView svGoods;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_grid_inventory, null);


        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//original is (this), fragment need to use getActivity

        gridView = fragmentView.findViewById(R.id.gridviewInventory);
        svGoods = fragmentView.findViewById(R.id.searchView1);
        CharSequence query = svGoods.getQuery();
        //list all category based on user db
        listAllCategory();
        searchGoods();
        //retrieve all user goods to be use in searchview
        // listAllGoods();



        return fragmentView;

    }

    private void listAllCategory() {
        // Reference to your entire Firebase database

        final String userId = user.getUid();
        parentReference = database.getReference().child("user").child(userId).child("goods");


        parentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                goodsCategoryList.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    final String ParentKey = snapshot.getKey();


                    int goodsCategoryIcon = R.drawable.ic_add_goods;
                    if (ParentKey.equals("Fruit & Vegetables")) {
                        goodsCategoryIcon = R.drawable.ic_vege_fruit;
                    } else if (ParentKey.equals("Grain Product")) {
                        goodsCategoryIcon = R.drawable.ic_grains_products;

                    }
                    GoodsCategoryGrid goodsCategory = new GoodsCategoryGrid(ParentKey, goodsCategoryIcon);
                    goodsCategoryList.add(goodsCategory);


                    final GoodsCategoryGridAdapter booksAdapter = new GoodsCategoryGridAdapter(getActivity(), goodsCategoryList);

                    gridView.setAdapter(booksAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final GoodsCategoryGrid book = goodsCategoryList.get(position);
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

                    //  Toast.makeText(getContext(), ParentKey, Toast.LENGTH_SHORT).show();
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
                    long count = dataSnapshot.getChildrenCount();
                    // Toast.makeText(getContext(), category, Toast.LENGTH_SHORT).show();
                    final DatabaseReference categoryReference = parentReference.child(category);
                    categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                long c = dataSnapshot.getChildrenCount();
                                String barcode = snapshot.getKey();
                                //Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();
                                DatabaseReference goodsReference = categoryReference.child(barcode).child("masterExpirationQuantity");
                                goodsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final int goodsCategoryIcon = R.drawable.ic_add_goods;
                                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String goodsName = snapshot.child("goodsName").getValue().toString();
                                            //Toast.makeText(getContext(), goodsName, Toast.LENGTH_SHORT).show();
                                            GoodsGrid goodsGrid = new GoodsGrid(goodsName, goodsCategoryIcon);
                                            goodsList.add(goodsGrid);
                                        }
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

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


                if(newText.isEmpty()){
                    listAllCategory();
                }
                else {   listAllGoods();
                    filterGoodsList.clear();
                    for (int x = 0; x < goodsList.size(); x++) {
                        if (goodsList.get(x).getName().contains(newText)) {
                            filterGoodsList.add(goodsList.get(x));
                        }

                    }
                    final GoodsListGridAdapter goodsAdapter = new GoodsListGridAdapter(getActivity(), filterGoodsList);

                    gridView.setAdapter(goodsAdapter);}

                return false;
            }
        });
    }
}
