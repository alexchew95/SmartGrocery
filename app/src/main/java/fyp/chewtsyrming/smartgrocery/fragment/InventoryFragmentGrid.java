package fyp.chewtsyrming.smartgrocery.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fyp.chewtsyrming.smartgrocery.object.Goods;
import fyp.chewtsyrming.smartgrocery.object.GoodsCategoryGrid;
import fyp.chewtsyrming.smartgrocery.R;
import fyp.chewtsyrming.smartgrocery.adapter.GoodsCategoryGridAdapter;

public class InventoryFragmentGrid extends Fragment {

    @Nullable
    FirebaseAuth.AuthStateListener aSL;
    DatabaseReference reff;
    List<Goods> events = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    List<Goods> goodsList;
    List<GoodsCategoryGrid> goodsCategoryList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_grid_inventory, null);
        TextView tv = (TextView) fragmentView;
        inflater.inflate(R.layout.fragment_grid_inventory, null);
        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//original is (this), fragment need to use getActivity

        final GridView gridView = fragmentView.findViewById(R.id.gridviewInventory);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Reference to your entire Firebase database
        DatabaseReference parentReference = database.getReference().child("user").child("e1yf7z22Vpcq1XHz1c5O1jhVX1C3").child("goods");


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
                            ListSpecificGoodsFragment testFrag = new ListSpecificGoodsFragment();
                            testFrag.setArguments(cate);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, testFrag);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });

                    //  Toast.makeText(getContext(), ParentKey, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab_add_goods);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Fragment newCase=new AddGoodsBarcodeReaderFragment();
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,newCase); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();

            }
        });

        return fragmentView;

    }

}
