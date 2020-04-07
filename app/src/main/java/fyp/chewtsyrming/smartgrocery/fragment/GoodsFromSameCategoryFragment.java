package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

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

import fyp.chewtsyrming.smartgrocery.FragmentHandler;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListGridAdapter;
import fyp.chewtsyrming.smartgrocery.object.GoodsGrid;

public class GoodsFromSameCategoryFragment extends Fragment {

    GoodsListGridAdapter goodsListGridAdapter;
    private ArrayList<GoodsGrid> goodsCategoryList;
    private GridView gridView;
    private FirebaseUser user;
    private String userId, goodsCategory, imageURL, goodsName, category, processType;
    private SearchView searchView1;
    private TextView tvCategoryTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_goods_from_same_category, null);
        gridView = fragmentView.findViewById(R.id.gridviewGoodsList);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        tvCategoryTitle = fragmentView.findViewById(R.id.tvCategoryTitle);
        searchView1 = fragmentView.findViewById(R.id.searchView1);
        goodsCategoryList = new ArrayList<>();
        goodsListGridAdapter = new GoodsListGridAdapter(getActivity(), goodsCategoryList);
        gridView.setAdapter(goodsListGridAdapter);
        Bundle cate = this.getArguments();
        goodsCategory = cate.getString("goodsCategory");
        processType = "view";
        if (cate.getString("processType") != null) processType = cate.getString("processType");
        //  Toast.makeText(getContext(), processType, Toast.LENGTH_LONG).show();

        tvCategoryTitle.setText(goodsCategory);
        //reference to db
        if (goodsCategory.equals("All Goods")) {
            getAllGoods();
        } else {
            listGoods();

        }


        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {

                }
                goodsListGridAdapter.filter(s);

                return false;
            }
        });

        return fragmentView;
    }

    public void listGoods() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference parentReference = database.getReference().child("user").child(userId).
                child("goods").child(goodsCategory);
        /*TODO change icon to image retrieve from firebase storage*/

        parentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String barcode = snapshot.getKey();


                    DatabaseReference goodDetailsReference = database.getReference().child("barcode").child(barcode);
                    goodDetailsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotChild) {

                            goodsName = snapshotChild.child("goodsName").getValue().toString();
                            imageURL = snapshotChild.child("imageURL").getValue().toString();
                            category = snapshotChild.child("goodsCategory").getValue().toString();
                            GoodsGrid goodsGrid = new GoodsGrid(goodsName, imageURL, barcode, category);
                            goodsCategoryList.add(goodsGrid);

                            //Toast.makeText(getContext(), processType, Toast.LENGTH_LONG).show();
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    GoodsGrid goodsGrid1 = goodsCategoryList.get(position);
                                    Bundle cate = new Bundle();
                                    cate.putString("barcode", goodsGrid1.getBarcode());
                                    cate.putString("goodsCategory", goodsGrid1.getCategory());
                                    cate.putString("imageURL", imageURL);
                                    cate.putString("goodsName", goodsName);
                                    GoodsFromSameGoodsFragment frag = new GoodsFromSameGoodsFragment();
                                    frag.setArguments(cate);
                                    FragmentHandler h = new FragmentHandler();
                                    h.loadFragment(frag, getContext());

                                }
                            });
                            goodsListGridAdapter.notifyDataSetChanged();
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

    private void getAllGoods() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference parentReference = database.getReference().child("user").child(userId).
                child("goods");
        /*TODO change icon to image retrieve from firebase storage*/

        parentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {

                for (final DataSnapshot dataSnapshot : ds.getChildren()) {
                    String cat = dataSnapshot.getKey();

                    if (cat != null && !cat.equals("fav")) {

                        if (cat != null && !cat.equals("recent")) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                final String barcode = snapshot.getKey();


                                DatabaseReference goodDetailsReference = database.getReference().child("barcode").child(barcode);
                                goodDetailsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshotChild) {

                                        goodsName = snapshotChild.child("goodsName").getValue().toString();
                                        imageURL = snapshotChild.child("imageURL").getValue().toString();
                                        category = snapshotChild.child("goodsCategory").getValue().toString();

                                        GoodsGrid goodsGrid = new GoodsGrid(goodsName, imageURL, barcode, category);
                                        goodsCategoryList.add(goodsGrid);

                                        if (processType.equals("view")) {
                                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    GoodsGrid goodsGrid1 = goodsCategoryList.get(position);
                                                    Bundle cate = new Bundle();
                                                    cate.putString("barcode", goodsGrid1.getBarcode());
                                                    cate.putString("goodsCategory", goodsGrid1.getCategory());
                                                    cate.putString("imageURL", imageURL);
                                                    cate.putString("goodsName", goodsName);
                                                    GoodsFromSameGoodsFragment frag = new GoodsFromSameGoodsFragment();
                                                    frag.setArguments(cate);
                                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.fragment_container, frag);
                                                    transaction.addToBackStack(null);
                                                    transaction.commit();
                                                }
                                            });
                                        } else if (processType.equals("shoppinglist")) {

                                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    GoodsGrid goodsGrid1 = goodsCategoryList.get(position);
                                                    String shoppingPlanID = getArguments().getString("shoppingPlanID");
                                                    String shoppingPlanName = getArguments().getString("shoppingPlanName");

                                                    Bundle cate = new Bundle();
                                                    cate.putString("barcode", goodsGrid1.getBarcode());
                                                    cate.putString("shoppingPlanID", shoppingPlanID);
                                                    cate.putString("shoppingPlanName", shoppingPlanName);


                                                    ViewItemsShoppingListFragment frag = new ViewItemsShoppingListFragment();
                                                    frag.setArguments(cate);
                                                    FragmentHandler h = new FragmentHandler();
                                                    h.loadFragment(frag, getContext());

                                                }
                                            });
                                        }
                                        goodsListGridAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }

                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
