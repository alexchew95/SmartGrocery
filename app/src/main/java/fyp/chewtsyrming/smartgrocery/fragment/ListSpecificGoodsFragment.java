package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.object.Goods;
import fyp.chewtsyrming.smartgrocery.object.GoodsCategory;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.object.SubGoods;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsListViewAdapter;

public class ListSpecificGoodsFragment extends Fragment {

    GoodsListViewAdapter adapter;
    List<Goods> goodsCategoryList = new ArrayList<>();
    List<SubGoods> subGoods;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View fragmentView = inflater.inflate(R.layout.fragment_list_specific_goods, null);

        final RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_list_specific_goods);// normal activity not require view.
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//original is (this), fragment need to use getActivity
        Bundle cate = this.getArguments();
        final String goodsCategory = cate.getString("Key");
        /*if(cate != null){

            Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();

        }*/

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Reference to your entire Firebase database
        DatabaseReference parentReference = database.getReference().child("user").child("e1yf7z22Vpcq1XHz1c5O1jhVX1C3").child("goods").child(goodsCategory);
        parentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<GoodsCategory> Parent = new ArrayList<>();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //  Toast.makeText(getContext(), snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();

                    final String test = snapshot.child("goodsName").getValue().toString();
                    //Goods g = snapshot1.getValue(Goods.class);
                    //  String key = snapshot.getValue().toString();
                    // Toast.makeText(getContext(), test, Toast.LENGTH_LONG).show();
                    final String ParentKey = snapshot.getKey();


                    DatabaseReference childReference = database.getReference().
                            child("user").child("e1yf7z22Vpcq1XHz1c5O1jhVX1C3").child("goods").child(goodsCategory).
                            child(ParentKey).child("masterExpirationQuantity");

                    childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            subGoods=  new ArrayList<>();

                            for (final DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                                DatabaseReference subChildReference = database.getReference().
                                        child("user").child("e1yf7z22Vpcq1XHz1c5O1jhVX1C3").child("goods").child(goodsCategory).
                                        child(ParentKey).child("masterExpirationQuantity").child(snapshot1.getKey());
                                String test = snapshot1.child("expirationDate").getValue().toString();
                                Toast.makeText(getContext(),test,  Toast.LENGTH_SHORT).show();
                                SubGoods ss = new SubGoods(snapshot1.child("expirationDate").getValue().toString(),snapshot1.child("expirationDate").getValue().toString());
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
