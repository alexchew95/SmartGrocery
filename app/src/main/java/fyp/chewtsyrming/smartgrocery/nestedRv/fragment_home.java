package fyp.chewtsyrming.smartgrocery.nestedRv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class fragment_home extends Fragment {
    private List<Category> dataList;
    private List<Goods> goodsList;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseUser user;
    private String userId;
    String name, imageUrl;
    Goods goods;
    int i = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null);
        dataList = new ArrayList<>();
        adapter = new CategoryAdapter(dataList, v.getContext());
        layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = v.findViewById(R.id.rv_main);
        progressBar = v.findViewById(R.id.pb_home);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getInventoryData();
        return v;
    }

    public void getInventoryData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("user").child(userId).child("goods");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("fav")) {
                    //Toast.makeText(getContext(), dataSnapshot.child("fav").getKey(), Toast.LENGTH_SHORT).show();

                    String ParentKey = dataSnapshot.child("fav").getKey();
                    goodsList = new ArrayList<>();


                    for (DataSnapshot snapshot2 : dataSnapshot.child("fav").getChildren()) {
                        String barcode = snapshot2.getKey();
                        //Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();

                        goods = new Goods("a", barcode, "as", barcode, "hfghf");

                        goodsList.add(goods);
                        // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();


                    }
                    progressBar.setVisibility(View.GONE);

                    Category category = new Category(goodsList, ParentKey, "tat");
                    dataList.add(category);
                    //Toast.makeText(getContext(), dataList.get(0).getGenre(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();

                }
                if (dataSnapshot.hasChild("recent")) {
                    //Toast.makeText(getContext(), dataSnapshot.child("fav").getKey(), Toast.LENGTH_SHORT).show();

                    String ParentKey = dataSnapshot.child("recent").getKey();
                    goodsList = new ArrayList<>();


                    for (DataSnapshot snapshot2 : dataSnapshot.child("recent").getChildren()) {
                        String barcode = snapshot2.getKey();
                        //Toast.makeText(getContext(), barcode, Toast.LENGTH_SHORT).show();

                        goods = new Goods("a", barcode, "as", barcode, "hfghf");

                        goodsList.add(goods);
                        // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();


                    }
                    progressBar.setVisibility(View.GONE);

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

                                goods = new Goods("a", barcode, "as", barcode, "hfghf");

                                goodsList.add(goods);
                                // Toast.makeText(getContext(), snapshot2.getKey(), Toast.LENGTH_SHORT).show();


                            }
                            progressBar.setVisibility(View.GONE);

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
}
