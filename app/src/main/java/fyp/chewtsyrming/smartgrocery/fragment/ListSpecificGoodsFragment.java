package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;

import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListViewAdapter;
import fyp.chewtsyrming.smartgrocery.object.Goods;
import fyp.chewtsyrming.smartgrocery.object.SubGoods;

public class ListSpecificGoodsFragment extends Fragment {

    GoodsListViewAdapter adapter;
    List<Goods> goodsCategoryList;
    List<SubGoods> subGoods;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View fragmentView = inflater.inflate(R.layout.fragment_list_specific_goods, null);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        final RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_list_specific_goods);// normal activity not require view.
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//original is (this), fragment need to use getActivity
        Bundle cate = this.getArguments();
        final String goodsCategory = cate.getString("Key");
        /*TODO convert to gridview. add button then enable select like checkbox*/

        /*if(cate != null){

            Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();

        }*/

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Reference to your entire Firebase database
        DatabaseReference parentReference = database.getReference().child("user").child(userId).
                child("goods").child(goodsCategory);
        parentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //  Toast.makeText(getContext(), snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();

                    final String test = snapshot.child("goodsName").getValue().toString();
                    //Goods g = snapshot1.getValue(Goods.class);
                    //  String key = snapshot.getValue().toString();
                    // Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();
                    final String ParentKey = snapshot.getKey();


                    DatabaseReference childReference = database.getReference().
                            child("user").child(userId).child("goods").child(goodsCategory).
                            child(ParentKey).child("masterExpirationQuantity");

                    childReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            subGoods = new ArrayList<>();
                            String barcode, category, expirationDate, masterExpirationQuantityID, quantity;
                            for (final DataSnapshot snapshot1 : dataSnapshot.getChildren()) {



//                                Toast.makeText(getContext(), test, Toast.LENGTH_SHORT).show();
                                String goodsName = snapshot1.child("barcode").getValue().toString();
                                barcode = snapshot1.child("barcode").getValue().toString();
                                category = snapshot1.child("category").getValue().toString();
                                expirationDate = snapshot1.child("expirationDate").getValue().toString();
                                masterExpirationQuantityID = snapshot1.child("masterExpirationQuantityID").getValue().toString();
                                quantity = snapshot1.child("quantity").getValue().toString();
                                SubGoods ss = new SubGoods(barcode,category,expirationDate,masterExpirationQuantityID,quantity,goodsName);
                                subGoods.add(ss);
                                /* subChildReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        subGoods=  new ArrayList<>();
                                        for (final DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                            SubGoods ss = dataSnapshot.getValue(SubGoods.class);

                                                subGoods.add(ss);
                                            Toast.makeText(getContext(),snapshot2.getKey(),  Toast.LENGTH_SHORT).show();

                                        }
                                        Goods goodsCategory2 = new Goods(test, subGoods, R.drawable.ic_home_garden);
                                        goodsCategoryList.add(goodsCategory2);
                                        adapter = new GoodsListViewAdapter(goodsCategoryList);
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });*/
                               /*subGoods=new ArrayList<>();
                                                             String abc = snapshot1.getValue().toString();
                                SubGoods ss = snapshot1.getValue(SubGoods.class);
                                subGoods.add(ss);
                                Long sasa=snapshot1.getChildrenCount();
                                //Toast.makeText(getContext(),sasa.toString(),  Toast.LENGTH_LONG).show();
                                Toast.makeText(getContext(),snapshot1.getKey(),  Toast.LENGTH_LONG).show();*/
                            }
                            Goods goodsCategory2 = new Goods(test, subGoods, R.drawable.ic_home_garden);
                            goodsCategoryList = new ArrayList<>();
                            goodsCategoryList.add(goodsCategory2);
                            adapter = new GoodsListViewAdapter(goodsCategoryList);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //  Toast.makeText(getContext(), ParentKey, Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return fragmentView;
    }

}
